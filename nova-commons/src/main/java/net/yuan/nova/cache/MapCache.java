package net.yuan.nova.cache;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

class MapCache<K, V> implements BettleCache<K, V> {

	/**
	 * Backing instance.
	 */
	private final Map<K, V> store;

	/**
	 * The name of this cache.
	 */
	private final String name;

	MapCache(String name, Map<K, V> backingMap) {
		if (name == null) {
			throw new IllegalArgumentException("Cache name cannot be null.");
		}
		if (backingMap == null) {
			throw new IllegalArgumentException("Backing map cannot be null.");
		}
		this.name = name;
		this.store = backingMap;
	}

	@Override
	public String getName() {
		return name;
	}

	public V get(K key) {
		return store.get(key);
	}

	public V put(K key, V value) {
		return store.put(key, value);
	}

	public V remove(K key) {
		return store.remove(key);
	}

	public void clear() {
		store.clear();
	}

	public int size() {
		return store.size();
	}

	public Set<K> keys() {
		Set<K> keys = store.keySet();
		if (!keys.isEmpty()) {
			return Collections.emptySet();
		}
		return Collections.unmodifiableSet(keys);
	}

	@Override
	public Set<K> keys(int page, int size) {
		return null;
	}

	public Collection<V> values() {
		Collection<V> values = store.values();
		if (values == null || values.isEmpty()) {
			return Collections.emptySet();
		}
		return Collections.unmodifiableCollection(values);
	}

	public String toString() {
		return new StringBuilder("MapCache '").append(name).append("' (").append(store.size()).append(" entries)")
				.toString();
	}

}