package net.yuan.nova.pis.controller.model;

import java.util.Date;
/**
 * 推荐人信息
 * @author leasonlive
 *
 */
public class CustomerRefreeInfo {
	//推荐id
	private String recommendId;
	//推荐人id
	private String refreeUserId;
	//推荐人名称
	private String refreeUserName;
	//推荐日期
	private Date refreeDate;
	//推荐状态
	private String statusTitle;
	
	public String getRecommendId() {
		return recommendId;
	}
	public void setRecommendId(String recommendId) {
		this.recommendId = recommendId;
	}
	public String getRefreeUserId() {
		return refreeUserId;
	}
	public void setRefreeUserId(String refreeUserId) {
		this.refreeUserId = refreeUserId;
	}
	public String getRefreeUserName() {
		return refreeUserName;
	}
	public void setRefreeUserName(String refreeUserName) {
		this.refreeUserName = refreeUserName;
	}
	public Date getRefreeDate() {
		return refreeDate;
	}
	public void setRefreeDate(Date refreeDate) {
		this.refreeDate = refreeDate;
	}
	public String getStatusTitle() {
		return statusTitle;
	}
	public void setStatusTitle(String statusTitle) {
		this.statusTitle = statusTitle;
	}
	
	
}
