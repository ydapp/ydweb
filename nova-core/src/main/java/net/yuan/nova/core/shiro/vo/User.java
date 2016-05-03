package net.yuan.nova.core.shiro.vo;

import java.io.Serializable;

public class User implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String userId;
	private String userName;// 用户名，登录后用于显示的名称，用户真名或则昵称
	private String loginName;// 登录帐号
	private String password;// 密码
	private String salt;// 盐
	private String email;
	private String phoneNumber;
	private Type type;
	private LoginType loginType;
	private UserModel userModel;//定义用户显示信息
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public LoginType getLoginType() {
		return loginType;
	}

	public void setLoginType(LoginType loginType) {
		this.loginType = loginType;
	}

	/**
	 * 用户类型
	 *
	 */
	public static enum Type {
		/**
		 * staff：员工，即管理员
		 */
		staff,
		/**
		 * pubUser：公共用户
		 */
		pubUser;
	}

	/**
	 * 
	 * 登录类型
	 * 
	 * @author Administrator
	 *
	 */
	public static enum LoginType {
		/**
		 * WEB：web应用登录
		 */
		WEB,
		/**
		 * APP：app登录
		 */
		APP;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		User user = (User) o;
		if (userId != null ? !userId.equals(user.userId) : user.userId != null)
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		return userId != null ? userId.hashCode() : 0;
	}

	public UserModel getUserModel() {
		return userModel;
	}

	public void setUserModel(UserModel userModel) {
		this.userModel = userModel;
	}

}
