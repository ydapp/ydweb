package net.yuan.nova.pis.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.yuan.nova.cache.CacheUtils;
import net.yuan.nova.pis.entity.vo.CacheInfo;

import org.springframework.stereotype.Service;

@Service
public class CacheManagerService {

	/**
	 * 获取缓存名称（uboss和pisp）
	 * 
	 * @return
	 */
	public List<CacheInfo> getCacheNames() {
		List<CacheInfo> cacheInfos = new ArrayList<CacheInfo>();
		List<CacheInfo> cacheInfosRemove = new ArrayList<CacheInfo>();
		List<CacheInfo> cacheInfosPisp = null;
		List<CacheInfo> cacheInfosUboss = null;
		cacheInfosPisp = getCacheNameImpl("pisp");
		cacheInfosUboss = getCacheNameImpl("uboss");
		if (cacheInfosPisp != null) {
			Collections.sort(cacheInfosPisp);
			cacheInfos.addAll(cacheInfosPisp);
		}
		if (cacheInfosUboss != null) {
			Collections.sort(cacheInfosUboss);
			cacheInfos.addAll(cacheInfosUboss);
		}
		if (cacheInfos.size() > 0) {
			for (CacheInfo c : cacheInfos) {
				if (!"shiro-activeSessionCache".equals(c.getCacheName())) {
					cacheInfosRemove.add(c);
				}
			}
		}
		return cacheInfosRemove;
	}

	/**
	 * 获取shiro-activeSessionCache缓存下的keys数量
	 * 
	 * @return
	 */
	public long getActiveUserCount() {
		Collection<String> cahcheNames = null;
		cahcheNames = CacheUtils.getCacheNames();
		Iterator<String> itPisp = cahcheNames.iterator();
		while (itPisp.hasNext()) {
			String str = itPisp.next();
			if ("shiro-activeSessionCache".equals(str)) {
				Set<Object> keys = CacheUtils.keys("shiro-activeSessionCache");
				return keys.size();
			}
		}
		return 0;
	}

	/**
	 * 根据缓存来源获取具体的缓存名称
	 * 
	 * @param type
	 * @return
	 */
	public List<CacheInfo> getCacheNameImpl(String type) {
		List<CacheInfo> cacheInfos = new ArrayList<CacheInfo>();
		Collection<String> cahcheNames = null;
		if ("pisp".equals(type)) {
			cahcheNames = CacheUtils.getCacheNames();// 从pisp中获取所有缓存name
		} else {
			cahcheNames = CacheUtils.getUbossCacheNames();
		}
		if (cahcheNames == null) {
			return null;
		}
		Iterator<String> itPisp = cahcheNames.iterator();
		CacheInfo cacheInfo = null;
		while (itPisp.hasNext()) {
			cacheInfo = new CacheInfo();
			String str = itPisp.next();
			cacheInfo.setCacheName(str);
			if ("pisp".equals(type)) {
				cacheInfo.setCacheSource(CacheInfo.Source.pisp);
				long size = CacheUtils.size(str);
				cacheInfo.setKeySize(size);
			} else {
				cacheInfo.setCacheSource(CacheInfo.Source.uboss);
				long size = CacheUtils.ubossCacheSize(str);
				cacheInfo.setKeySize(size);
			}
			cacheInfos.add(cacheInfo);
		}
		return cacheInfos;
	}

	/**
	 * 根据缓存名得到所有的key
	 * 
	 * @param cacheName
	 * @param cacheSource
	 * @return
	 */
	public List<CacheInfo> getCacheKeysByCacheName(String cacheName, String cacheSource, int page, int size) {
		List<CacheInfo> list = new ArrayList<CacheInfo>();
		CacheInfo cacheInfo = null;
		if ("pisp".equals(cacheSource)) {
			Set<Object> keys = CacheUtils.keys(cacheName, page, size);
			Iterator<Object> it = keys.iterator();
			while (it.hasNext()) {
				cacheInfo = new CacheInfo();
				cacheInfo.setKey(it.next().toString());
				cacheInfo.setCacheName(cacheName);
				cacheInfo.setCacheSource(CacheInfo.Source.pisp);
				list.add(cacheInfo);
			}
			return list;
		} else if ("uboss".equals(cacheSource)) {
			Set<String> keys = CacheUtils.ubossCacheKeys(cacheName, page, size);
			Iterator<String> it = keys.iterator();
			while (it.hasNext()) {
				cacheInfo = new CacheInfo();
				cacheInfo.setKey(it.next().toString());
				cacheInfo.setCacheName(cacheName);
				cacheInfo.setCacheSource(CacheInfo.Source.uboss);
				list.add(cacheInfo);
			}
			return list;
		}
		return null;
	}

	/**
	 * 根据缓存名得到所有的key
	 * 
	 * @param cacheName
	 * @param cacheSource
	 * @return
	 */
	public int getkeysTotalSize(String cacheName, String cacheSource) {
		if ("pisp".equals(cacheSource)) {
			Set<Object> keys = CacheUtils.keys(cacheName);
			return keys.size();
		} else if ("uboss".equals(cacheSource)) {
			Set<String> keys = CacheUtils.ubossCacheKeys(cacheName);
			return keys.size();
		}
		return 0;
	}

	/**
	 * 根据key得到相应的value
	 * 
	 * @param cacheName
	 * @param cacheSource
	 * @param key
	 * @return
	 */
	public Object getValueByKey(String cacheName, String cacheSource, String key) {
		if ("pisp".equals(cacheSource)) {
			Object value = CacheUtils.get(cacheName, key);
			return value;
		} else if ("uboss".equals(cacheSource)) {
			String value = CacheUtils.ubossCacheGet(cacheName, key);
			return value;
		}
		return null;
	}

	/**
	 * 根据缓存名清除缓存
	 * 
	 * @param cacheName
	 * @param cacheSource
	 */
	public void clearByCacheName(String cacheName, String cacheSource) {
		if ("pisp".equals(cacheSource)) {
			CacheUtils.clear(cacheName);
		} else if ("uboss".equals(cacheSource)) {
			CacheUtils.ubossCacheClear(cacheName);
		}
	}

	/**
	 * 根据缓存名和key清除对应的value
	 * 
	 * @param cacheName
	 * @param cacheSource
	 * @param key
	 * @return
	 */
	public Object removeByKey(String cacheName, String cacheSource, String key) {
		if ("pisp".equals(cacheSource)) {
			return CacheUtils.remove(cacheName, key);
		} else if ("uboss".equals(cacheSource)) {
			return CacheUtils.ubossCacheRemove(cacheName, key);
		}
		return null;
	}

}
