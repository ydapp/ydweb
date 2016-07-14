package net.yuan.nova.pis.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import net.yuan.nova.pis.entity.PisBrokingFirm;

@Repository
public interface PisBrokingFirmMapper {
	public int insert(PisBrokingFirm brokingFirm);
	public PisBrokingFirm selectById(@Param("brokingFirmId") String brokingFirmId);
	public PisBrokingFirm selectByName(@Param("brokingFirmName") String brokingFirmName);
	public List<PisBrokingFirm> selectAll();
	public List<PisBrokingFirm> selectByLikeName(@Param("brokingFirmName") String brokingFirmNam );
}
