package net.yuan.nova.core.shiro.service;

import java.util.Set;

public interface RoleService {

	/**
	 * 根据角色编号得到角色标识符列表
	 * 
	 * @param roleIds
	 * @return
	 */
	Set<String> findRoles(String[] roleIds);

	/**
	 * 根据角色编号得到权限字符串列表
	 * 
	 * @param roleIds
	 * @return
	 */
	Set<String> findPermissions(String[] roleIds);
}
