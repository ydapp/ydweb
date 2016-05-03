package net.yuan.nova.pis.controller.model;
/**
 * 添加用户基本信息
 * @author leasonlive
 *
 */
public class UserModel {
	//电话
	private String tel;
	//姓名
	private String nick;
	//类型
	private String groupType;
	//用户经纪公司和经纪人标志
	private String brokingFirm;
	//用于案场专员
	private String building;
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
	
	
}
