package net.yuan.nova.pis.dao;

import java.util.List;

import net.yuan.nova.pis.entity.PisSysLog;
import net.yuan.nova.pis.entity.vo.PisSysLogVo;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * 
 * @author dengxiaoming
 *
 */
@Repository
public interface PisSysLogMapper {
	/**
	 * 查询日志信息分页列表
	 * 
	 * @param logBackVo
	 * @return
	 */
	public List<PisSysLog> findLogList(PisSysLogVo logBackVo);

	/**
	 * 查询总数
	 * 
	 * @param logBackVo
	 * @return
	 */
	public int findLogCount(PisSysLog logBackVo);

	/**
	 * 批量删除日志信息
	 * 
	 * @param
	 * @return
	 */
	public void deleteBatchLog(@Param("sysLogId") String sysLogId);

	/**
	 * 删除数据库所有日志
	 * 
	 * @param
	 * @return
	 */
	public void deleteAllLog();
}
