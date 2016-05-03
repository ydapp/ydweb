package net.yuan.nova.pis.entity;

import java.util.Date;

/**
 * 推荐
 * 
 * @author leasonlive
 *
 */
public class PisRecommend {
	// 推荐id
	private String recommendId;
	// 客户名称
	private String customerName;
	// 客户电话
	private String customerTel;
	// 城市id
	private String cityId;
	// 楼盘id
	private String buildingId;
	// 预计看房时间
	private Date appointmentLookHouseDate;
	// 推荐人
	private String refreeId;
	// 备注
	private String remark;
	// 推荐状态三种状态，recommend推荐，present到场，confirm确认
	private Status status;
	// 推荐时间
	private Date recommendDate;
	// 客户到场时间
	private Date customerPresentDate;
	// 客户到场确认人
	private String customerPresentUserId;
	// 推荐确认时间
	private Date recommendConfirmDate;
	// 推荐确认人
	private String recommendConfirmUserId;

	public String getRecommendId() {
		return recommendId;
	}

	public void setRecommendId(String recommendId) {
		this.recommendId = recommendId;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getCustomerTel() {
		return customerTel;
	}

	public void setCustomerTel(String customerTel) {
		this.customerTel = customerTel;
	}

	public String getCityId() {
		return cityId;
	}

	public void setCityId(String cityId) {
		this.cityId = cityId;
	}

	public String getBuildingId() {
		return buildingId;
	}

	public void setBuildingId(String buildingId) {
		this.buildingId = buildingId;
	}

	public Date getAppointmentLookHouseDate() {
		return appointmentLookHouseDate;
	}

	public void setAppointmentLookHouseDate(Date appointmentLookHouseDate) {
		this.appointmentLookHouseDate = appointmentLookHouseDate;
	}

	public String getRefreeId() {
		return refreeId;
	}

	public void setRefreeId(String refreeId) {
		this.refreeId = refreeId;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Date getRecommendDate() {
		return recommendDate;
	}

	public void setRecommendDate(Date recommendDate) {
		this.recommendDate = recommendDate;
	}

	public Date getCustomerPresentDate() {
		return customerPresentDate;
	}

	public void setCustomerPresentDate(Date customerPresentDate) {
		this.customerPresentDate = customerPresentDate;
	}

	public String getCustomerPresentUserId() {
		return customerPresentUserId;
	}

	public void setCustomerPresentUserId(String customerPresentUserId) {
		this.customerPresentUserId = customerPresentUserId;
	}

	public Date getRecommendConfirmDate() {
		return recommendConfirmDate;
	}

	public void setRecommendConfirmDate(Date recommendConfirmDate) {
		this.recommendConfirmDate = recommendConfirmDate;
	}

	public String getRecommendConfirmUserId() {
		return recommendConfirmUserId;
	}

	public void setRecommendConfirmUserId(String recommendConfirmUserId) {
		this.recommendConfirmUserId = recommendConfirmUserId;
	}

	public static enum Status {
		// 经纪人申请报备
		appointment,
		// 客户到现场，经纪人进行到场确认
		present,
		// 报备专员进行最终确认
		confirm
	}
	public String getStatusName(){
		switch (this.status) {
		case appointment:
			return "报备申请";
		case present:
			return "客户到场";
		case confirm:
			return "报备确认";
		}
		return "";
	}

}
