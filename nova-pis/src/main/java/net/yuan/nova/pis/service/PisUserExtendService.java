package net.yuan.nova.pis.service;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;

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
	/**
	 * 修改用户扩展关系
	 * @param userExtend
	 * @return
	 */
	public int updateByUserId(PisUserExtend userExtend){
		return this.mapper.updateByUserId(userExtend);
	}
	/**
	 * 通过经济公司主键ID获取该公司下的用户ID
	 * @param brokingFirmId
	 * @return
	 */
	public List<PisUserExtend> selectByBrokingfirmId(int page, int pageSize,String brokingFirmId){
		PageHelper.startPage(page, pageSize);
		return this.mapper.selectByBrokingfirmId(brokingFirmId);
	}
}
