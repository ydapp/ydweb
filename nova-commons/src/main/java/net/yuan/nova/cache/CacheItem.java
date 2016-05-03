package net.yuan.nova.cache;

import java.io.Serializable;

import net.yuan.nova.cache.CacheHandle.CacheWrapper;

/**
 * 在<code>{@link CacheHandle}</code>使用的缓存实体
 * 
 * @author 0027005704
 *
 */
public class CacheItem implements CacheWrapper, Serializable {
	/**
	 * 序列化ID
	 */
	private static final long serialVersionUID = 1L;

	// 数据存入或保存时的时间戳
	private long timeMillis;

	private Object value;

	public CacheItem(Object value) {
		this.timeMillis = System.currentTimeMillis();
		this.value = value;
	}

	public long getTimeMillis() {
		return timeMillis;
	}

	public Object getValue() {
		return value;
	}

}
