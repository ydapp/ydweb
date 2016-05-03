package net.yuan.nova.logback.web;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class LogbackConfigListener implements ServletContextListener {

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		WebLogbackConfigurer.shutdownLogging(event.getServletContext());
	}

	@Override
	public void contextInitialized(ServletContextEvent event) {
		WebLogbackConfigurer.initLogging(event.getServletContext());
	}
}
