package net.yuan.nova.pis.dao;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import net.yuan.nova.pis.entity.PisUserExtend;

@Repository
public interface PisUserExtendMapper {
	public int deleteByUserId(@Param("userId") String userId);
	public int insert(PisUserExtend userExtend);
	public PisUserExtend selectByUserId(@Param("userId") String userId);
}
