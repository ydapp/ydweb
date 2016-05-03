package net.yuan.nova.cache.shiro;

import java.util.Collection;
import java.util.Set;

import net.yuan.nova.cache.BettleCache;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;

public class ShiroCache<K, V> implements Cache<K, V> {

	private BettleCache<K, V> bettleCache;

	public ShiroCache(BettleCache<K, V> cache) {
		this.bettleCache = cache;
	}

	@Override
	public V get(K key) throws CacheException {
		try {
			return bettleCache.get(key);
		} catch (Throwable t) {
			throw new CacheException(t);
		}
	}

	@Override
	public V put(K key, V value) throws CacheException {
		try {
			return bettleCache.put(key, value);
		} catch (Throwable t) {
			throw new CacheException(t);
		}
	}

	@Override
	public V remove(K key) throws CacheException {
		try {
			return bettleCache.remove(key);
		} catch (Throwable t) {
			throw new CacheException(t);
		}
	}

	@Override
	public void clear() throws CacheException {
		try {
			bettleCache.clear();
		} catch (Throwable t) {
			throw new CacheException(t);
		}
	}

	@Override
	public int size() {
		return bettleCache.size();
	}

	@Override
	public Set<K> keys() {
		return bettleCache.keys();
	}

	@Override
	public Collection<V> values() {
		return bettleCache.values();
	}

}
