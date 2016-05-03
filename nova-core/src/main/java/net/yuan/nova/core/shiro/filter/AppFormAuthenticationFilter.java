package net.yuan.nova.core.shiro.filter;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.servlet.Cookie;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.View;

import net.yuan.nova.core.shiro.AutoAuthenticationToken;

public class AppFormAuthenticationFilter extends FormAuthenticationFilter {

	private final Logger log = LoggerFactory.getLogger(AppFormAuthenticationFilter.class);

	/**
	 * 这个方法决定了是否能让用户登录
	 */
	@Override
	protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
		Subject subject = getSubject(request, response);

		// 如果 isAuthenticated 为 false 证明不是登录过的，同时 isRememberd 为true
		// 证明是没登陆直接通过记住我功能进来的
		if (!subject.isAuthenticated() && subject.isRemembered()) {
			// 判断是否有imei，只有有的情况才允许自动登录
			SimpleCookie cookie = new SimpleCookie();
			cookie.setName("imei");
			String imei = cookie.readValue(WebUtils.toHttp(request), WebUtils.toHttp(response));
			if (StringUtils.isNotBlank(imei)) {
				// 未登录，但已经记住的用户，这里进行自动登录
				Subject currentUser = SecurityUtils.getSubject();
				// 获得记住的用户名
				String username = (String) currentUser.getPrincipals().getPrimaryPrincipal();
				log.info("自动登录：{}，URL：{}", username, WebUtils.toHttp(request).getRequestURI());
				AutoAuthenticationToken token = new AutoAuthenticationToken(username, imei);
				token.setRememberMe(true);
				try {
					currentUser.login(token);
				} catch (AuthenticationException e) {
					log.warn(e.getMessage());
				}
				if (currentUser.isAuthenticated()) {
					// 用户登录成功，将imei数据写如cookie
					cookie.setValue(imei);
					cookie.setHttpOnly(true);
					cookie.setMaxAge(Cookie.ONE_YEAR);
					cookie.saveTo(WebUtils.toHttp(request), WebUtils.toHttp(response));
				}
			}
		}
		return super.isAccessAllowed(request, response, mappedValue);
	}

	@Override
	protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
		if (isLoginRequest(request, response)) {
			if (isLoginSubmission(request, response)) {
				if (log.isTraceEnabled()) {
					log.trace("Login submission detected.  Attempting to execute login.");
				}
				return executeLogin(request, response);
			} else {
				if (log.isTraceEnabled()) {
					log.trace("Login page view.");
				}
				return true;
			}
		} else {
			if (log.isTraceEnabled()) {
				log.trace("Attempting to access a path which requires authentication.  Forwarding to the "
						+ "Authentication url [" + getLoginUrl() + "]");
			}
			log.debug("未登录或登录超时");
			if (isJsonView(WebUtils.toHttp(request))) {
				log.debug("请求json数据");
				setContentType(request, response);
				PrintWriter out = response.getWriter();
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("success", false);
				map.put("message", "未登录或登录超时");
				map.put("timeout", true);
				out.println(JSONObject.fromObject(map).toString());
				out.flush();
				out.close();
			} else {
				log.debug("请求其它数据");
				saveRequestAndRedirectToLogin(request, response);
			}
			return false;
		}
	}

	/**
	 * 判断是否是请求json数据
	 * 
	 * @param request
	 * @return
	 */
	private boolean isJsonView(HttpServletRequest request) {
		String uri = request.getRequestURI();
		if (StringUtils.endsWith(uri, ".json") || StringUtils.equals(request.getParameter("format"), "json")) {
			return true;
		}
		return false;
	}

	private void setContentType(ServletRequest request, ServletResponse response) {
		// 判断是否是IE，IE不支持“application/json”
		String agent = WebUtils.toHttp(request).getHeader("User-Agent");
		if (agent != null && (agent.indexOf("MSIE") != -1 || agent.indexOf("rv:11") != -1)) {
			response.setContentType("text/plain;charset=UTF-8");
		} else {
			response.setCharacterEncoding("UTF-8");
			MediaType mediaType = (MediaType) request.getAttribute(View.SELECTED_CONTENT_TYPE);
			if (mediaType != null && mediaType.isConcrete()) {
				response.setContentType(mediaType.toString());
			} else {
				response.setContentType("application/json");
			}
		}
	}

}
