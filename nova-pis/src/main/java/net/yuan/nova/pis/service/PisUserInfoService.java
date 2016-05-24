package net.yuan.nova.pis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import net.yuan.nova.pis.dao.PisUserInfoMapper;
import net.yuan.nova.pis.entity.PisUserInfo;

@Service
public class PisUserInfoService {
	
	@Autowired
	private PisUserInfoMapper pisUserInfoMapper;

	// ///////////////////////////////
		// ///// 查詢 ////////
		// ///////////////////////////////
	public PisUserInfo findUserInfoById(String userId){
		return pisUserInfoMapper.selectByPrimaryKey(userId);
	}
}
