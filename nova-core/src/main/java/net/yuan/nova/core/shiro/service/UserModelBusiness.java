package net.yuan.nova.core.shiro.service;

import net.yuan.nova.core.shiro.vo.User;
import net.yuan.nova.core.shiro.vo.UserModel;

/**
 * 用户显示信息业务逻辑处理
 * @author leasonlive
 *
 */
public interface UserModelBusiness {
	/**
	 * 根据用户得到用户显示对象
	 * @param user
	 * @return
	 */
	public UserModel getUserModel(String userId);
}
