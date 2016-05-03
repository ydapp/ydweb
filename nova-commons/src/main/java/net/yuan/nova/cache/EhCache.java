package net.yuan.nova.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import net.sf.ehcache.Element;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class EhCache<K, V> implements BettleCache<K, V> {

	/**
	 * Private internal log instance.
	 */
	private static final Logger log = LoggerFactory.getLogger(EhCache.class);

	/**
	 * The wrapped Ehcache instance.
	 */
	private net.sf.ehcache.Ehcache cache;

	/**
	 * Constructs a new EhCache instance with the given cache.
	 *
	 * @param cache
	 *            - delegate EhCache instance this Shiro cache instance will
	 *            wrap.
	 */
	EhCache(net.sf.ehcache.Ehcache cache) {
		if (cache == null) {
			throw new IllegalArgumentException("Cache argument cannot be null.");
		}
		this.cache = cache;
	}

	@Override
	public String getName() {
		return cache.getName();
	}

	/**
	 * Gets a value of an element which matches the given key.
	 *
	 * @param key
	 *            the key of the element to return.
	 * @return The value placed into the cache with an earlier put, or null if
	 *         not found or expired
	 */
	@SuppressWarnings("unchecked")
	public V get(K key) throws CacheException {
		try {
			if (log.isTraceEnabled()) {
				log.trace("Getting object from cache [" + cache.getName() + "] for key [" + key + "]");
			}
			if (key == null) {
				return null;
			} else {
				Element element = cache.get(key);
				if (element == null) {
					if (log.isTraceEnabled()) {
						log.trace("Element for [" + key + "] is null.");
					}
					return null;
				} else {
					// noinspection unchecked
					return (V) element.getObjectValue();
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
			log.trace("Putting object in cache [" + cache.getName() + "] for key [" + key + "]");
		}
		try {
			V previous = get(key);
			Element element = new Element(key, value);
			cache.put(element);
			return previous;
		} catch (Throwable t) {
			throw new CacheException(t);
		}
	}

	/**
	 * Removes the element which matches the key.
	 *
	 * <p>
	 * If no element matches, nothing is removed and no Exception is thrown.
	 * </p>
	 *
	 * @param key
	 *            the key of the element to remove
	 */
	public V remove(K key) throws CacheException {
		if (log.isTraceEnabled()) {
			log.trace("Removing object from cache [" + cache.getName() + "] for key [" + key + "]");
		}
		try {
			V previous = get(key);
			cache.remove(key);
			return previous;
		} catch (Throwable t) {
			throw new CacheException(t);
		}
	}

	/**
	 * Removes all elements in the cache, but leaves the cache in a useable
	 * state.
	 */
	public void clear() throws CacheException {
		if (log.isTraceEnabled()) {
			log.trace("Clearing all objects from cache [" + cache.getName() + "]");
		}
		try {
			cache.removeAll();
		} catch (Throwable t) {
			throw new CacheException(t);
		}
	}

	public int size() {
		try {
			return cache.getSize();
		} catch (Throwable t) {
			throw new CacheException(t);
		}
	}

	public Set<K> keys() {
		try {
			@SuppressWarnings({ "unchecked" })
			List<K> keys = cache.getKeys();
			if (keys == null || keys.isEmpty()) {
				return Collections.emptySet();
			} else {
				return Collections.unmodifiableSet(new LinkedHashSet<K>(keys));
			}
		} catch (Throwable t) {
			throw new CacheException(t);
		}
	}

	@Override
	public Set<K> keys(int page, int size) {
		try {
			@SuppressWarnings({ "unchecked" })
			List<K> keys = cache.getKeys();
//			Collections.sort(keys, new Comparator<K>() {
//				@Override
//				public int compare(K o1, K o2) {
//					// TODO Auto-generated method stub
//					String str1 = (String) o1;
//					String str2 = (String) o2;
//					return str1.compareTo(str2);
//				}
//
//			});
			List<K> keysPage = new ArrayList<K>();
			if (keys == null || keys.isEmpty()) {
				return Collections.emptySet();
			} else {
				int beginRowNum = (page - 1) * size;
				int endRowNum = page * size - 1;
				for (int i = beginRowNum; i < endRowNum; i++) {
					if ((i + 1) > keys.size()) {
						break;
					}
					keysPage.add(keys.get(i));
				}
				return new LinkedHashSet<K>(keysPage);
			}
		} catch (Throwable t) {
			throw new CacheException(t);
		}
	}

	public Collection<V> values() {
		try {
			@SuppressWarnings({ "unchecked" })
			List<K> keys = cache.getKeys();
			if (keys == null || keys.isEmpty()) {
				return Collections.emptyList();
			} else {
				List<V> values = new ArrayList<V>(keys.size());
				for (K key : keys) {
					V value = get(key);
					if (value != null) {
						values.add(value);
					}
				}
				return Collections.unmodifiableList(values);
			}
		} catch (Throwable t) {
			throw new CacheException(t);
		}
	}

	/**
	 * Returns the size (in bytes) that this EhCache is using in memory (RAM),
	 * or <code>-1</code> if that number is unknown or cannot be calculated.
	 *
	 * @return the size (in bytes) that this EhCache is using in memory (RAM),
	 *         or <code>-1</code> if that number is unknown or cannot be
	 *         calculated.
	 */
	public long getMemoryUsage() {
		try {
			return cache.calculateInMemorySize();
		} catch (Throwable t) {
			return -1;
		}
	}

	/**
	 * Returns the size (in bytes) that this EhCache's memory store is using
	 * (RAM), or <code>-1</code> if that number is unknown or cannot be
	 * calculated.
	 *
	 * @return the size (in bytes) that this EhCache's memory store is using
	 *         (RAM), or <code>-1</code> if that number is unknown or cannot be
	 *         calculated.
	 */
	public long getMemoryStoreSize() {
		try {
			return cache.getMemoryStoreSize();
		} catch (Throwable t) {
			throw new CacheException(t);
		}
	}

	/**
	 * Returns the size (in bytes) that this EhCache's disk store is consuming
	 * or <code>-1</code> if that number is unknown or cannot be calculated.
	 *
	 * @return the size (in bytes) that this EhCache's disk store is consuming
	 *         or <code>-1</code> if that number is unknown or cannot be
	 *         calculated.
	 */
	public long getDiskStoreSize() {
		try {
			return cache.getDiskStoreSize();
		} catch (Throwable t) {
			throw new CacheException(t);
		}
	}

	/**
	 * Returns &quot;EhCache [&quot; + cache.getName() + &quot;]&quot;
	 *
	 * @return &quot;EhCache [&quot; + cache.getName() + &quot;]&quot;
	 */
	public String toString() {
		return "EhCache [" + cache.getName() + "]";
	}

}
