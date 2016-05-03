package net.yuan.nova.pis.service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import net.yuan.nova.pis.dao.PisUserGroupMapper;
import net.yuan.nova.pis.entity.PisUserGroup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * 组
 * 
 * @author leasonlive
 *
 */
@Service
public class UserGroupService {
	@Autowired
	private PisUserGroupMapper groupMapper;

	/**
	 * 根据parentId得到所有的组
	 * 
	 * @param parentId
	 * @return
	 */
	@Cacheable(value = "userGroupCache", key = "'getGroupByParentId_'+#parentId")
	public List<PisUserGroup> getGroupByParentId(String parentId) {
		return this.groupMapper.selectByParentId(parentId);
	}

	/**
	 * 添加一个新记录，id自动生成
	 * 
	 * @param userGroup
	 * @return
	 */
	@CacheEvict(value = "userGroupCache", allEntries = true)
	public int insert(PisUserGroup userGroup) {
		userGroup.setGroupId(UUID.randomUUID().toString());
		userGroup.setCreateTime(new Date());
		return this.groupMapper.insert(userGroup);
	}

	/**
	 * 根据组名称获得用户组
	 * 
	 * @param groupName
	 * @return
	 */
	@Cacheable(value = "userGroupCache", key = "'getByGroupName_'+#groupName")
	public PisUserGroup getByGroupName(String groupName) {
		return this.groupMapper.selectByGroupName(groupName);
	}

	/**
	 * 根据组id获得用户组
	 * 
	 * @param groupId
	 * @return
	 */
	@Cacheable(value = "userGroupCache", key = "'getByGroupId_'+#groupId")
	public PisUserGroup getByGroupId(String groupId) {
		return this.groupMapper.selectByPrimaryKey(groupId);
	}
	@Cacheable(value = "userGroupCache", key = "'getByGroupId_'+#type")
	public PisUserGroup getByType(String type) {
		return this.groupMapper.selectByType(type);
	}
}
