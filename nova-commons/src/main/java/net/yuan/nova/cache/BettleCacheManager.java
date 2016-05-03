package net.yuan.nova.cache;

import java.util.Collection;

public interface BettleCacheManager {

	/**
	 * 获得所有的缓存名称
	 * 
	 * @return
	 */
	public Collection<String> getCacheNames();

	/**
	 * 
	 * 根据缓存名称<code>name</code>获得缓存对象，如果传入的缓存名称没有对应的缓存存在，将会创建一个新的缓存并返回
	 *
	 * @param name
	 *            缓存名称
	 * @return 给定名称的缓存对象
	 * @throws CacheException
	 *             if there is an error acquiring the Cache instance.
	 */
	public <K, V> BettleCache<K, V> getCache(String name);

}
