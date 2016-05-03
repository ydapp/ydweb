package net.yuan.nova.job;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.impl.SchedulerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.yuan.nova.commons.PID;

public class JobInformations implements Serializable {

	private static final Logger logger = LoggerFactory.getLogger(JobInformations.class);

	private static final boolean QUARTZ_AVAILABLE = isQuartzAvailable();
	private static final long serialVersionUID = -2826168112578815952L;
	private final String group;
	private final String name;
	private final String description;
	private final String jobClassName;
	private final Date previousFireTime;
	private final Date nextFireTime;
	private final long elapsedTime;
	private final long repeatInterval;
	private final String cronExpression;
	private final boolean paused;
	private final String globalJobId;

	JobInformations(JobDetail jobDetail, JobExecutionContext jobExecutionContext, Scheduler scheduler)
			throws SchedulerException {
		super();
		assert jobDetail != null;
		assert scheduler != null;
		final QuartzAdapter quartzAdapter = QuartzAdapter.getSingleton();
		this.group = quartzAdapter.getJobGroup(jobDetail);
		this.name = quartzAdapter.getJobName(jobDetail);
		this.description = quartzAdapter.getJobDescription(jobDetail);
		this.jobClassName = quartzAdapter.getJobClass(jobDetail).getName();
		if (jobExecutionContext == null) {
			elapsedTime = -1;
		} else {
			elapsedTime = System.currentTimeMillis() - quartzAdapter.getContextFireTime(jobExecutionContext).getTime();
		}
		final List<Trigger> triggers = quartzAdapter.getTriggersOfJob(jobDetail, scheduler);
		this.nextFireTime = getNextFireTime(triggers);
		this.previousFireTime = getPreviousFireTime(triggers);

		String cronTriggerExpression = null;
		long simpleTriggerRepeatInterval = -1;
		boolean jobPaused = true;
		for (final Trigger trigger : triggers) {
			if (trigger instanceof CronTrigger) {
				cronTriggerExpression = quartzAdapter.getCronTriggerExpression((CronTrigger) trigger);
			} else if (trigger instanceof SimpleTrigger) {
				simpleTriggerRepeatInterval = quartzAdapter.getSimpleTriggerRepeatInterval((SimpleTrigger) trigger);
			}
			jobPaused = jobPaused && quartzAdapter.isTriggerPaused(trigger, scheduler);
		}
		this.repeatInterval = simpleTriggerRepeatInterval;
		this.cronExpression = cronTriggerExpression;
		this.paused = jobPaused;
		this.globalJobId = buildGlobalJobId(jobDetail);
	}

	private static boolean isQuartzAvailable() {
		try {
			Class.forName("org.quartz.Job");
			return true;
		} catch (final ClassNotFoundException e) {
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	static List<JobInformations> buildJobInformationsList() {
		if (!QUARTZ_AVAILABLE) {
			return Collections.emptyList();
		}
		final List<JobInformations> result = new ArrayList<JobInformations>();
		try {
			for (final Scheduler scheduler : getAllSchedulers()) {
				final Map<String, JobExecutionContext> currentlyExecutingJobsByFullName = new LinkedHashMap<String, JobExecutionContext>();
				for (final JobExecutionContext currentlyExecutingJob : (List<JobExecutionContext>) scheduler
						.getCurrentlyExecutingJobs()) {
					final JobDetail jobDetail = QuartzAdapter.getSingleton().getContextJobDetail(currentlyExecutingJob);
					final String jobFullName = QuartzAdapter.getSingleton().getJobFullName(jobDetail);
					currentlyExecutingJobsByFullName.put(jobFullName, currentlyExecutingJob);
				}
				for (final JobDetail jobDetail : getAllJobsOfScheduler(scheduler)) {
					final String jobFullName = QuartzAdapter.getSingleton().getJobFullName(jobDetail);
					final JobExecutionContext jobExecutionContext = currentlyExecutingJobsByFullName.get(jobFullName);
					result.add(new JobInformations(jobDetail, jobExecutionContext, scheduler));
				}
			}
		} catch (final Exception e) {
			throw new IllegalStateException(e);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	static List<Scheduler> getAllSchedulers() {
		return new ArrayList<Scheduler>(SchedulerRepository.getInstance().lookupAll());
	}

	static List<JobDetail> getAllJobsOfScheduler(Scheduler scheduler) {
		try {
			return QuartzAdapter.getSingleton().getAllJobsOfScheduler(scheduler);
		} catch (Exception e) {
			logger.warn(e.getMessage(), e);
			return Collections.emptyList();
		}
	}

	private static Date getPreviousFireTime(List<Trigger> triggers) {
		Date previousFireTime = null;
		for (final Trigger trigger : triggers) {
			final Date triggerPreviousFireTime = QuartzAdapter.getSingleton().getTriggerPreviousFireTime(trigger);
			if (previousFireTime == null || triggerPreviousFireTime != null
					&& previousFireTime.before(triggerPreviousFireTime)) {
				previousFireTime = triggerPreviousFireTime;
			}
		}
		return previousFireTime;
	}

	private static Date getNextFireTime(List<Trigger> triggers) {
		Date nextFireTime = null;
		for (final Trigger trigger : triggers) {
			final Date triggerNextFireTime = QuartzAdapter.getSingleton().getTriggerNextFireTime(trigger);
			if (nextFireTime == null || triggerNextFireTime != null && nextFireTime.after(triggerNextFireTime)) {
				nextFireTime = triggerNextFireTime;
			}
		}
		return nextFireTime;
	}

	public String getGlobalJobId() {
		return globalJobId;
	}

	public String getName() {
		return name;
	}

	public String getGroup() {
		return group;
	}

	public String getDescription() {
		return description;
	}

	public String getJobClassName() {
		return jobClassName;
	}

	public long getElapsedTime() {
		return elapsedTime;
	}

	public boolean isCurrentlyExecuting() {
		return elapsedTime >= 0;
	}

	public Date getNextFireTime() {
		return nextFireTime;
	}

	public Date getPreviousFireTime() {
		return previousFireTime;
	}

	public long getRepeatInterval() {
		return repeatInterval;
	}

	public String getCronExpression() {
		return cronExpression;
	}

	public boolean isPaused() {
		return paused;
	}

	private static String buildGlobalJobId(JobDetail jobDetail) {
		return PID.getPID() + '_' + getHostAddress() + '_'
				+ QuartzAdapter.getSingleton().getJobFullName(jobDetail).hashCode();
	}

	/**
	 * @return 获得机器的IP地址
	 */
	static String getHostAddress() {
		try {
			return InetAddress.getLocalHost().getHostAddress();
		} catch (final UnknownHostException ex) {
			return "unknown";
		}
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[name=" + getName() + ", group=" + getGroup() + ']';
	}
}
