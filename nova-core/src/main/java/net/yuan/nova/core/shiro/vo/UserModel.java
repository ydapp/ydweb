package net.yuan.nova.core.shiro.vo;

import java.io.Serializable;

/**
 * 添加用户基本信息
 * @author leasonlive
 *
 */
public class UserModel implements Serializable {
	
	//电话
	private String tel;
	//姓名
	private String nick;
	//类型代码
	private String groupType;
	//类型名称
	private String groupTypeTitle;
	//用户经纪公司和经纪人标志
	private String brokingFirm;
	//用于案场专员
	private String building;
	//用户ID
	private String userId;
	//类型ID
	private String groupId;
	
	
	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	public String getNick() {
		return nick;
	}
	public void setNick(String nick) {
		this.nick = nick;
	}
	public String getGroupType() {
		return groupType;
	}
	public void setGroupType(String groupType) {
		this.groupType = groupType;
	}
	public String getBrokingFirm() {
		return brokingFirm;
	}
	public void setBrokingFirm(String brokingFirm) {
		this.brokingFirm = brokingFirm;
	}
	public String getBuilding() {
		return building;
	}
	public void setBuilding(String building) {
		this.building = building;
	}
	public String getGroupTypeTitle() {
		return groupTypeTitle;
	}
	public void setGroupTypeTitle(String groupTypeTitle) {
		this.groupTypeTitle = groupTypeTitle;
	}
	
	
}
