package net.yuan.nova.pis.entity.vo;

import net.yuan.nova.core.shiro.vo.User;
import net.yuan.nova.pis.entity.PisUserGroup;

public class UserInfoVo {
	// 用户信息
	private User user;
	// 用户组信息
	private PisUserGroup userGroup;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public PisUserGroup getUserGroup() {
		return userGroup;
	}

	public void setUserGroup(PisUserGroup userGroup) {
		this.userGroup = userGroup;
	}

}
