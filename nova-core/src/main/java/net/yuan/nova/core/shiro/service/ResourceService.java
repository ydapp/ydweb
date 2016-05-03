package net.yuan.nova.core.shiro.service;

import java.util.List;
import java.util.Set;

import net.yuan.nova.core.shiro.vo.Privilege;

public interface ResourceService {

	/**
	 * 得到资源对应的权限字符串
	 * 
	 * @param resourceIds
	 * @return
	 */
	Set<String> findPermissions(Set<String> resourceIds);

	/**
	 * 根据用户权限得到菜单
	 * 
	 * @param permissions
	 * @return
	 */
	List<Privilege> findMenus(Set<String> permissions);
}
