package net.yuan.nova.core.shiro.filter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.yuan.nova.commons.SystemConstant;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.PathMatchingFilter;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhangshuai
 *
 */
public class SysUserFilter extends PathMatchingFilter {

	private final Logger log = LoggerFactory.getLogger(SysUserFilter.class);

	@Override
	protected boolean onPreHandle(ServletRequest request, ServletResponse response, Object mappedValue)
			throws Exception {
		if (log.isDebugEnabled()) {
			HttpServletRequest httpRequest = WebUtils.toHttp(request);
			HttpServletResponse httpResponse = WebUtils.toHttp(response);
			log.debug("User-Agent：{}", httpRequest.getHeader("User-Agent"));
			log.debug("当前请求的地址：{}", httpRequest.getRequestURI());
			SimpleCookie cookie = new SimpleCookie();
			cookie.setName("imei");
			log.debug("imei:{}", cookie.readValue(httpRequest, httpResponse));
			cookie = new SimpleCookie();
			cookie.setName("rememberMe");
			log.debug("rememberMe:{}", cookie.readValue(httpRequest, httpResponse));
			cookie = new SimpleCookie();
			cookie.setName("sid");
			log.debug("sid:{}", cookie.readValue(httpRequest, httpResponse));
			Subject currentUser = SecurityUtils.getSubject();
			String username = (String) SecurityUtils.getSubject().getPrincipal();
			if (currentUser.isAuthenticated()) {
				log.debug("用户已经登录，设置当前的登录用户：{}", username);
				// 设置当前的登录用户
				Session session = currentUser.getSession();
				request.setAttribute(SystemConstant.CURRENT_USER, session.getAttribute(SystemConstant.SESSION_USER));
			} else if (currentUser.isRemembered()) {
				log.debug("之前记住的用户：{}", username);
			}
		}
		return true;
	}
}
