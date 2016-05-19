package net.yuan.nova.pis.controller.model;
/**
 * 追加详情的参数表
 * @author leasonlive
 *
 */
public class AppendRemarkModel {
	private String recommendId;
	private String appendUserId;
	private String recommendAppendRemark;
	public String getRecommendId() {
		return recommendId;
	}
	public void setRecommendId(String recommendId) {
		this.recommendId = recommendId;
	}
	public String getAppendUserId() {
		return appendUserId;
	}
	public void setAppendUserId(String appendUserId) {
		this.appendUserId = appendUserId;
	}
	public String getRecommendAppendRemark() {
		return recommendAppendRemark;
	}
	public void setRecommendAppendRemark(String recommendAppendRemark) {
		this.recommendAppendRemark = recommendAppendRemark;
	}
	
}
