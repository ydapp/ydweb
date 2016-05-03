package net.yuan.nova.pis.service;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.yuan.nova.pis.dao.PisUserExtendMapper;
import net.yuan.nova.pis.dao.PisUserMapper;
import net.yuan.nova.pis.entity.PisUserExtend;
/**
 * 用户扩展信息服务对象
 * @author leasonlive
 *
 */
@Service
public class PisUserExtendService {
	@Autowired
	private PisUserExtendMapper mapper;
	/**
	 * 删除一个关联信息
	 * @param userId
	 * @return
	 */
	public int deleteByUserId(String userId){
		return this.mapper.deleteByUserId(userId);
	}
	/**
	 * 插入一个关联信息
	 * @param userExtend
	 * @return
	 */
	public int insert(PisUserExtend userExtend){
		return this.mapper.insert(userExtend);
	}
	/**
	 * 查找一个关联信息
	 * @param userId
	 * @return
	 */
	public PisUserExtend selectByUserId(String userId){
		return this.mapper.selectByUserId(userId);
	}
}
