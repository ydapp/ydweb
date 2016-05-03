package net.yuan.nova.job;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.quartz.CronTrigger;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

/**
 * <p>
 * 用于管理项目的所有定时任务，项目中的定时任务统一使用。在该例中没有用到定时任务的监听器。<br>
 * 需要添加任务时，创建<code>{@link BettleJobBean}</code>对象并调用
 * <code>afterPropertiesSet()</code>方法即可。 <code>
 * 		BettleJobBean jobBean = new BettleJobBean();
 * 		jobBean.setName("testJob");
 * 		jobBean.setBeanName("testJobBean");
 * 		Class<Job> clazz = (Class<Job>) Class.forName("net.yuan.nova.job.SimpleJob");
 * 		jobBean.setJobClass(clazz);
 * 		jobBean.setCronExpression("0 0/5 * * * ?");
 * 		jobBean.setApplicationContext(this.applicationContext);
 * 		jobBean.afterPropertiesSet();
 * </code>
 * </p>
 * 
 * @author zhangshuai
 * 
 */

public class JobManager implements DisposableBean, ApplicationListener<ContextRefreshedEvent>, ApplicationContextAware {

	private static final Logger logger = LoggerFactory.getLogger(JobManager.class);

	public static final String JOB_STATUS_KEY = "jobStatus";

	public static final String GROUP_NAME = "BETTLE";

	private static final String SCHEDULER_NAME = "BETTLE_SCHEDULER";

	private static final Map<String, BettleJobBean> bettleJobs = new ConcurrentHashMap<String, BettleJobBean>();

	private static Scheduler scheduler;

	private static SchedulerFactoryBean schedulerFactory;

	private int startupDelay = 20;

	private boolean waitForJobsToCompleteOnShutdown = false;

	private static ApplicationContext applicationContext;

	/**
	 * 获取所有任务的状态
	 * 
	 * @return
	 */
	public static List<JobStatus> getJobStatuses() {
		if (JobManager.bettleJobs.isEmpty()) {
			return Collections.emptyList();
		}
		List<JobStatus> list = new ArrayList<JobStatus>();
		Collection<BettleJobBean> values = JobManager.bettleJobs.values();
		for (BettleJobBean bettleJob : values) {
			JobStatus jobStatus = bettleJob.getJobStatus();
			list.add(jobStatus);
		}
		return list;
	}

	/**
	 * 获得调度器工厂实例
	 * 
	 * @return
	 */
	private static SchedulerFactoryBean getSchedulerFactory() {
		if (schedulerFactory == null) {
			schedulerFactory = new SchedulerFactoryBean();
			schedulerFactory.setSchedulerName(SCHEDULER_NAME);
			// 在存储库中暴露调度程序
			schedulerFactory.setExposeSchedulerInRepository(true);
		}
		return schedulerFactory;
	}

	/**
	 * 获得当前的调度对象
	 * 
	 * @return
	 * @throws SchedulerException
	 */
	public static Scheduler getScheduler() throws SchedulerException {
		if (scheduler == null) {
			scheduler = getSchedulerFactory().getScheduler();
		}
		return scheduler;
	}

	/**
	 * 当一个ApplicationContext被初始化或刷新触发
	 */
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		ApplicationContext context = event.getApplicationContext();
		if (isRoot(context)) {
			// 跟容器初始化完成
			try {
				start(context);
			} catch (Exception e) {
				logger.error("启动定时任务时出错", e);
			}
		}
	}

	protected void start(ApplicationContext context) throws Exception {
		if (scheduler != null) {
			if (!scheduler.isShutdown()) {
				// 没有被关闭
				schedulerFactoryStart(context);
			}
		} else {
			schedulerFactoryStart(context);
		}
	}

	private Scheduler schedulerFactoryStart(ApplicationContext context) throws Exception {
		if (JobManager.bettleJobs.isEmpty()) {
			return scheduler;
		}
		List<Trigger> triggerList = new ArrayList<Trigger>();
		Collection<BettleJobBean> values = JobManager.bettleJobs.values();
		for (BettleJobBean bettleJob : values) {
			if (bettleJob.isAutoStartup()) {
				CronTrigger cronTrigger = bettleJob.getCronTrigger();
				// 添加入列表
				triggerList.add(cronTrigger);
				// 修改状态
				JobStatus jobStatus = bettleJob.getJobStatus();
				jobStatus.setState(JobStatus.State.wait);
			}
		}
		if (triggerList != null && triggerList.size() > 0) {
			// /////////////////////////////////////////////
			// ////////// 实例化Scheduler并启动任务 /////////
			// /////////////////////////////////////////////
			SchedulerFactoryBean sfb = getSchedulerFactory();
			sfb.setBeanName("bettleScheduler");
			sfb.setApplicationContext(context);
			// 自动开始
			sfb.setAutoStartup(true);
			// 延迟30秒
			sfb.setStartupDelay(this.startupDelay);
			Trigger[] triggers = new Trigger[triggerList.size()];
			sfb.setTriggers(triggerList.toArray(triggers));
			// 设置完成
			sfb.afterPropertiesSet();
			sfb.start();
			scheduler = sfb.getObject();
		}
		return scheduler;
	}

	private boolean isRoot(ApplicationContext context) {
		return StringUtils.endsWith(context.getId(), context.getApplicationName());
	}

	/**
	 * 当类被销毁时是触发
	 */
	@Override
	public void destroy() throws Exception {
		logger.info("Shutting down Quartz Scheduler");
		if (scheduler != null) {
			SchedulerFactoryBean schedulerFactory = getSchedulerFactory();
			if (schedulerFactory.isRunning()) {
				schedulerFactory.stop();
			}
			scheduler.shutdown(this.waitForJobsToCompleteOnShutdown);
		}
		JobManager.scheduler = null;
	}

	// /////////////////////////////////////////////////
	// ////////////以下是定时任务的操作//////////////////
	// /////////////////////////////////////////////////

	/**
	 * 立刻触发一个任务执行
	 * 
	 * @param jobName
	 *            触发器名称
	 * @param groupName
	 *            触发器组名
	 * @throws SchedulerException
	 *             void
	 */
	public static void triggerJob(String jobName, String groupName) throws SchedulerException {
		if (scheduler == null) {
			return;
		}
		scheduler.triggerJob(jobName, groupName);
	}

	/**
	 * <p>
	 * 添加新的任务，该方法在<code>{@link BettleJobBean.afterPropertiesSet}</code>方法中被调用
	 * </p>
	 * 
	 * @param job
	 * @throws SchedulerException
	 */
	static void addJob(BettleJobBean job) throws SchedulerException {
		JobManager.bettleJobs.put(job.getName(), job);

		// 判断调度任务是否已经开始运行
		if (scheduler != null && job.isAutoStartup()) {
			// 判断调度任务是否已经添加进去
			if (!triggerExists(job.getCronTrigger())) {
				scheduler.scheduleJob(job.getJobDetail(), job.getCronTrigger());
			} else {
				logger.info("任务【{}:{}】已经存在", job.getGroupName(), job.getName());
			}
			JobStatus jobStatus = job.getJobStatus();
			jobStatus.setState(JobStatus.State.wait);
		}
	}

	/**
	 * <p>
	 * 针对没有自动启动的任务
	 * <p>
	 * 
	 * @param jobName
	 * @param groupName
	 * @return
	 * @throws SchedulerException
	 */
	public static boolean startJob(String jobName, String groupName) throws SchedulerException {
		if (JobManager.bettleJobs.isEmpty()) {
			return false;
		}
		BettleJobBean job = bettleJobs.get(jobName);
		if (job == null) {
			return false;
		}
		JobStatus jobStatus = job.getJobStatus();
		// 任务的状体为初始化
		if (jobStatus.getState().equals(JobStatus.State.init)) {
			scheduler.scheduleJob(job.getJobDetail(), job.getCronTrigger());
		}
		jobStatus.setState(JobStatus.State.wait);
		return true;
	}

	private static boolean triggerExists(Trigger trigger) throws SchedulerException {
		return triggerExists(trigger.getName(), trigger.getGroup());
	}

	private static boolean triggerExists(String triggerName, String groupName) throws SchedulerException {
		return scheduler.getTrigger(triggerName, groupName) != null;
	}

	/**
	 * <p>
	 * 从调度程序中删除指定任务
	 * </p>
	 * 
	 * @param jobName
	 *            任务名称，这里触发器名称和任务名称相同
	 * @param groupName
	 *            任务所在的组名
	 * @return
	 * @throws SchedulerException
	 */
	public static boolean removeJob(String jobName, String groupName) throws SchedulerException {
		bettleJobs.remove(jobName);
		if (scheduler == null) {
			return false;
		}
		// 这里触发器名称和任务名称相同
		String triggerName = jobName;
		// 暂停任务对应的触发器
		scheduler.pauseTrigger(triggerName, groupName);
		// 删除任务对应的触发器
		scheduler.unscheduleJob(triggerName, groupName);
		// 将任务从调度任务中删除
		return scheduler.deleteJob(jobName, groupName);
	}

	/**
	 * <p>
	 * 暂停任务
	 * </p>
	 * 
	 * @param jobkey
	 * @throws SchedulerException
	 */
	public static void pauseJob(String jobName, String groupName) throws SchedulerException {
		if (scheduler == null) {
			return;
		}
		scheduler.pauseJob(jobName, groupName);
		scheduler.pauseTrigger(jobName, groupName);
		// 修改任务状态为暂停状态
		BettleJobBean bettleJobBean = bettleJobs.get(jobName);
		if (bettleJobBean != null)
			bettleJobBean.getJobStatus().jobPause();
	}

	/**
	 * <p>
	 * 恢复暂停的任务
	 * </p>
	 * 
	 * @param jobkey
	 * @throws SchedulerException
	 */
	public static void resumeJob(String jobName, String groupName) throws SchedulerException {
		if (scheduler == null) {
			return;
		}
		if (triggerExists(jobName, groupName)) {
			scheduler.resumeJob(jobName, groupName);
			scheduler.resumeTrigger(jobName, groupName);
			// 恢复任务状态为暂停状态
			BettleJobBean bettleJobBean = bettleJobs.get(jobName);
			if (bettleJobBean != null)
				bettleJobBean.getJobStatus().jobResume();
		} else {
			logger.warn("预回复的任务不存在，可能以备删除");
			bettleJobs.remove(jobName);
		}
	}

	/**
	 * 暂停调度中所有的job任务
	 * 
	 * @throws SchedulerException
	 */
	public static void pauseAll() throws SchedulerException {
		if (scheduler != null) {
			scheduler.pauseAll();
		}
		if (!JobManager.bettleJobs.isEmpty()) {
			Collection<BettleJobBean> values = JobManager.bettleJobs.values();
			for (BettleJobBean bettleJob : values) {
				bettleJob.getJobStatus().jobPause();
			}
		}
	}

	/**
	 * 恢复调度中所有的job的任务
	 * 
	 * @throws SchedulerException
	 */
	public static void resumeAll() throws SchedulerException {
		if (scheduler != null) {
			scheduler.resumeAll();
		}
		if (!JobManager.bettleJobs.isEmpty()) {
			Collection<BettleJobBean> values = JobManager.bettleJobs.values();
			for (BettleJobBean bettleJob : values) {
				bettleJob.getJobStatus().jobResume();
			}
		}
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		JobManager.applicationContext = applicationContext;
	}

	protected static ApplicationContext getApplicationContext() {
		return applicationContext;
	}
}
