package net.yuan.nova.job;

import net.sf.json.JSONArray;

import org.quartz.JobExecutionContext;

public class SimpleJob extends AbstractJob {

	@Override
	protected void onExecute(JobExecutionContext context) throws Exception {

		// 获得当前任务的状态
		this.getJobStatus();
		// 获得所有任务的运行状态
		System.out.println(JSONArray.fromObject(JobManager.getJobStatuses()));
	}

}
