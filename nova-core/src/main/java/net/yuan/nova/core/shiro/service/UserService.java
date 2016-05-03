package net.yuan.nova.core.shiro.service;

import java.util.Set;

import net.yuan.nova.core.shiro.vo.User;

public interface UserService {

	/**
	 * 根据用户名查找其权限
	 * 
	 * @param username
	 * @param userType
	 * @return
	 */
	public User findUser(String username, User.Type userType);

	/**
	 * 根据用户名查找其权限
	 * 
	 * @param oauthId
	 *            第三方平台返回的ID
	 * @param oauthPlatType
	 *            第三方平台类型
	 * @return
	 */
	public User findUserByUid(String oauthId, String oauthPlatType);

	/**
	 * 根据用户名查找其角色
	 * 
	 * @param username
	 * @return
	 */
	public Set<String> findRoles(String username, User.Type userType);

	/**
	 * 根据用户名查找其权限
	 * 
	 * @param username
	 * @return
	 */
	public Set<String> findPermissions(String username, User.Type userType);

}
