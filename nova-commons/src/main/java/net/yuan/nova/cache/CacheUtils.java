package net.yuan.nova.cache;

import java.util.Collection;
import java.util.Set;

import net.sf.json.JSONObject;
import net.yuan.nova.commons.SpringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 
 * 对缓存的常用方法进行封装
 * 
 * @author 张帅（0027005704）
 *
 */
public class CacheUtils {

	private static final Logger log = LoggerFactory.getLogger(CacheUtils.class);

	private static BettleCacheManager cacheManager;

	protected static BettleCacheManager getCacheManager() {
		if (cacheManager == null) {
			cacheManager = (BettleCacheManager) SpringUtils.getBean("bettleCacheManager");
		}
		return cacheManager;
	}

	/**
	 * 根据缓存名称获得缓存对象
	 * 
	 * @param name
	 * @return
	 */
	protected static <K, V> BettleCache<K, V> getCache(String name) {
		return getCacheManager().getCache(name);
	}

	/**
	 * 获得所有缓存的名字
	 * 
	 * @return
	 */
	public static Collection<String> getCacheNames() {
		return getCacheManager().getCacheNames();
	}

	/**
	 * 获得缓存的值
	 * 
	 * @param cacheName
	 * @param key
	 * @return
	 */
	public static Object get(String cacheName, Object key) {
		return getCache(cacheName).get(key);
	}

	/**
	 * 将数据插入指定缓存
	 * 
	 * @param cacheName
	 * @param key
	 * @param value
	 * @return
	 */
	public static Object put(String cacheName, Object key, Object value) {
		return getCache(cacheName).put(key, value);
	}

	/**
	 * 从缓存中移除指定的项
	 * 
	 * @param cacheName
	 * @param key
	 * @return
	 */
	public static Object remove(String cacheName, Object key) {
		return getCache(cacheName).remove(key);
	}

	/**
	 * 清空指定缓存
	 * 
	 * @param cacheName
	 */
	public static void clear(String cacheName) {
		getCache(cacheName).clear();
	}

	/**
	 * 获得缓存中数据总数
	 * 
	 * @param cacheName
	 * @return
	 */
	public static int size(String cacheName) {
		return getCache(cacheName).size();
	}

	/**
	 * 获得缓存中的所有key值
	 * 
	 * @param cacheName
	 * @return
	 */
	public static Set<Object> keys(String cacheName) {
		return getCache(cacheName).keys();
	}

	/**
	 * 分页获得缓存中的key值
	 * 
	 * @param cacheName
	 * @return
	 */
	public static Set<Object> keys(String cacheName, int page, int size) {
		return getCache(cacheName).keys(page, size);
	}

	/**
	 * 获得缓存中的所有数据
	 * 
	 * @param cacheName
	 * @return
	 */
	public static Collection<Object> values(String cacheName) {
		return getCache(cacheName).values();
	}

	// ///////////////////////////////////////////////////////////
	// ////////////////////以下为兼容UBOSS缓存的实现///////////////
	// ///////////////////////////////////////////////////////////

	private static BettleCacheManager ubossCacheManager;

	/**
	 * 获得对应UBOSS框架的缓存管理器，该缓存管理器只支持从redis中获取数据
	 * 
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private static BettleCacheManager getUbossCacheManager() {
		if (ubossCacheManager == null) {
			RedisTemplate redisTemplate = null;
			try {
				redisTemplate = (RedisTemplate) SpringUtils.getBean("redisTemplate");
			} catch (Exception e) {
				log.warn("获取RedisTemplate对象时失败，系统没有使用redis作为缓存");
			}
			if (redisTemplate != null) {
				ubossCacheManager = new UbossRedisCacheManager(redisTemplate);
				if (ubossCacheNames != null) {
					for (String name : ubossCacheNames) {
						ubossCacheManager.getCache(name);
					}
				}
			}
		}
		return ubossCacheManager;
	}

	private static BettleCache<String, String> getUbossCache(String cacheName) {
		BettleCacheManager ucm = getUbossCacheManager();
		if (ucm != null) {
			return ucm.getCache(cacheName);
		}
		return null;
	}

	private static Collection<String> ubossCacheNames = null;

	public static void putUbossCacheNames(Collection<String> cacheNames) {
		ubossCacheNames = cacheNames;
	}

	/**
	 * 获得UBOSS中所有缓存的名字
	 * 
	 * @return
	 */
	public static Collection<String> getUbossCacheNames() {
		BettleCacheManager ucm = getUbossCacheManager();
		if (ucm != null) {
			return ucm.getCacheNames();
		}
		return null;
	}

	/**
	 * 取出UBOSS框架保存的数据
	 * 
	 * @param cacheName
	 * @param key
	 * @return
	 */
	public static String ubossCacheGet(String cacheName, String key) {
		BettleCache<String, String> cache = getUbossCache(cacheName);
		if (cache != null) {
			return cache.get(key);
		}
		return null;
	}

	/**
	 * 向UBOSS的缓存中存入数据
	 * 
	 * @param cacheName
	 * @param key
	 * @param value
	 * @return
	 */
	public static String ubossCachePut(String cacheName, String key, Object value) {
		BettleCache<String, String> cache = getUbossCache(cacheName);
		if (cache != null) {
			if (value instanceof String) {
				return cache.put(key, (String) value);
			} else {
				return cache.put(key, JSONObject.fromObject(value).toString());
			}
		}
		return null;
	}

	/**
	 * 从UBOSS缓存中移除指定的项
	 * 
	 * @param cacheName
	 * @param key
	 * @return
	 */
	public static String ubossCacheRemove(String cacheName, String key) {
		BettleCache<String, String> cache = getUbossCache(cacheName);
		if (cache != null) {
			return cache.remove(key);
		}
		return null;
	}

	/**
	 * 清空UBOSS中指定缓存
	 * 
	 * @param cacheName
	 */
	public static void ubossCacheClear(String cacheName) {
		BettleCache<String, String> cache = getUbossCache(cacheName);
		if (cache != null) {
			cache.clear();
		}
	}

	/**
	 * 获得UBOSS缓存中数据总数
	 * 
	 * @param cacheName
	 * @return
	 */
	public static int ubossCacheSize(String cacheName) {
		BettleCache<String, String> cache = getUbossCache(cacheName);
		if (cache != null) {
			return cache.size();
		}
		return 0;
	}

	/**
	 * 获得UBOSS缓存中的所有key值
	 * 
	 * @param cacheName
	 * @return
	 */
	public static Set<String> ubossCacheKeys(String cacheName) {
		BettleCache<String, String> cache = getUbossCache(cacheName);
		if (cache != null) {
			return cache.keys();
		}
		return null;
	}

	/**
	 * 分页获得UBOSS缓存中的key值
	 * 
	 * @param cacheName
	 * @return
	 */
	public static Set<String> ubossCacheKeys(String cacheName, int page, int size) {
		BettleCache<String, String> cache = getUbossCache(cacheName);
		if (cache != null) {
			return cache.keys(page, size);
		}
		return null;
	}

	/**
	 * 获得UBOSS缓存中的所有数据
	 * 
	 * @param cacheName
	 * @return
	 */
	public static Collection<String> ubossCacheValues(String cacheName) {
		BettleCache<String, String> cache = getUbossCache(cacheName);
		if (cache != null) {
			return cache.values();
		}
		return null;
	}

}
