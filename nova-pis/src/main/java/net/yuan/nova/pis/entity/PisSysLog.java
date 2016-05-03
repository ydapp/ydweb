package net.yuan.nova.pis.entity;

import java.io.Serializable;

/**
 * 日志信息实体类
 * 
 * @author zhangshuai
 *
 */
public class PisSysLog implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String sysLogId;// 日志标识
	private String logCotnent;// 日志内容
	private String logLevel;// 日志级别
	private String genPos;// 发生位置
	private String threadName;// 线程名称
	private String comments;// 说明
	private String createDate;// 创建时间
	private String loggerName;
	private String callerFilename;

	public String getSysLogId() {
		return sysLogId;
	}

	public void setSysLogId(String sysLogId) {
		this.sysLogId = sysLogId;
	}

	public String getLogCotnent() {
		return logCotnent;
	}

	public void setLogCotnent(String logCotnent) {
		this.logCotnent = logCotnent;
	}

	public String getLogLevel() {
		return logLevel;
	}

	public void setLogLevel(String logLevel) {
		this.logLevel = logLevel;
	}

	public String getGenPos() {
		return genPos;
	}

	public void setGenPos(String genPos) {
		this.genPos = genPos;
	}

	public String getThreadName() {
		return threadName;
	}

	public void setThreadName(String threadName) {
		this.threadName = threadName;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public String getLoggerName() {
		return loggerName;
	}

	public void setLoggerName(String loggerName) {
		this.loggerName = loggerName;
	}

	public String getCallerFilename() {
		return callerFilename;
	}

	public void setCallerFilename(String callerFilename) {
		this.callerFilename = callerFilename;
	}

}
