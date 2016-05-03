package net.yuan.nova.pis.entity.vo;

import java.io.Serializable;

public class CacheInfo implements Serializable, Comparable<CacheInfo> {

	private static final long serialVersionUID = 1L;

	private String cacheName;
	private Source cacheSource; // 区分是pisp还是uboss
	private String key;
	private long keySize;

	public long getKeySize() {
		return keySize;
	}

	public void setKeySize(long keySize) {
		this.keySize = keySize;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public static enum Source {
		/**
		 * 公众信息服务平台
		 */
		pisp,
		/**
		 * uboss接入平台
		 */
		uboss;
	}

	public String getCacheName() {
		return cacheName;
	}

	public void setCacheName(String cacheName) {
		this.cacheName = cacheName;
	}

	public Source getCacheSource() {
		return cacheSource;
	}

	public void setCacheSource(Source cacheSource) {
		this.cacheSource = cacheSource;
	}

	@Override
	public int compareTo(CacheInfo arg0) {
		CacheInfo c = (CacheInfo) arg0;
		return this.cacheName.compareTo(c.getCacheName());
	}

}
