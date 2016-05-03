package net.yuan.nova.logback.web;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Method;

import javax.servlet.ServletContext;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.util.SystemPropertyUtils;
import org.springframework.web.util.WebUtils;

import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.ext.spring.LogbackConfigurer;

import net.yuan.nova.commons.ConfigurationMgr;

/**
 * 加载logback的配置文件，该类依赖于“ConfigurationMgr”
 * 
 * @author Administrator
 *
 */
public class WebLogbackConfigurer {

	// 默认的配置文件名称
	public static final String DEFAULT_CONFIG_NAME = "logback.xml";
	// 默认的配置文件路径
	public static final String DEFAULT_CONFIG_LOCATION = "/WEB-INF/" + DEFAULT_CONFIG_NAME;

	private WebLogbackConfigurer() {
		super();
	}

	/**
	 * Initialize Logback
	 *
	 * @param servletContext
	 *            the current ServletContext
	 */
	public static void initLogging(ServletContext servletContext) {
		String location = null;
		// 获得配置文件所在目录
		String configPath = ConfigurationMgr.getConfigPath();
		if (StringUtils.isNotBlank(configPath)) {
			if (StringUtils.endsWith(configPath, "/")) {
				location = configPath + DEFAULT_CONFIG_NAME;
			} else {
				location = configPath + "/" + DEFAULT_CONFIG_NAME;
			}
			File configFile = new File(location);
			if (configFile.exists()) {
				location = "file:" + location;
			}
		}
		if (location == null) {
			location = SystemPropertyUtils.resolvePlaceholders(DEFAULT_CONFIG_LOCATION);
		}
		// Perform actual Logback initialization; else rely on Logback's default initialization.
		try {
			// Resolve system property placeholders before potentially resolving real path.

			// Return a URL (e.g. "classpath:" or "file:") as-is;
			// consider a plain file path as relative to the web application root directory.
			if (!ResourceUtils.isUrl(location)) {
				location = WebUtils.getRealPath(servletContext, location);
			}
			// Write log message to server log.
			servletContext.log("Initializing Logback from [" + location + "]");

			// Initialize
			LogbackConfigurer.initLogging(location);
		} catch (FileNotFoundException ex) {
			throw new IllegalArgumentException("Invalid 'logbackConfigLocation' parameter: " + ex.getMessage());
		} catch (JoranException e) {
			throw new RuntimeException("Unexpected error while configuring logback", e);
		}

		// If SLF4J's java.util.logging bridge is available in the classpath,
		// install it. This will direct any messages
		// from the Java Logging framework into SLF4J. When logging is
		// terminated, the bridge will need to be uninstalled
		try {
			Class<?> julBridge = ClassUtils.forName("org.slf4j.bridge.SLF4JBridgeHandler",
					ClassUtils.getDefaultClassLoader());

			Method removeHandlers = ReflectionUtils.findMethod(julBridge, "removeHandlersForRootLogger");
			if (removeHandlers != null) {
				servletContext.log("Removing all previous handlers for JUL to SLF4J bridge");
				ReflectionUtils.invokeMethod(removeHandlers, null);
			}

			Method install = ReflectionUtils.findMethod(julBridge, "install");
			if (install != null) {
				servletContext.log("Installing JUL to SLF4J bridge");
				ReflectionUtils.invokeMethod(install, null);
			}
		} catch (ClassNotFoundException ignored) {
			// Indicates the java.util.logging bridge is not in the classpath.
			// This is not an indication of a problem.
			servletContext.log("JUL to SLF4J bridge is not available on the classpath");
		}
	}

	/**
	 * Shut down Logback
	 *
	 * @param servletContext
	 *            the current ServletContext
	 */
	public static void shutdownLogging(ServletContext servletContext) {
		// Uninstall the SLF4J java.util.logging bridge *before* shutting down
		// the Logback framework.
		try {
			Class<?> julBridge = ClassUtils.forName("org.slf4j.bridge.SLF4JBridgeHandler",
					ClassUtils.getDefaultClassLoader());
			Method uninstall = ReflectionUtils.findMethod(julBridge, "uninstall");
			if (uninstall != null) {
				servletContext.log("Uninstalling JUL to SLF4J bridge");
				ReflectionUtils.invokeMethod(uninstall, null);
			}
		} catch (ClassNotFoundException ignored) {
			// No need to shutdown the java.util.logging bridge. If it's not on
			// the classpath, it wasn't started either.
		}

		servletContext.log("Shutting down Logback");
		LogbackConfigurer.shutdownLogging();
	}

}
