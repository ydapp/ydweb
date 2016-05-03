package net.yuan.nova.cache;

import java.util.Collection;
import java.util.Set;

public interface BettleCache<K, V> {

	/**
	 * 获得当前缓存名称
	 * 
	 * @return
	 */
	String getName();

	/**
	 * 获得缓存的对象
	 *
	 * @param key
	 * @return
	 */
	public V get(K key);

	/**
	 * 添加对象到缓存
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public V put(K key, V value);

	/**
	 * 移除指定的缓存项
	 * 
	 * @param key
	 * @return
	 */
	public V remove(K key);

	/**
	 * 清空缓存中的所有数据
	 */
	public void clear();

	/**
	 * 返回缓存中的条目数
	 */
	public int size();

	/**
	 * 返回该缓存中包含的所有项的键.
	 */
	public Set<K> keys();

	/**
	 * 分页返回该缓存中包含的所有项的键.
	 * 
	 * @param page
	 *            当前页数，页数从1开始
	 * @param size
	 *            每页多少条记录
	 * @return
	 */
	public Set<K> keys(int page, int size);

	/**
	 * 返回该缓存中包含的所有项的值
	 */
	public Collection<V> values();
}
