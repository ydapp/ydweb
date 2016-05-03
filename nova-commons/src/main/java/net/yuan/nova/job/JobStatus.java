package net.yuan.nova.job;

import java.io.Serializable;
import java.util.Date;

/**
 * @author zhangshuai
 * 
 *         用于记录当前job的状态
 *
 */
public class JobStatus implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// 任务的名称
	private String name;
	// 任务组名称
	private String groupName;
	// 任务描述
	private String description;
	// 运行的总次数
	private long total = 0;
	// 第一次运行的时间
	private Date startTime;
	// 下一次运行的时间
	private Date nextFireTime;
	// 上一次运行的时间
	private Date lastFireTime;
	// 上一次运行的时间
	private Date lastEndTime;
	// 任务当前状态
	private State state;
	// 最近一次发生异常的时间
	private Date lastExceptionTime;
	// 最近一次异常的消息
	private String message;
	// 运行的总次数
	private long errorTotal = 0;

	public JobStatus() {
		this.state = State.init;
	}

	public void jobStart() {
		total++;
		this.setState(State.running);
		this.lastFireTime = new Date();
		if (this.startTime == null) {
			this.startTime = this.lastFireTime;
		}
	}

	public void jobStop() {
		this.setState(State.wait);
	}

	/**
	 * 暂停任务 jobPause void
	 */
	public void jobPause() {
		this.setState(State.pause);
	}

	/**
	 * 恢复任务运行 jobRegain void
	 */
	public void jobResume() {
		this.setState(State.wait);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getNextFireTime() {
		return nextFireTime;
	}

	public void setNextFireTime(Date nextFireTime) {
		this.nextFireTime = nextFireTime;
		nextFireTime.clone();
	}

	public Date getLastFireTime() {
		return lastFireTime;
	}

	public void setLastFireTime(Date lastFireTime) {
		this.lastFireTime = lastFireTime;
	}

	public Date getLastEndTime() {
		return lastEndTime;
	}

	public Date getLastExceptionTime() {
		return lastExceptionTime;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public void throwable(Throwable throwable) {
		this.errorTotal++;
		this.lastExceptionTime = new Date();
		this.message = throwable.getMessage();
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public long getErrorTotal() {
		return errorTotal;
	}

	public void setErrorTotal(long errorTotal) {
		this.errorTotal = errorTotal;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	/**
	 * <p>
	 * 定义任务的运行状态
	 * <ul>
	 * <li>wait：等待，即：等待运行，在任务初始化后或任务执行一次调度完成后，等到下一次被运行；</li>
	 * <li>pause：暂停，即：任务暂停，此时不能被调度，需在恢复运行时转换为“wait”状态；</li>
	 * <li>running：运行，即：任务正在运行，任务正在执行相关操作</li>
	 * </ul>
	 * </p>
	 * 
	 * @author 0027005704
	 *
	 */
	public static enum State {
		/**
		 * init：初始化
		 */
		init("初始化"),
		/**
		 * wait：等待运行
		 */
		wait("等待运行"),
		/**
		 * running：任务正在运行
		 */
		running("任务正在运行"),
		/**
		 * stopped：任务已停止
		 */
		stopped("任务已停止"),
		/**
		 * pause:任务暂停
		 */
		pause("任务暂停");

		private String message;

		State(String message) {
			this.message = message;
		}

		public String getMessage() {
			return message;
		}

		public String toString() {
			return message;
		}

	}

}
