package net.yuan.nova.pis.service;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @author Administrator
 * 
 *         网站模版服务类
 *
 */
@Service
public class TemplateService {

	protected final Logger logger = LoggerFactory.getLogger(TemplateService.class);

	private static String INDEX_TEMPLATE_SUFFIX = "index";
	private static String SEARCH_TEMPLATE_SUFFIX = "search";

	/**
	 * 得到首页（默认页）模板
	 * 
	 * @return
	 * @throws TemplateNotFoundException
	 */
	public String getDefaultTemplate() {
		return INDEX_TEMPLATE_SUFFIX;
	}

	/**
	 * 获得查询页面的模版
	 * 
	 * @return
	 * @throws TemplateNotFoundException
	 */
	public String getSearcherTemplate() {
		return SEARCH_TEMPLATE_SUFFIX;
	}

	/**
	 * 更具传入的servletPath获得相应的模版
	 * 
	 * @param servletPath
	 * @return
	 */
	public String getAdminTemplate(String servletPath) {
		if (StringUtils.isNotBlank(servletPath)) {
			return servletPath.substring(0, servletPath.indexOf("."));
		}
		return this.getErrorTemplate(404);
	}

	public String getAdminTemplate(String[] paths) {
		if (paths != null && paths.length > 0) {
			return StringUtils.join(paths, "/");
		}
		return this.getErrorTemplate(404);
	}

	/**
	 * 得到当前请求需要渲染的模板相对路径
	 * 
	 * @param theme
	 * @return
	 */
	public String getTemplate(String[] paths) {
		if (paths != null && paths.length > 0) {
			return StringUtils.join(paths, "/");
		}
		return null;
	}

	public String getErrorTemplate(int errorCode) {
		return "error/" + Integer.toString(errorCode);
	}

}
