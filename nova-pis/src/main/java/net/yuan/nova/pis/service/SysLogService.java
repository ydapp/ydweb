package net.yuan.nova.pis.service;

import java.util.List;

import net.yuan.nova.pis.dao.PisSysLogMapper;
import net.yuan.nova.pis.entity.PisSysLog;
import net.yuan.nova.pis.entity.vo.PisSysLogVo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 
 * @author zhangshuai
 *
 */
@Service
public class SysLogService {
	@Autowired
	PisSysLogMapper logBackDao;

	/**
	 * 查询日志信息分页列表
	 * 
	 * @param logBackVo
	 * @return
	 */
	public List<PisSysLog> findLogList(PisSysLogVo logBackVo) {
		return logBackDao.findLogList(logBackVo);
	}

	/**
	 * 查询总数
	 * 
	 * @param logBackVo
	 * @return
	 */
	public int findLogCount(PisSysLog logBackVo) {
		return logBackDao.findLogCount(logBackVo);

	}

	/**
	 * 批量删除日志信息
	 * 
	 * @param
	 * @return
	 */
	public void deleteBatchLog(String[] sysLogIds) {
		for (String sysLogId : sysLogIds) {
			logBackDao.deleteBatchLog(sysLogId);
		}
	}

	/**
	 * 删除数据库所有日志
	 * 
	 * @param
	 * @return
	 */
	public void deleteAllLog() {
		logBackDao.deleteAllLog();
	}

}
