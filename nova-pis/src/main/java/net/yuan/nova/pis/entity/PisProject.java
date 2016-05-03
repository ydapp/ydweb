package net.yuan.nova.pis.entity;
/**
 * 对于老百姓来说是楼盘，对于房地产公司来说是项目
 * @author leasonlive
 *
 */
public class PisProject {
	//项目id
	private String projectId;
	//楼盘id
	private String buildingId;
	//报备专员id
	private String userId;
	
	public String getProjectId() {
		return projectId;
	}
	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}
	public String getBuildingId() {
		return buildingId;
	}
	public void setBuildingId(String buildingId) {
		this.buildingId = buildingId;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
}
