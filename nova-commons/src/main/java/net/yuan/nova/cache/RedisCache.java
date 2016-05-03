package net.yuan.nova.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * 构件供shiro使用的缓存类，该类对springCache进行封装实现shiro的org.apache.shiro.cache.Cache<K, V>接口
 * 
 * @author Administrator
 *
 * @param <K>
 * @param <V>
 */
class RedisCache<K, V> implements BettleCache<K, V> {

	private static final Logger log = LoggerFactory.getLogger(RedisCache.class);

	private static final int PAGE_SIZE = 128;
	private final String name;
	private final int prefixLength;
	private final RedisTemplate<K, V> template;
	private final byte[] setName;

	private Cache springCache;

	@SuppressWarnings("unchecked")
	RedisCache(Cache springCache, byte[] prefix) {
		if (springCache == null) {
			throw new IllegalArgumentException("Cache argument cannot be null.");
		}
		this.springCache = springCache;
		this.name = springCache.getName();
		this.prefixLength = prefix == null ? 0 : prefix.length;
		// 取得RedisTemplate对象
		Object cache = springCache.getNativeCache();
		template = (RedisTemplate<K, V>) cache;

		StringRedisSerializer stringSerializer = new StringRedisSerializer();
		this.setName = stringSerializer.serialize(name + "~keys");
	}

	@Override
	public String getName() {
		return springCache.getName();
	}

	@SuppressWarnings("unchecked")
	@Override
	public V get(K key) throws CacheException {
		if (log.isTraceEnabled()) {
			log.trace("Getting object from cache [{}] for key [{}]", springCache.getName(), key);
		}
		try {
			if (key == null) {
				return null;
			} else {
				ValueWrapper value = springCache.get(key);
				if (value != null) {
					return (V) value.get();
				} else {
					if (log.isTraceEnabled()) {
						log.trace("Element for [{}] is null.", key);
					}
					return null;
				}
			}
		} catch (Throwable t) {
			throw new CacheException(t);
		}
	}

	/**
	 * Puts an object into the cache.
	 *
	 * @param key
	 *            the key.
	 * @param value
	 *            the value.
	 */
	public V put(K key, V value) throws CacheException {
		if (log.isTraceEnabled()) {
			log.trace("Putting object in cache [{}] for key [{}]", springCache.getName(), key);
		}
		try {
			V previous = get(key);
			springCache.put(key, value);
			return previous;
		} catch (Throwable t) {
			throw new CacheException(t);
		}
	}

	public V remove(K key) throws CacheException {
		if (log.isTraceEnabled()) {
			log.trace("Removing object from cache [{}] for key [{}]", springCache.getName(), key);
		}
		try {
			V previous = get(key);
			springCache.evict(key);
			return previous;
		} catch (Throwable t) {
			throw new CacheException(t);
		}
	}

	public void clear() throws CacheException {
		if (log.isTraceEnabled()) {
			log.trace("Clearing all objects from cache [{}]", springCache.getName());
		}
		try {
			springCache.clear();
		} catch (Throwable t) {
			throw new CacheException(t);
		}
	}

	@Override
	public int size() {
		Long obj = template.execute(new RedisCallback<Long>() {
			public Long doInRedis(RedisConnection connection) throws DataAccessException {
				Long zCount = connection.zCount(setName, 0, 1);
				return zCount;
			}
		}, true);
		return obj == null ? 0 : obj.intValue();
	}

	@Override
	public Set<K> keys() {

		Set<K> obj = template.execute(new RedisCallback<Set<K>>() {
			@SuppressWarnings("unchecked")
			public Set<K> doInRedis(RedisConnection connection) throws DataAccessException {

				Set<K> s = new LinkedHashSet<K>();
				RedisSerializer<?> keySerializer = template.getKeySerializer();

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
						s.add((K) value);
					}
				}
				return s;
			}
		}, true);
		return obj;
	}

	@Override
	public Set<K> keys(final int page, final int size) {

		Set<K> obj = template.execute(new RedisCallback<Set<K>>() {
			@SuppressWarnings("unchecked")
			public Set<K> doInRedis(RedisConnection connection) throws DataAccessException {

				Set<K> s = new LinkedHashSet<K>();
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
						s.add((K) value);
					}
				}
				return s;
			}
		}, true);
		return obj;
	}

	@Override
	public Collection<V> values() {

		Collection<V> obj = template.execute(new RedisCallback<Collection<V>>() {
			@SuppressWarnings("unchecked")
			public Collection<V> doInRedis(RedisConnection connection) throws DataAccessException {

				int offset = 0;
				boolean finished = false;
				Collection<V> c = new ArrayList<V>();
				RedisSerializer<?> valueSerializer = template.getValueSerializer();
				do {
					// need to paginate the keys
					Set<byte[]> keys = connection.zRange(setName, (offset) * PAGE_SIZE, (offset + 1) * PAGE_SIZE - 1);
					finished = keys.size() < PAGE_SIZE;
					offset++;
					if (!keys.isEmpty()) {
						for (byte[] key : keys) {
							byte[] bs = connection.get(key);
							if (bs != null) {
								Object value = valueSerializer != null ? valueSerializer.deserialize(bs) : bs;
								c.add((V) value);
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
	 * Returns &quot;RedisCache [&quot; + cache.getName() + &quot;]&quot;
	 *
	 * @return &quot;RedisCache [&quot; + cache.getName() + &quot;]&quot;
	 */
	public String toString() {
		return "RedisCache [" + springCache.getName() + "]";
	}

}
