package net.yuan.nova.cache;

/**
 * 类级别的缓存实现，用于少量数据的缓存，默认缓存数据100条有效时间30分钟（1800秒）
 * 
 * @author Administrator
 *
 */
public abstract class CacheHandle {

	// 缓存的时间，默认30分钟，即：1800秒
	private long DEFAULT_CACHE_SECONDS = 1800;

	private long cacheTimes = DEFAULT_CACHE_SECONDS;
	// 缓存的时间，毫秒
	private long cacheTimeMillis = cacheTimes * 1000;

	private static final String CACHE_NAME = "CacheHandle";

	private BettleCache<String, CacheWrapper> beanCache;

	public CacheHandle() {

	}

	/**
	 * 获得缓存中的数据
	 * 
	 * @param key
	 * @return
	 */
	protected Object getCacheValue(String key) {
		key = getCacheKey(key);
		CacheWrapper cache = getBeanCache().get(key);
		if (cache == null) {
			return null;
		}
		long time = cache.getTimeMillis();
		long bt = System.currentTimeMillis() - time;
		if (bt > cacheTimeMillis) {
			getBeanCache().remove(key);
			return null;
		}
		return cache.getValue();
	}

	/**
	 * 向缓存中赋值
	 * 
	 * @param key
	 * @param value
	 */
	protected void setCacheValue(String key, Object value) {
		key = getCacheKey(key);
		CacheWrapper cache = new CacheItem(value);
		getBeanCache().put(key, cache);
	}

	private BettleCache<String, CacheWrapper> getBeanCache() {
		if (beanCache == null) {
			beanCache = CacheUtils.getCache(CACHE_NAME);
		}
		return beanCache;
	}

	private String getCacheKey(String key) {
		return this.getClass().getSimpleName() + "_" + key;
	}

	/**
	 * 设置缓存的时间，单位秒
	 * 
	 * @param catchTimes
	 */
	protected void setCacheTimes(long cacheTimes) {
		this.cacheTimes = cacheTimes;
		this.cacheTimeMillis = cacheTimes * 1000;
	}

	/**
	 * 缓存对象的包装
	 */
	interface CacheWrapper {

		/**
		 * @return 加入缓存时的时间戳
		 */
		public long getTimeMillis();

		/**
		 * @return 缓存的值
		 */
		public Object getValue();
	}

}
