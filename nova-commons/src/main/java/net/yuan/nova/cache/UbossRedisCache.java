package net.yuan.nova.cache;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.Assert;

/**
 * Redis缓存基本操作，在UBOSS框架中数据是被转成了JSON字符串后存入redis的，这里为了简化将类型统一定义为String
 * 
 * @author 0027005704
 *
 * @param <String>
 * @param <String>
 */

public class UbossRedisCache implements BettleCache<String, String> {

	private static final Logger log = LoggerFactory.getLogger(RedisCache.class);

	private static final int PAGE_SIZE = 128;
	private final String name;
	private final byte[] prefix;
	private final int prefixLength;
	private final RedisTemplate<String, Object> template;
	private final byte[] setName;

	private final byte[] cacheLockName;
	private long WAIT_FOR_LOCK = 300;
	private final long expiration;

	private RedisSerializer<String> stringSerializer = new StringRedisSerializer();

	private org.springframework.cache.Cache springCache;

	/**
	 * 构造方法
	 * 
	 * @param name
	 *            缓存名称
	 * @param prefix
	 *            前缀，通常情况下前缀为：缓存名称+“:”
	 * @param template
	 * @param expiration
	 *            缓存时间
	 */
	public UbossRedisCache(String name, byte[] prefix, RedisTemplate<String, Object> template, long expiration) {
		Assert.hasText(name, "缓存名称不能为空");
		this.name = name;
		this.template = template;
		this.prefix = prefix;
		this.prefixLength = prefix == null ? 0 : prefix.length;
		this.expiration = expiration;

		StringRedisSerializer stringSerializer = new StringRedisSerializer();

		// 设置的名称的键
		this.setName = stringSerializer.serialize(name + "~keys");
		this.cacheLockName = stringSerializer.serialize(name + "~lock");
	}

	/**
	 * 获得缓存名称
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * 获得缓存的底层实现
	 * 
	 * @return
	 */
	public Object getNativeCache() {
		return template;
	}

	public String get(final String key) {
		if (key == null) {
			return null;
		} else {
			Object value = template.execute(new RedisCallback<Object>() {

				public Object doInRedis(RedisConnection connection) throws DataAccessException {
					waitForLock(connection);
					byte[] bs = connection.get(computeKey(key));
					return stringSerializer.deserialize(bs);
				}
			}, true);
			if (value != null) {
				return (String) value;
			} else {
				if (log.isTraceEnabled()) {
					log.trace("Element for [{}] is null.", key);
				}
				return null;
			}
		}
	}

	/**
	 * @param key
	 * @param value
	 */
	public String put(String key, String value) {
		// 获得之前的值
		String previous = get(key);

		final byte[] keyBytes = computeKey(key);
		final byte[] valueBytes = stringSerializer.serialize(value);

		template.execute(new RedisCallback<Object>() {
			public Object doInRedis(RedisConnection connection) throws DataAccessException {

				waitForLock(connection);
				connection.multi();
				connection.set(keyBytes, valueBytes);
				// 插入数据时，同步保存key到set中，插入是分数都给了0
				connection.zAdd(setName, 0, keyBytes);
				if (expiration > 0) {
					connection.expire(keyBytes, expiration);
					// 更新到期时间
					connection.expire(setName, expiration);
				}
				connection.exec();
				return null;
			}
		}, true);

		return previous;
	}

	/**
	 * 从缓存中移除
	 * 
	 * @param key
	 * @return
	 */
	public String remove(String key) {
		String previous = get(key);

		final byte[] k = computeKey(key);

		template.execute(new RedisCallback<Object>() {
			public Object doInRedis(RedisConnection connection) throws DataAccessException {
				connection.del(k);
				// 从set中将key移除
				connection.zRem(setName, k);
				return null;
			}
		}, true);

		return previous;
	}

	/**
	 * 清空当前缓存
	 */
	public void clear() {

		template.execute(new RedisCallback<Object>() {
			public Object doInRedis(RedisConnection connection) throws DataAccessException {
				// 如果此时有其他清空则直接跳过
				if (connection.exists(cacheLockName)) {
					return null;
				}
				try {
					connection.set(cacheLockName, cacheLockName);
					int offset = 0;
					boolean finished = false;
					do {
						// 分页查找，也可使用zRangeByScore获得所有数据
						Set<byte[]> keys = connection.zRange(setName, (offset) * PAGE_SIZE, (offset + 1) * PAGE_SIZE
								- 1);
						finished = keys.size() < PAGE_SIZE;
						offset++;
						if (!keys.isEmpty()) {
							connection.del(keys.toArray(new byte[keys.size()][]));
						}
					} while (!finished);
					// 清空缓存时将对应的set删除
					connection.del(setName);
					return null;
				} finally {
					connection.del(cacheLockName);
				}
			}
		}, true);
	}

	/**
	 * 获得当前缓存中数据总数
	 * 
	 * @return
	 */
	public int size() {
		Long obj = template.execute(new RedisCallback<Long>() {
			public Long doInRedis(RedisConnection connection) throws DataAccessException {
				// 插入时给的是“0”，这里刚好可以取出全部
				Long zCount = connection.zCount(setName, 0, 1);
				return zCount;
			}
		}, true);
		return obj == null ? 0 : obj.intValue();
	}

	/**
	 * 获得缓存中所有的key，注意这里的key是不带前缀的
	 * 
	 * @return
	 */
	public Set<String> keys() {

		Set<String> obj = template.execute(new RedisCallback<Set<String>>() {
			public Set<String> doInRedis(RedisConnection connection) throws DataAccessException {

				Set<String> s = new LinkedHashSet<String>();
				RedisSerializer<?> keySerializer = template.getKeySerializer();
				// 插入时给的是“0”，这里刚好可以取出全部
				Set<byte[]> keys = connection.zRangeByScore(setName, 0, 1);
				if (!keys.isEmpty()) {
					for (byte[] keyBytes : keys) {
						// key的生成规则：【缓存名称+“:”+缓存的key值】使用StringRedisSerializer进行序列化
						byte[] result = keyBytes;
						if (prefixLength > 0) {
							result = new byte[keyBytes.length - prefixLength];
							System.arraycopy(keyBytes, prefixLength, result, 0, result.length);
						}
						Object value = keySerializer != null ? keySerializer.deserialize(result) : result;
						s.add((String) value);
					}
				}
				return s;
			}
		}, true);
		return obj;
	}

	/**
	 * 分页获得缓存中的key，注意这里的key是不带前缀的
	 * 
	 * @return
	 */
	@Override
	public Set<String> keys(final int page, final int size) {

		Set<String> obj = template.execute(new RedisCallback<Set<String>>() {
			public Set<String> doInRedis(RedisConnection connection) throws DataAccessException {

				Set<String> s = new LinkedHashSet<String>();
				RedisSerializer<?> keySerializer = template.getKeySerializer();

				Set<byte[]> keys = connection.zRange(setName, (page - 1) * size, page * size - 1);
				if (!keys.isEmpty()) {
					for (byte[] keyBytes : keys) {
						// key的生成规则：【缓存名称+“:”+缓存的key值】使用StringRedisSerializer进行序列化
						byte[] result = keyBytes;
						if (prefixLength > 0) {
							result = new byte[keyBytes.length - prefixLength];
							System.arraycopy(keyBytes, prefixLength, result, 0, result.length);
						}
						Object value = keySerializer != null ? keySerializer.deserialize(result) : result;
						s.add((String) value);
					}
				}
				return s;
			}
		}, true);
		return obj;
	}

	/**
	 * 获得缓存中所有的值
	 * 
	 * @return
	 */
	public Collection<String> values() {

		Collection<String> obj = template.execute(new RedisCallback<Collection<String>>() {
			public Collection<String> doInRedis(RedisConnection connection) throws DataAccessException {

				int offset = 0;
				boolean finished = false;
				Collection<String> c = new ArrayList<String>();
				do {
					// 这里使用了分页查询
					Set<byte[]> keys = connection.zRange(setName, (offset) * PAGE_SIZE, (offset + 1) * PAGE_SIZE - 1);
					finished = keys.size() < PAGE_SIZE;
					offset++;
					if (!keys.isEmpty()) {
						for (byte[] key : keys) {
							byte[] bs = connection.get(key);
							if (bs != null) {
								Object value = stringSerializer.deserialize(bs);
								c.add((String) value);
							}
						}
					}
				} while (!finished);
				return c;
			}
		}, true);
		return obj;
	}

	/**
	 * 计算并获得插入redis时实际用到的key
	 * 
	 * @param key
	 * @return
	 */
	private byte[] computeKey(String key) {
		byte[] keyBytes = stringSerializer.serialize(key);

		if (prefix == null || prefix.length == 0) {
			return keyBytes;
		}
		// 数组操作拼入前缀
		byte[] result = Arrays.copyOf(prefix, prefix.length + keyBytes.length);
		System.arraycopy(keyBytes, 0, result, prefix.length, keyBytes.length);

		return result;
	}

	/**
	 * 等待其他线程的读或写结束
	 * 
	 * @param connection
	 * @return
	 */
	private boolean waitForLock(RedisConnection connection) {

		boolean retry;
		boolean foundLock = false;
		do {
			retry = false;
			if (connection.exists(cacheLockName)) {
				foundLock = true;
				try {
					Thread.sleep(WAIT_FOR_LOCK);
				} catch (InterruptedException ex) {
					Thread.currentThread().interrupt();
				}
				retry = true;
			}
		} while (retry);

		return foundLock;
	}

	/**
	 *
	 * @return RedisCache [cache.getName()]
	 */
	public String toString() {
		return "RedisCache [" + springCache.getName() + "]";
	}

}
