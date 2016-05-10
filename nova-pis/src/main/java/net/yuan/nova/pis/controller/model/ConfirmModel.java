package net.yuan.nova.pis.controller.model;
/**
 * 报备信息确认时候提交的数据
 * @author leasonlive
 *
 */
public class ConfirmModel {
	private String recommendId;
	private String confirmUserId;
	private String recommendConfirmAdvice;
	public String getRecommendId() {
		return recommendId;
	}
	public void setRecommendId(String recommendId) {
		this.recommendId = recommendId;
	}
	public String getConfirmUserId() {
		return confirmUserId;
	}
	public void setConfirmUserId(String confirmUserId) {
		this.confirmUserId = confirmUserId;
	}
	public String getRecommendConfirmAdvice() {
		return recommendConfirmAdvice;
	}
	public void setRecommendConfirmAdvice(String recommendConfirmAdvice) {
		this.recommendConfirmAdvice = recommendConfirmAdvice;
	}
	
}
