package net.yuan.nova.pis.service;

import java.util.List;
import java.util.UUID;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.yuan.nova.pis.dao.PisBrokingFirmMapper;
import net.yuan.nova.pis.entity.PisBrokingFirm;

@Service
public class PisBrokingFirmService {
	@Autowired
	private PisBrokingFirmMapper mapper;
	public String add(PisBrokingFirm brokingFirm){
		brokingFirm.setBrokingFirmId(UUID.randomUUID().toString());
		this.mapper.insert(brokingFirm);
		return brokingFirm.getBrokingFirmId();
	}
	public PisBrokingFirm findById(String brokingFirmId){
		return this.mapper.selectById(brokingFirmId);
	}
	public PisBrokingFirm findByName(String brokingFirmName){
		return this.mapper.selectByName(brokingFirmName);
	}
	public List<PisBrokingFirm> findAll(){
		return this.mapper.selectAll();
	}
	public String add(String brokingFirmName){
		PisBrokingFirm brokingFirm = new PisBrokingFirm();
		brokingFirm.setBrokingFirmName(brokingFirmName);
		return this.add(brokingFirm);
	}
	
}
