package net.yuan.nova.cache.shiro;

import net.yuan.nova.cache.BettleCache;
import net.yuan.nova.cache.BettleCacheManager;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.util.Destroyable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 缓存管理器<code>{@link org.apache.shiro.cache.CacheManager}</code>
 * ，用来缓存用户认证信息和授权信息。 参照如下配置：<br>
 * <code>
 *     <bean id="shiroCacheManager" class="net.yuan.nova.commons.cache.ShiroCacheManager">
 *         <property name="cacheManager" ref="bettleCacheManager"/>
 *     </bean>
 * </code><br>
 * 其中bettleCacheManager是对
 * <code>{@link org.springframework.cache.CacheManager}</code>的包装。<br>
 * <code>
 *     <bean id="bettleCacheManager" class="net.yuan.nova.commons.cache.SpringCacheManager">
 *         <property name="cacheManager" ref="cacheManager" />
 *     </bean>
 * </code>
 * 
 * @author 0027005704
 *
 */
public class ShiroCacheManager implements CacheManager, Destroyable {

	private static final Logger log = LoggerFactory.getLogger(ShiroCacheManager.class);

	private BettleCacheManager cacheManager;

	public ShiroCacheManager() {

	}

	@Override
	public <K, V> Cache<K, V> getCache(String name) throws CacheException {
		if (log.isTraceEnabled()) {
			log.trace("Acquiring EhCache instance named [" + name + "]");
		}
		try {
			BettleCache<K, V> cache = getCacheManager().getCache(name);
			log.info("Using BettleCache named [" + cache.getName() + "]");
			return new ShiroCache<K, V>(cache);
		} catch (net.sf.ehcache.CacheException e) {
			throw new CacheException(e);
		}
	}

	@Override
	public void destroy() throws Exception {
		this.cacheManager = null;
	}

	public BettleCacheManager getCacheManager() {
		return cacheManager;
	}

	public void setCacheManager(BettleCacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}

}
