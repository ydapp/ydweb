package net.yuan.nova.pis.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.yuan.nova.pis.service.TemplateService;

import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Administrator
 * 
 *         系统主页及栏目主页控制器
 *
 */

@Controller
public class IndexController {

	protected final Logger logger = LoggerFactory.getLogger(IndexController.class);

	@Autowired
	private TemplateService templateService;

	/**
	 * 首页
	 * 
	 * @param pageNum
	 * @param modelMap
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/index.html", method = RequestMethod.GET)
	public String index(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) throws IOException {
		// TODO 跳转到系统的主站点首页
		WebUtils.issueRedirect(request, response, "/admin/index.html");
		return null;
	}

	/**
	 * 404
	 * 
	 * @return
	 */
	@RequestMapping(value = "/404", method = RequestMethod.GET)
	public String pageNotFound(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) {
		modelMap.addAttribute("message", "请求的资源不存在（404）");
		response.setStatus(HttpStatus.NOT_FOUND.value());
		return templateService.getErrorTemplate(404);
	}

	/**
	 * 500
	 * 
	 * @return
	 */
	@RequestMapping(value = "/500", method = RequestMethod.GET)
	public String error(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) {
		modelMap.addAttribute("message", "服务器内部错误（500）");
		response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
		return templateService.getErrorTemplate(500);
	}

}
