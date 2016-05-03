package net.yuan.nova.cache;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import net.sf.ehcache.Ehcache;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.ehcache.EhCacheCache;
import org.springframework.data.redis.cache.DefaultRedisCachePrefix;
import org.springframework.data.redis.cache.RedisCachePrefix;

/**
 * 提供对Spring <code>{@link CacheManager}</code>封装，以利于更加友好的管理缓存数据
 * 
 * @author 0027005704
 *
 */
@SuppressWarnings("rawtypes")
public class SpringCacheManager implements BettleCacheManager {

	protected static final Logger log = LoggerFactory.getLogger(SpringCacheManager.class);

	private boolean usePrefix = true;
	private RedisCachePrefix cachePrefix = new DefaultRedisCachePrefix();
	/**
	 * The spring cache manager used by this implementation to create caches.
	 */
	private CacheManager cacheManager;
	/**
	 * Retains all Cache objects maintained by this cache manager.
	 */
	private final ConcurrentMap<String, BettleCache> caches;

	public SpringCacheManager() {
		this.caches = new ConcurrentHashMap<String, BettleCache>();
	}

	@Override
	public Collection<String> getCacheNames() {
		return cacheManager.getCacheNames();
	}

	@Override
	@SuppressWarnings("unchecked")
	public <K, V> BettleCache<K, V> getCache(String name) {

		if (StringUtils.isBlank(name)) {
			throw new IllegalArgumentException("Cache name cannot be null or empty.");
		}
		log.debug("Acquiring Cache instance named [{}]", name);

		BettleCache<K, V> cache = this.caches.get(name);
		if (cache == null) {
			cache = createCache(name);
			if (cache == null) {
				return null;
			}
			BettleCache<K, V> existing = caches.putIfAbsent(name, cache);
			if (existing != null) {
				cache = existing;
			}
		}
		return cache;
	}

	private <K, V> BettleCache<K, V> createCache(String name) {
		log.debug("Creating Cache instance named [{}]", name);
		Cache springCache = cacheManager.getCache(name);
		if (springCache != null) {
			if (springCache instanceof EhCacheCache) {
				// 基于Ehcache的实现
				net.sf.ehcache.Ehcache cache = (Ehcache) springCache.getNativeCache();
				return new EhCache<K, V>(cache);
			} else if (springCache instanceof ConcurrentMapCache) {
				// 基于Map的实现
				@SuppressWarnings("unchecked")
				Map<K, V> cache = (Map<K, V>) springCache.getNativeCache();
				return new MapCache<K, V>(name, cache);
			} else {
				// 基于redis的实现
				return createRedisCache(springCache, (usePrefix ? cachePrefix.prefix(name) : null));
			}
		} else {
			throw new CacheException("SpringCache is null.");
		}
	}

	/**
	 * 创建redis缓存
	 * 
	 * @param name
	 * @return
	 */
	protected <K, V> BettleCache<K, V> createRedisCache(Cache springCache, byte[] prefix) {
		return new RedisCache<K, V>(springCache, prefix);
	}

	public CacheManager getCacheManager() {
		return cacheManager;
	}

	/**
	 * 设置spring cache manager
	 *
	 * @param cacheManager
	 */
	public void setCacheManager(CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}

	public void setUsePrefix(boolean usePrefix) {
		this.usePrefix = usePrefix;
	}

	public void setCachePrefix(RedisCachePrefix cachePrefix) {
		this.cachePrefix = cachePrefix;
	}

	public void destroy() throws Exception {
		cacheManager = null;
		caches.clear();
	}

	public String toString() {
		Collection<BettleCache> values = caches.values();
		StringBuilder sb = new StringBuilder(getClass().getSimpleName()).append(" with ").append(caches.size())
				.append(" cache(s): [");
		int i = 0;
		for (BettleCache cache : values) {
			if (i > 0) {
				sb.append(", ");
			}
			sb.append(cache.toString());
			i++;
		}
		sb.append("]");
		return sb.toString();
	}

}
