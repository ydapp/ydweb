package net.yuan.nova.pis.entity.vo;

import net.yuan.nova.pis.entity.PisSysLog;

/**
 * 用于日志查询的实体
 * 
 * @author zhangshuai
 *
 */
public class PisSysLogVo extends PisSysLog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// 分页
	private Integer page;// 当前页
	private Integer rows;// size
	private Integer offset;
	private Integer endRowNum;
	private String startTime;
	private String endTime;

	public Integer getPage() {
		return page;
	}

	public void setPage(Integer page) {
		this.page = page;
	}

	public Integer getRows() {
		return rows;
	}

	public void setRows(Integer rows) {
		this.rows = rows;
	}

	public Integer getOffset() {
		return offset;
	}

	public void setOffset(Integer offset) {
		this.offset = offset;
	}

	public Integer getEndRowNum() {
		return endRowNum;
	}

	public void setEndRowNum(Integer endRowNum) {
		this.endRowNum = endRowNum;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

}
