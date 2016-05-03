package net.yuan.nova.job;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QuartzAdapter {

	private static final Logger logger = LoggerFactory.getLogger(QuartzAdapter.class);

	private static final QuartzAdapter SINGLETON = createSingleton();

	protected QuartzAdapter() {
		super();
	}

	static QuartzAdapter getSingleton() {
		return SINGLETON;
	}

	private static QuartzAdapter createSingleton() {
		return new QuartzAdapter();
	}

	String getJobName(JobDetail jobDetail) {
		return jobDetail.getName();
	}

	String getJobGroup(JobDetail jobDetail) {
		return jobDetail.getGroup();
	}

	String getJobFullName(JobDetail jobDetail) {
		return getJobGroup(jobDetail) + '.' + getJobName(jobDetail);
	}

	String getJobDescription(JobDetail jobDetail) {
		return jobDetail.getDescription();
	}

	Class<?> getJobClass(JobDetail jobDetail) {
		return jobDetail.getJobClass();
	}

	Date getTriggerPreviousFireTime(Trigger trigger) {
		return trigger.getPreviousFireTime();
	}

	Date getTriggerNextFireTime(Trigger trigger) {
		return trigger.getNextFireTime();
	}

	String getCronTriggerExpression(CronTrigger trigger) {
		return trigger.getCronExpression(); // NOPMD
	}

	long getSimpleTriggerRepeatInterval(SimpleTrigger trigger) {
		return trigger.getRepeatInterval(); // NOPMD
	}

	JobDetail getContextJobDetail(JobExecutionContext context) {
		return context.getJobDetail();
	}

	Date getContextFireTime(JobExecutionContext context) {
		return context.getFireTime();
	}

	List<JobDetail> getAllJobsOfScheduler(Scheduler scheduler) throws SchedulerException {
		final List<JobDetail> result = new ArrayList<JobDetail>();
		for (final String jobGroupName : scheduler.getJobGroupNames()) {
			for (final String jobName : scheduler.getJobNames(jobGroupName)) {
				final JobDetail jobDetail;
				try {
					jobDetail = scheduler.getJobDetail(jobName, jobGroupName);
					if (jobDetail != null) {
						result.add(jobDetail);
					}
				} catch (Exception e) {
					logger.error(e.toString(), e);
				}
			}
		}
		return result;
	}

	List<Trigger> getTriggersOfJob(JobDetail jobDetail, Scheduler scheduler) throws SchedulerException {
		return Arrays.asList(scheduler.getTriggersOfJob(jobDetail.getName(), jobDetail.getGroup()));
	}

	boolean isTriggerPaused(Trigger trigger, Scheduler scheduler) throws SchedulerException {
		return scheduler.getTriggerState(trigger.getName(), trigger.getGroup()) == Trigger.STATE_PAUSED;
	}

	void pauseJob(JobDetail jobDetail, Scheduler scheduler) throws SchedulerException {
		scheduler.pauseJob(jobDetail.getName(), jobDetail.getGroup());
	}

	void resumeJob(JobDetail jobDetail, Scheduler scheduler) throws SchedulerException {
		scheduler.resumeJob(jobDetail.getName(), jobDetail.getGroup());
	}
}
