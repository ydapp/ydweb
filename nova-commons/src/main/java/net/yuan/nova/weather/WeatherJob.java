package net.yuan.nova.weather;

import org.quartz.JobExecutionContext;
import org.springframework.context.ApplicationContext;

import net.yuan.nova.job.AbstractJob;

/**
 * 主动更新天气天气数据的任务
 * 
 * @author zhangshuai
 *
 */
public class WeatherJob extends AbstractJob {

	private static String WEATHERMANAGER_NAME = "weatherManager";

	@Override
	protected void onExecute(JobExecutionContext context) throws Exception {
		ApplicationContext ctx = this.getApplicationContext();
		WeatherManager weatherManager = (WeatherManager) ctx.getBean(WEATHERMANAGER_NAME);
		// 刷新默认城市的天气信息
		weatherManager.refresh();
	}

}