package net.yuan.nova.pis.service;

import java.util.List;

import net.yuan.nova.pis.dao.PisUserGroupShipMapper;
import net.yuan.nova.pis.entity.PisUserGroupShipKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserGroupShipKeyService {
	@Autowired
	private PisUserGroupShipMapper userGroupShipMapper;

	public int insert(PisUserGroupShipKey key) {
		return this.userGroupShipMapper.insert(key);
	}

	public List<PisUserGroupShipKey> getByUserId(String userId) {
		return this.userGroupShipMapper.getByUserId(userId);
	}
	public List<PisUserGroupShipKey> getByGroupId(String groupId) {
		return this.userGroupShipMapper.getByGroupId(groupId);
	}
}
