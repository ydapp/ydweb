package net.yuan.nova.pis.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.yuan.nova.commons.SystemConstant;
import net.yuan.nova.commons.SystemParam;
import net.yuan.nova.core.shiro.CurrentUserUtil;
import net.yuan.nova.core.shiro.vo.User;
import net.yuan.nova.pis.entity.PisMenuItem;
import net.yuan.nova.pis.service.MenuItemService;

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
public class AdminController {

	protected final Logger logger = LoggerFactory.getLogger(AdminController.class);

	private String loginUrl = "/login.html";
	private String successUrl = "/admin/index.html";
	@Autowired
	private MenuItemService menuItemService;

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
		return getAdminTemplate(request.getServletPath());
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
		return "error/404";
	}

}
