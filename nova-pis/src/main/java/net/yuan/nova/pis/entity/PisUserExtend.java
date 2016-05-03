package net.yuan.nova.pis.entity;
/**
 * 用户扩展表，用户记录该用户所关联的楼盘和经纪公司数据
 * @author leasonlive
 *
 */
public class PisUserExtend {
	private String userId;
	private String buildingId;
	private String brokingFirmId;
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getBuildingId() {
		return buildingId;
	}
	public void setBuildingId(String buildingId) {
		this.buildingId = buildingId;
	}
	public String getBrokingFirmId() {
		return brokingFirmId;
	}
	public void setBrokingFirmId(String brokingFirmId) {
		this.brokingFirmId = brokingFirmId;
	}
	
}
