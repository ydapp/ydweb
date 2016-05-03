package net.yuan.nova.cache;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * UbossRedisCache的管理器，用于管理和创建UbossRedisCache
 * 
 * @author Administrator
 *
 */

@SuppressWarnings("rawtypes")
public class UbossRedisCacheManager implements BettleCacheManager {

	private static final Logger log = LoggerFactory.getLogger(UbossRedisCacheManager.class);

	// 已创建的缓存
	private final ConcurrentMap<String, BettleCache> cacheMap = new ConcurrentHashMap<String, BettleCache>(16);
	// 已创建的缓存名称
	private Set<String> cacheNames = new LinkedHashSet<String>(16);

	private final RedisTemplate template;
	// 缓存名称是否使用前缀，默认为使用（true）
	private boolean usePrefix = true;
	// 缓存名称前缀分隔符，默认为“:”
	private final String delimiter;
	private RedisSerializer<String> serializer = new StringRedisSerializer();

	// 默认的缓存时间（单位：秒）
	private long defaultExpiration = 1800;
	// 预定义的各个缓存的缓存时间（单位：秒）
	private Map<String, Long> expires = null;

	public UbossRedisCacheManager(RedisTemplate template) {
		this(template, ":");
	}

	public UbossRedisCacheManager(RedisTemplate template, String delimiter) {
		this.template = template;
		this.delimiter = delimiter;
	}

	/**
	 * 根据缓存名称<code>name</code>获得缓存对象，如果传入的缓存名称没有对应的缓存存在将会创建一个新的缓存并返回
	 */
	@SuppressWarnings("unchecked")
	public <K, V> BettleCache<K, V> getCache(String name) {
		log.trace("Acquiring UbossRedisCache instance named [{}]", name);
		BettleCache<K, V> cache = this.cacheMap.get(name);
		if (cache == null) {
			return createAndAddCache(name);
		}
		return cache;
	}

	public void setUsePrefix(boolean usePrefix) {
		this.usePrefix = usePrefix;
	}

	public void setDefaultExpiration(long defaultExpireTime) {
		this.defaultExpiration = defaultExpireTime;
	}

	public void setExpires(Map<String, Long> expires) {
		this.expires = (expires != null ? new ConcurrentHashMap<String, Long>(expires) : null);
	}

	/**
	 * 获得所有缓存的名称
	 */
	public Collection<String> getCacheNames() {
		return Collections.unmodifiableSet(this.cacheNames);
	}

	private <K, V> BettleCache<K, V> createAndAddCache(String cacheName) {
		log.debug("Creating UbossRedisCache instance named [{}]", cacheName);
		BettleCache<K, V> cache = createCache(cacheName);
		this.cacheMap.put(cacheName, cache);
		this.cacheNames.add(cacheName);
		return cache;
	}

	@SuppressWarnings("unchecked")
	private <K, V> BettleCache<K, V> createCache(String cacheName) {
		long expiration = computeExpiration(cacheName);
		return (BettleCache<K, V>) new UbossRedisCache(cacheName, (usePrefix ? prefix(cacheName) : null), template,
				expiration);
	}

	/**
	 * 获得缓存数据的缓存时间
	 * 
	 * @param name
	 * @return
	 */
	private long computeExpiration(String name) {
		Long expiration = null;
		if (expires != null) {
			expiration = expires.get(name);
		}
		return (expiration != null ? expiration.longValue() : defaultExpiration);
	}

	/**
	 * 缓存名称前缀，即缓存名称加“:”
	 * 
	 * @param cacheName
	 * @return
	 */
	private byte[] prefix(String cacheName) {
		return serializer.serialize((delimiter != null ? cacheName.concat(delimiter) : cacheName.concat(":")));
	}

}
