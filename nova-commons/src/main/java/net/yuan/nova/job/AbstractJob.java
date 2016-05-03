package net.yuan.nova.job;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.StatefulJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

public abstract class AbstractJob implements StatefulJob {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private JobStatus jobStatus;

	private ApplicationContext applicationContext;

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
		// 获得当前的容器
		applicationContext = (ApplicationContext) jobDataMap.get("applicationContext");
		// 获得运行状态
		jobStatus = (JobStatus) jobDataMap.get(JobManager.JOB_STATUS_KEY);
		if (jobStatus == null) {
			jobStatus = new JobStatus();
			jobDataMap.put(JobManager.JOB_STATUS_KEY, jobStatus);
		}
		try {
			if (beforeExecute(context)) {
				jobStatus.jobStart();
				synchronized (jobDataMap) {
					onExecute(context);
				}
				afterExecute(context);
			}
		} catch (Exception e) {
			jobStatus.throwable(e);
			logger.error("任务在调用中出现异常", e);
		} finally {
			jobStatus.jobStop();
			logger.trace("第{}次运行已结束", jobStatus.getTotal());
		}

	}

	/**
	 * 获得当前job执行的状态数据
	 * 
	 * @return
	 */
	public JobStatus getJobStatus() {
		return jobStatus;
	}

	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	/**
	 * 任务执行之前触发，若返回false，则不运行相应的job
	 * 
	 * @return
	 */
	protected boolean beforeExecute(JobExecutionContext context) {
		return true;
	}

	/**
	 * job需要执行的具体任务
	 * 
	 * @param context
	 */
	protected abstract void onExecute(JobExecutionContext context) throws Exception;

	/**
	 * 具体任务执行完成以后
	 * 
	 * @param context
	 */
	protected void afterExecute(JobExecutionContext context) {

	}

}
