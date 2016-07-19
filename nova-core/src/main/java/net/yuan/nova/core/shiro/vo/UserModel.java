package net.yuan.nova.core.shiro.vo;

import java.io.Serializable;

/**
 * 添加用户基本信息
 * @author leasonlive
 *
 */
public class UserModel implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
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
	//用户类型：A admin U user
	private String type;
	//身份证号码
	private String idNumber;
	//姓名
	private String name;
	//性别
	private String sex;
	//地址
	private String address;
	//用户头像附件关联附件ID
	private String userIcon;
	//用户身份证照片关联附件ID
	private String frontPhoto;
	//用户编码
	private String personCode;
	//城市名称
	private String cityName;
	//楼盘名称
	private String propertyName;
	
	
	
	
	
	
	
	public String getCityName() {
		return cityName;
	}
	public void setCityName(String cityName) {
		this.cityName = cityName;
	}
	public String getPropertyName() {
		return propertyName;
	}
	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}
	public String getUserIcon() {
		return userIcon;
	}
	public void setUserIcon(String userIcon) {
		this.userIcon = userIcon;
	}
	public String getFrontPhoto() {
		return frontPhoto;
	}
	public void setFrontPhoto(String frontPhoto) {
		this.frontPhoto = frontPhoto;
	}
	public String getIdNumber() {
		return idNumber;
	}
	public void setIdNumber(String idNumber) {
		this.idNumber = idNumber;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
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
	public String getPersonCode() {
		return personCode;
	}
	public void setPersonCode(String personCode) {
		this.personCode = personCode;
	}
}
