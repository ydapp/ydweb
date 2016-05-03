package net.yuan.nova.pis.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.yuan.nova.commons.SystemConstant;
import net.yuan.nova.commons.SystemParam;
import net.yuan.nova.core.shiro.CurrentUserUtil;
import net.yuan.nova.core.shiro.vo.User;
import net.yuan.nova.pis.entity.PisMenuItem;
import net.yuan.nova.pis.service.MenuItemService;
import net.yuan.nova.pis.service.TemplateService;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
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

	private String loginUrl = "/login.html";
	private String successUrl = "/index.html";
	@Autowired
	private TemplateService templateService;
	@Autowired
	private MenuItemService menuItemService;

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
	 * 管理页面登录页面和登录地址
	 * 
	 * @param request
	 * @param response
	 * @param modelMap
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/login.html")
	public String login(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) throws Exception {

		Subject currentUser = SecurityUtils.getSubject();
		if (currentUser.isAuthenticated()) {
			WebUtils.issueRedirect(request, response, successUrl);
			return null;
		} else {
			String exceptionClassName = (String) request
					.getAttribute(FormAuthenticationFilter.DEFAULT_ERROR_KEY_ATTRIBUTE_NAME);
			logger.debug("exceptionClassName:{}", exceptionClassName);
			String msg = null;
			if (UnknownAccountException.class.getName().equals(exceptionClassName)) {
				// 您输入的用户名无效
				msg = "用户名或密码错误！";
			} else if (IncorrectCredentialsException.class.getName().equals(exceptionClassName)) {
				// 密码错误
				msg = "用户名或密码错误！";
			} else if (AuthenticationException.class.getName().equals(exceptionClassName)) {
				msg = "用户名或密码错误！";
			} else if (LockedAccountException.class.getName().equals(exceptionClassName)) {
				msg = "账号被锁定！";
			} else if (exceptionClassName != null) {
				msg = "其他错误：" + exceptionClassName;
			}
			logger.debug(msg);
			modelMap.addAttribute("hasError", StringUtils.isNotBlank(msg));
			modelMap.addAttribute("msg", msg);

			modelMap.addAttribute("systemName", "未设置名称的应用系统");
			SystemParam systemName = SystemConstant.getSystemParam("SYSTEM_NAME");
			if (systemName != null && StringUtils.isNoneBlank(systemName.getCurrentValue())) {
				modelMap.addAttribute("systemName", systemName.getCurrentValue());
			}
			modelMap.addAttribute("copyright", "@未设置版权信息");
			SystemParam copyright = SystemConstant.getSystemParam("COPYRIGHT");
			if (systemName != null && StringUtils.isNoneBlank(copyright.getCurrentValue())) {
				modelMap.addAttribute("copyright", copyright.getCurrentValue());
			}
			return "admin/login";
		}
	}

	/**
	 * 
	 * 系统登出接口，登出后转向登录页面
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/logout.html")
	public String logout(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Subject currentUser = SecurityUtils.getSubject();
		currentUser.logout();
		WebUtils.issueRedirect(request, response, loginUrl);
		return null;
	}

	/**
	 * 管理页面的主页面
	 * 
	 * @param request
	 * @param response
	 * @param modelMap
	 * @return
	 */
	@RequestMapping(value = { "/admin/index.html", "/admin", "/admin/" }, method = RequestMethod.GET)
	public String adminIndex(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) {
		// 当前登录用户
		User shiroUser = CurrentUserUtil.getShiroUser();
		// 获得一级菜单
		List<PisMenuItem> Menus = menuItemService.findMenus(null);
		modelMap.addAttribute("menus_kind", Menus);
		modelMap.put(SystemConstant.CURRENT_USER, shiroUser);
		modelMap.addAttribute("systemName", "未设置名称的应用系统");
		SystemParam systemName = SystemConstant.getSystemParam("SYSTEM_NAME");
		if (systemName != null && StringUtils.isNoneBlank(systemName.getCurrentValue())) {
			modelMap.addAttribute("systemName", systemName.getCurrentValue());
		}
		return "/admin/index";
	}

	/**
	 * 管理页面各模块页面
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = { "/admin/**/*.html", "/admin/**/index.html" }, method = RequestMethod.GET)
	public String adminIndex(HttpServletRequest request, HttpServletResponse response) {
		logger.debug("系统管理");
		return templateService.getAdminTemplate(request.getServletPath());
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
