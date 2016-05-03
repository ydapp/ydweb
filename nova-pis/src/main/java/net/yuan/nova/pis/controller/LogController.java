package net.yuan.nova.pis.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.yuan.nova.core.vo.DataGridData;
import net.yuan.nova.pis.entity.PisSysLog;
import net.yuan.nova.pis.entity.vo.PisSysLogVo;
import net.yuan.nova.pis.service.SysLogService;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 
 * @author zhangshuai
 *
 */
@Controller
public class LogController {
	@Autowired
	SysLogService sysLogService;

	/**
	 * 查询日志信息分页列表
	 * 
	 * @return
	 */
	@RequestMapping("/admin/logList")
	public @ResponseBody Object logBackfindList(PisSysLogVo logBackVo, ModelMap modelMap) {
		if (logBackVo.getPage() == null || logBackVo.getPage() < 1) {
			logBackVo.setPage(1);
		}
		if (logBackVo.getRows() == null || logBackVo.getRows() < 1) {
			logBackVo.setRows(10);
		}
		logBackVo.setOffset((logBackVo.getPage() - 1) * logBackVo.getRows());
		logBackVo.setEndRowNum(logBackVo.getPage() * logBackVo.getRows());

		DataGridData<PisSysLog> dataGridData = new DataGridData<PisSysLog>();
		List<PisSysLog> list = sysLogService.findLogList(logBackVo);
		int total = sysLogService.findLogCount(logBackVo);
		dataGridData.setTotal(total);
		dataGridData.setRows(list);
		return dataGridData;
	}

	/**
	 * 批量删除日志信息
	 * 
	 * @param
	 * @return
	 */
	@RequestMapping("/admin/delBatchLog")
	public void logBackBatchDel(HttpServletRequest request) {
		String[] sysLogIds = StringUtils.trimToEmpty(request.getParameter("sysLogIds")).split(",");
		sysLogService.deleteBatchLog(sysLogIds);
	}

	/**
	 * 删除数据库所有的日志
	 * 
	 * @param
	 * @return
	 */
	@RequestMapping("/admin/delAllLog")
	public void logBackAllDel() {
		sysLogService.deleteAllLog();
	}
}
