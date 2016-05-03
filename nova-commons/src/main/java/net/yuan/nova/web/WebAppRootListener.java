package net.yuan.nova.web;


import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.util.WebUtils;

import net.yuan.nova.commons.SystemConstant;

public class WebAppRootListener implements ServletContextListener {

	public static String WEB_APP_ROOT_KEY = null;

	@Override
	public void contextInitialized(ServletContextEvent event) {
		setWebAppRootSystemProperty(event.getServletContext());
	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		removeWebAppRootSystemProperty(event.getServletContext());
	}

	public static void setWebAppRootSystemProperty(ServletContext servletContext) throws IllegalStateException {
		Assert.notNull(servletContext, "ServletContext must not be null");
		String root = servletContext.getRealPath("/");
		if (root == null) {
			throw new IllegalStateException("Cannot set web app root system property when WAR file is not expanded");
		}
		String key = getWebAppRootKey(servletContext);
		String oldValue = System.getProperty(key);
		if (oldValue != null && !StringUtils.pathEquals(oldValue, root)) {
			throw new IllegalStateException("Web app root system property already set to different value: '" + key
					+ "' = [" + oldValue + "] instead of [" + root + "] - "
					+ "Choose unique values for the 'webAppRootKey' context-param in your web.xml files!");
		}
		System.setProperty(key, root);
		servletContext.log("Set web app root system property: '" + key + "' = [" + root + "]");
	}

	public static void removeWebAppRootSystemProperty(ServletContext servletContext) {
		Assert.notNull(servletContext, "ServletContext must not be null");
		String key = getWebAppRootKey(servletContext);
		System.getProperties().remove(key);
	}

	private static String getWebAppRootKey(ServletContext servletContext) {
		if (StringUtils.isEmpty(WEB_APP_ROOT_KEY)) {
			String param = servletContext.getInitParameter(WebUtils.WEB_APP_ROOT_KEY_PARAM);
			String key = (param != null ? param : WebUtils.DEFAULT_WEB_APP_ROOT_KEY);
			WEB_APP_ROOT_KEY = new StringBuilder().append(key).append("_").append(System.currentTimeMillis()).toString();
			SystemConstant.WEBAPP_ROOT = WEB_APP_ROOT_KEY;
		}
		return WEB_APP_ROOT_KEY;
	}

}
