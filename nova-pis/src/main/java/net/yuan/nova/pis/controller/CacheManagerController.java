package net.yuan.nova.pis.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.yuan.nova.core.vo.DataGridData;
import net.yuan.nova.pis.entity.vo.CacheInfo;
import net.yuan.nova.pis.service.CacheManagerService;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CacheManagerController {

	private Logger logger = LoggerFactory.getLogger(CacheManagerController.class);
	@Autowired
	private CacheManagerService cacheManagerService;

	/**
	 * 获取所有缓存名
	 * 
	 * @param request
	 * @param modelMap
	 * @return
	 */
	@RequestMapping(value = "/api/CacheManager/getCacheNames")
	public Object getCacheNames(HttpServletRequest request, ModelMap modelMap) {
		logger.debug("step into getCacheNames ...");
		DataGridData<CacheInfo> data = new DataGridData<CacheInfo>();
		List<CacheInfo> list = new ArrayList<CacheInfo>();
		list = cacheManagerService.getCacheNames();
		data.setTotal(list.size());
		data.setRows(list);
		return data;
	}

	/**
	 * 根据缓存名得到所有的key
	 * 
	 * @param request
	 * @param modelMap
	 * @return
	 */
	@RequestMapping(value = "/api/CacheManager/getCacheKeysByCacheName")
	public Object getCacheKeysByCacheName(HttpServletRequest request, ModelMap modelMap) {
		logger.debug("step into getCacheKeysByCacheName ...");
		// 当前页数
		int page = NumberUtils.toInt(StringUtils.trimToEmpty(request.getParameter("page")), 1);
		// // 每行条数
		int rows = NumberUtils.toInt(StringUtils.trimToEmpty(request.getParameter("rows")), 100);
		String cacheName = request.getParameter("cacheName");
		String cacheSource = request.getParameter("cacheSource");
		List<CacheInfo> cacheKeysByCacheName = cacheManagerService.getCacheKeysByCacheName(cacheName, cacheSource,
				page, rows);
		DataGridData<CacheInfo> data = new DataGridData<CacheInfo>();
		data.setRows(cacheKeysByCacheName);
		data.setTotal(cacheManagerService.getkeysTotalSize(cacheName, cacheSource));
		return data;
	}

	/**
	 * 根据key获得value
	 * 
	 * @param request
	 * @param modelMap
	 * @return
	 */
	@RequestMapping(value = "/api/CacheManager/getValueByKey")
	public Object getValueByKey(HttpServletRequest request, ModelMap modelMap) {
		String cacheName = request.getParameter("cacheName");
		String cacheSource = request.getParameter("cacheSource");
		String key = request.getParameter("key");
		Object o = cacheManagerService.getValueByKey(cacheName, cacheSource, key);
		modelMap.addAttribute("res", o);
		return modelMap;
	}

	/**
	 * 清空指定的缓存
	 * 
	 * @param request
	 * @param modelMap
	 * @return
	 */
	@RequestMapping(value = "/api/CacheManager/clearByCacheName")
	public Object clearByCacheName(HttpServletRequest request, ModelMap modelMap) {
		String cacheName = request.getParameter("cacheName");
		String cacheSource = request.getParameter("cacheSource");
		try {
			cacheManagerService.clearByCacheName(cacheName, cacheSource);
			modelMap.addAttribute("success", true);
		} catch (Exception e) {
			logger.error("清空缓存失败");
			modelMap.addAttribute("success", false);
		}
		return modelMap;
	}

	/**
	 * 删除缓存
	 * 
	 * @param request
	 * @param modelMap
	 * @return
	 */
	@RequestMapping(value = "/api/CacheManager/removeByKey")
	public Object removeByKey(HttpServletRequest request, ModelMap modelMap) {
		String cacheName = request.getParameter("cacheName");
		String cacheSource = request.getParameter("cacheSource");
		String key = request.getParameter("key");
		try {
			cacheManagerService.removeByKey(cacheName, cacheSource, key);
			modelMap.addAttribute("success", true);
		} catch (Exception e) {
			logger.error("根据缓存key值删除数据失败");
			modelMap.addAttribute("success", false);
		}
		return modelMap;
	}

}
