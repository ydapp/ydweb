package net.yuan.nova.pis.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.yuan.nova.core.vo.DataGridData;
import net.yuan.nova.job.JobManager;
import net.yuan.nova.job.JobStatus;

import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class JobManagerController {

	protected final Logger log = LoggerFactory.getLogger(JobManagerController.class);

	/**
	 * 定时任务状态
	 * 
	 * @return
	 */
	@RequestMapping(value = "/admin/tasks")
	public Object getAllJobStatus() {
		List<JobStatus> list = JobManager.getJobStatuses();
		DataGridData<JobStatus> dataGridData = new DataGridData<JobStatus>();
		if (list != null) {
			dataGridData.setRows(list);
			dataGridData.setTotal(list.size());
		}
		return dataGridData;
	}

	/**
	 * 启动任务 manualStart
	 * 
	 * @param request
	 * @param modelMap
	 * @return Object
	 */
	@RequestMapping(value = "/admin/job/start")
	public Object startJob(HttpServletRequest request, ModelMap modelMap) {
		String jobClassName = request.getParameter("name");
		String groupName = request.getParameter("groupName");
		try {
			JobManager.startJob(jobClassName, groupName);
			modelMap.addAttribute("message", "启动成功");
			modelMap.addAttribute("success", true);
		} catch (SchedulerException e) {
			modelMap.addAttribute("message", "启动失败");
			modelMap.addAttribute("success", false);
			log.error("启动失败,{}", e.getMessage());
		}
		return null;
	}

	/**
	 * 暂停任务状态 suspendJob
	 * 
	 * @param request
	 * @param modelMap
	 * @return Object
	 */
	@RequestMapping(value = "/admin/job/pause")
	public Object pauseJob(HttpServletRequest request, ModelMap modelMap) {
		String jobClassName = request.getParameter("name");
		String groupName = request.getParameter("groupName");
		try {
			JobManager.pauseJob(jobClassName, groupName);
			modelMap.addAttribute("message", "暂停成功");
			modelMap.addAttribute("success", true);
		} catch (SchedulerException e) {
			log.error("暂停任务运行失败:{}", e.getMessage());
			modelMap.addAttribute("message", "暂停失败");
			modelMap.addAttribute("success", false);
		}
		return null;
	}

	/**
	 * 恢复任务运行 resumeJob
	 * 
	 * @param request
	 * @param modelMap
	 * @return Object
	 */
	@RequestMapping(value = "/admin/job/resume")
	public Object resumeJob(HttpServletRequest request, ModelMap modelMap) {
		String jobClassName = request.getParameter("name");
		String groupName = request.getParameter("groupName");
		try {
			JobManager.resumeJob(jobClassName, groupName);
			modelMap.addAttribute("message", "任务已继续运行");
			modelMap.addAttribute("success", true);
		} catch (SchedulerException e) {
			log.error("恢复任务运行失败:{}", e.getMessage());
			modelMap.addAttribute("message", "恢复任务运行失败");
			modelMap.addAttribute("success", false);
		}
		return null;
	}
}
