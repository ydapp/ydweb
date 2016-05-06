package net.yuan.nova.pis.service;

import java.util.UUID;

import net.yuan.nova.pis.dao.PisProjectMapper;
import net.yuan.nova.pis.entity.PisProject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 用于处理经济人所在楼盘信息
 * 这个服务不暂时再继续使用，请用PisUserExtendService
 * @author leasonlive
 *
 */
@Service
@Deprecated
public class PisProjectService {
	@Autowired
	private PisProjectMapper projectMapper;

	/**
	 * 根据报备专员id获取项目信息
	 * 
	 * @param userId
	 *            报备专员id
	 * @return
	 */
	public PisProject getByUserId(String userId) {
		return this.projectMapper.getByUserId(userId);
	}

	/**
	 * 根据楼盘id获取项目信息
	 * 
	 * @param buildingId
	 *            楼盘id
	 * @return
	 */
	public PisProject getByBuildingId(String buildingId) {
		return this.projectMapper.getByBuildingId(buildingId);
	}

	public int insert(String userId, String buildingId) {
		PisProject project = new PisProject();
		project.setBuildingId(buildingId);
		project.setUserId(userId);
		project.setProjectId(UUID.randomUUID().toString());
		return this.projectMapper.insert(project);
	}
}
