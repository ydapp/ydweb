package net.yuan.nova.pis.entity;
/**
 * 楼盘
 * @author leasonlive
 *
 */
public class PisBuilding {
	//所在城市id
	private String cityId;
	//楼盘id
	private String buildingId;
	//楼盘名称
	private String buildingName;
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
	public String getBuildingName() {
		return buildingName;
	}
	public void setBuildingName(String buildingName) {
		this.buildingName = buildingName;
	}
	
	
}
