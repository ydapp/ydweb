package net.yuan.nova.job;

import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;

/**
 * <p>
 * 在该类中初始化<code>{@link JobDetail}</code>、<code>{@link CronTrigger}</code>
 * 对象，并记录当前任务的状态
 * </p>
 * 
 * @author 0027005704
 *
 */
public class BettleJobBean implements ApplicationContextAware, InitializingBean, BeanNameAware {

	private final Logger logger = LoggerFactory.getLogger(BettleJobBean.class);

	private String name;

	private String beanName;

	private String groupName = JobManager.GROUP_NAME;

	private String description = "信息服务定时任务";

	/**
	 * 是否自动启动
	 */
	private boolean autoStartup = true;
	private JobStatus jobStatus;
	/**
	 * job的实现类
	 */
	private Class<Job> jobClass;

	private JobDetail jobDetail;

	/**
	 * cron表达式
	 */
	private String cronExpression;

	private CronTrigger cronTrigger;

	private ApplicationContext applicationContext;

	private String applicationContextJobDataKey = "applicationContext";

	public BettleJobBean() {

	}

	public BettleJobBean(Class<Job> jobClass, String cronExpression) {
		this.jobClass = jobClass;
		this.cronExpression = cronExpression;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if(this.beanName == null){
			throw new RuntimeException("beanName不能为空");
		}
		if (this.name == null) {
			this.name = this.beanName;
		}
		logger.debug("当前需要创建的任务:{}", name);
		// /////////////////////////////////////////////
		// ////////////// 实例化JobDetail //////////////
		// /////////////////////////////////////////////
		JobDetailFactoryBean jobDetailFactory = new JobDetailFactoryBean();
		jobDetailFactory.setName(name);
		jobDetailFactory.setGroup(groupName);
		jobDetailFactory.setBeanName(beanName);
		jobDetailFactory.setDescription(description);
		if (this.applicationContext == null) {
			// 没有传入，这里尝试去找一下
			this.applicationContext = JobManager.getApplicationContext();
		}
		jobDetailFactory.setApplicationContext(this.applicationContext);
		jobDetailFactory.setApplicationContextJobDataKey(applicationContextJobDataKey);
		jobDetailFactory.setJobClass(this.getJobClass());
		jobDetailFactory.afterPropertiesSet();
		this.jobDetail = jobDetailFactory.getObject();
		// 设置相应的状态
		JobDataMap jobDataMap = this.jobDetail.getJobDataMap();
		this.jobStatus = new JobStatus();
		this.jobStatus.setName(name);
		this.jobStatus.setGroupName(groupName);
		this.jobStatus.setDescription(this.description);
		jobDataMap.put(JobManager.JOB_STATUS_KEY, this.jobStatus);

		// /////////////////////////////////////////////
		// ///////////// 实例化CronTrigger /////////////
		// /////////////////////////////////////////////
		String cronExpression = this.getCronExpression();
		CronTriggerFactoryBean cronTriggerFactory = new CronTriggerFactoryBean();
		cronTriggerFactory.setCronExpression(cronExpression);
		cronTriggerFactory.setBeanName(beanName);
		cronTriggerFactory.setJobDetail(jobDetail);
		// 触发器的名称和组名
		cronTriggerFactory.setName(name);
		cronTriggerFactory.setGroup(groupName);
		cronTriggerFactory.afterPropertiesSet();
		this.cronTrigger = cronTriggerFactory.getObject();

		// 将该类，添加到任务管理器中
		JobManager.addJob(this);
	}

	@Override
	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isAutoStartup() {
		return autoStartup;
	}

	public void setAutoStartup(boolean autoStartup) {
		this.autoStartup = autoStartup;
	}

	public JobStatus getJobStatus() {
		return jobStatus;
	}

	public void setJobStatus(JobStatus jobStatus) {
		this.jobStatus = jobStatus;
	}

	public Class<Job> getJobClass() {
		return jobClass;
	}

	public void setJobClass(Class<Job> jobClass) {
		this.jobClass = jobClass;
	}

	public JobDetail getJobDetail() {
		return jobDetail;
	}

	public void setJobDetail(JobDetail jobDetail) {
		this.jobDetail = jobDetail;
	}

	public String getCronExpression() {
		return cronExpression;
	}

	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}

	public CronTrigger getCronTrigger() {
		return cronTrigger;
	}

	public void setCronTrigger(CronTrigger cronTrigger) {
		this.cronTrigger = cronTrigger;
	}

}
