package net.yuan.nova.cache.spring;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import net.yuan.nova.cache.CacheUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.transaction.AbstractTransactionSupportingCacheManager;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.cache.DefaultRedisCachePrefix;
import org.springframework.data.redis.cache.RedisCachePrefix;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

/**
 * <code>RedisCacheManager</code>是<code>{@link CacheManager}</code>的redis实现，抄至
 * <code>{@link org.springframework.data.redis.cache.RedisCacheManager}</code>
 * ，添加在启动时区分UBOSS数据缓存的功能
 * 
 * 
 * @author 0027005704
 *
 */
public class RedisCacheManager extends AbstractTransactionSupportingCacheManager {

	private final Log logger = LogFactory.getLog(RedisCacheManager.class);

	@SuppressWarnings("rawtypes")
	//
	private final RedisTemplate template;

	private boolean usePrefix = false;
	private RedisCachePrefix cachePrefix = new DefaultRedisCachePrefix();
	private boolean loadRemoteCachesOnStartup = false;
	private boolean dynamic = true;

	// 默认的缓存时间，0为不失效
	private long defaultExpiration = 1800;
	private Map<String, Long> expires = null;

	/**
	 * Construct a {@link RedisCacheManager}.
	 * 
	 * @param template
	 */
	@SuppressWarnings("rawtypes")
	public RedisCacheManager(RedisTemplate template) {
		this(template, Collections.<String> emptyList());
	}

	/**
	 * Construct a static {@link RedisCacheManager}, managing caches for the
	 * specified cache names only.
	 * 
	 * @param template
	 * @param cacheNames
	 * @since 1.2
	 */
	@SuppressWarnings("rawtypes")
	public RedisCacheManager(RedisTemplate template, Collection<String> cacheNames) {
		this.template = template;
		setCacheNames(cacheNames);
	}

	@Override
	public Cache getCache(String name) {
		Cache cache = super.getCache(name);
		if (cache == null && this.dynamic) {
			return createAndAddCache(name);
		}

		return cache;
	}

	/**
	 * Specify the set of cache names for this CacheManager's 'static' mode. <br/>
	 * The number of caches and their names will be fixed after a call to this
	 * method, with no creation of further cache regions at runtime.
	 */
	public void setCacheNames(Collection<String> cacheNames) {

		if (!CollectionUtils.isEmpty(cacheNames)) {
			for (String cacheName : cacheNames) {
				createAndAddCache(cacheName);
			}
			this.dynamic = false;
		}
	}

	public void setUsePrefix(boolean usePrefix) {
		this.usePrefix = usePrefix;
	}

	/**
	 * Sets the cachePrefix. Defaults to 'DefaultRedisCachePrefix').
	 * 
	 * @param cachePrefix
	 *            the cachePrefix to set
	 */
	public void setCachePrefix(RedisCachePrefix cachePrefix) {
		this.cachePrefix = cachePrefix;
	}

	/**
	 * Sets the default expire time (in seconds).
	 * 
	 * @param defaultExpireTime
	 *            time in seconds.
	 */
	public void setDefaultExpiration(long defaultExpireTime) {
		this.defaultExpiration = defaultExpireTime;
	}

	/**
	 * Sets the expire time (in seconds) for cache regions (by key).
	 * 
	 * @param expires
	 *            time in seconds
	 */
	public void setExpires(Map<String, Long> expires) {
		this.expires = (expires != null ? new ConcurrentHashMap<String, Long>(expires) : null);
	}

	/**
	 * If set to {@code true} {@link RedisCacheManager} will try to retrieve
	 * cache names from redis server using {@literal KEYS} command and
	 * initialize {@link RedisCache} for each of them.
	 * 
	 * @param loadRemoteCachesOnStartup
	 * @since 1.2
	 */
	public void setLoadRemoteCachesOnStartup(boolean loadRemoteCachesOnStartup) {
		this.loadRemoteCachesOnStartup = loadRemoteCachesOnStartup;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.cache.support.AbstractCacheManager#loadCaches()
	 */
	@Override
	protected Collection<? extends Cache> loadCaches() {

		Assert.notNull(this.template, "A redis template is required in order to interact with data store");
		return addConfiguredCachesIfNecessary(loadRemoteCachesOnStartup ? loadAndInitRemoteCaches() : Collections
				.<Cache> emptyList());
	}

	/**
	 * Returns a new {@link Collection} of {@link Cache} from the given caches
	 * collection and adds the configured {@link Cache}s of they are not already
	 * present.
	 * 
	 * @param caches
	 *            must not be {@literal null}
	 * @return
	 */
	private Collection<? extends Cache> addConfiguredCachesIfNecessary(Collection<? extends Cache> caches) {

		Assert.notNull(caches, "Caches must not be null!");

		Collection<Cache> result = new ArrayList<Cache>(caches);

		for (String cacheName : getCacheNames()) {

			boolean configuredCacheAlreadyPresent = false;

			for (Cache cache : caches) {

				if (cache.getName().equals(cacheName)) {
					configuredCacheAlreadyPresent = true;
					break;
				}
			}

			if (!configuredCacheAlreadyPresent) {
				result.add(getCache(cacheName));
			}
		}

		return result;
	}

	private Cache createAndAddCache(String cacheName) {
		addCache(createCache(cacheName));
		return super.getCache(cacheName);
	}

	@SuppressWarnings("unchecked")
	private RedisCache createCache(String cacheName) {
		long expiration = computeExpiration(cacheName);
		return new RedisCache(cacheName, (usePrefix ? cachePrefix.prefix(cacheName) : null), template, expiration);
	}

	private long computeExpiration(String name) {
		Long expiration = null;
		if (expires != null) {
			expiration = expires.get(name);
		}
		return (expiration != null ? expiration.longValue() : defaultExpiration);
	}

	private List<RedisCache> loadAndInitRemoteCaches() {

		List<RedisCache> caches = new ArrayList<RedisCache>();

		try {
			Set<String> cacheNames = loadRemoteCacheKeys();
			if (!CollectionUtils.isEmpty(cacheNames)) {
				Collection<String> ubossCacheNames = new ArrayList<String>();
				for (String cacheName : cacheNames) {
					if (null == super.getCache(cacheName)) {
						// 在这里判断是否需要创建缓存对象
						if (this.isPisp(cacheName)) {
							caches.add(createCache(cacheName));
						} else {
							ubossCacheNames.add(cacheName);
						}
					}
				}
				// 将UBOSS的缓存名称放入CacheUtils中
				CacheUtils.putUbossCacheNames(ubossCacheNames);
			}
		} catch (Exception e) {
			if (logger.isWarnEnabled()) {
				logger.warn("Failed to initialize cache with remote cache keys.", e);
			}
		}

		return caches;
	}

	/**
	 * 新添加的方法，用于判断系统类型
	 * <ul>
	 * <li>BETTLE框架中使用JdkSerializationRedisSerializer</li>
	 * <li>UBOSS框架中使用StringRedisSerializer</li>
	 * </ul>
	 * 
	 * @param cacheName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private boolean isPisp(final String cacheName) {
		return (Boolean) template.execute(new RedisCallback<Boolean>() {

			@Override
			public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
				byte[] setName = template.getKeySerializer().serialize(cacheName + "~keys");

				Set<byte[]> keys = connection.zRange(setName, 0, 10);
				if (!CollectionUtils.isEmpty(keys)) {
					for (byte[] key : keys) {
						try {
							byte[] bs = connection.get(key);
							// 尝试内否正常反序列化
							template.getValueSerializer().deserialize(bs);
							logger.info("PISP框架内的缓存：" + cacheName);
							return true;
						} catch (Exception e) {
							logger.info("UBOSS框架内的缓存：" + cacheName);
							return false;
						}
					}
				}
				return false;
			}
		});
	}

	@SuppressWarnings("unchecked")
	private Set<String> loadRemoteCacheKeys() {
		return (Set<String>) template.execute(new RedisCallback<Set<String>>() {

			@Override
			public Set<String> doInRedis(RedisConnection connection) throws DataAccessException {

				// we are using the ~keys postfix as defined in
				// RedisCache#setName
				Set<byte[]> keys = connection.keys(template.getKeySerializer().serialize("*~keys"));
				Set<String> cacheKeys = new LinkedHashSet<String>();

				if (!CollectionUtils.isEmpty(keys)) {
					for (byte[] key : keys) {
						cacheKeys.add(template.getKeySerializer().deserialize(key).toString().replace("~keys", ""));
					}
				}

				return cacheKeys;
			}
		});
	}
}
