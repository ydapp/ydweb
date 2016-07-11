package net.yuan.nova.pis.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.yuan.nova.pis.dao.PisBrokingFirmMapper;
import net.yuan.nova.pis.dao.PisPersonCodeMapper;
import net.yuan.nova.pis.entity.PisPersonCode;

/**
 * 用于处理用户编码
 * @author sway
 *
 */
@Service
public class PersonCodeService {
	protected final Logger log = LoggerFactory.getLogger(PersonCodeService.class);
	
	@Autowired
	private PisPersonCodeMapper pisPersonCodeMapper;
	@Autowired
	private PisBrokingFirmMapper pisBrokingFirmMapper;

	/**
	 * 根据用户编码类型获取下一个
	 * @param codeType
	 * @return
	 */
	public String getNextCode(String codeType){
		String personCode="";//声明返回值
		PisPersonCode pisPersonCode=null;//声明用户编码实体类
		//判断传入参数是否为空
		if(""!=codeType){
			//根据类型获取最大值
			pisPersonCode = 	this.pisPersonCodeMapper.selectPersonByKey(codeType);
			//判断验证是否为空
			if(null!=pisPersonCode){
				//获取最后一位加1
				String code = pisPersonCode.getValue().length()>4?pisPersonCode.getValue().substring(1,pisPersonCode.getValue().length()):pisPersonCode.getValue();
				int num =Integer.parseInt(code)+1;
				personCode =  codeType+"000"+String.valueOf(num);
			}else{
				//初始化值
				personCode = codeType+"0001";
			}
			//判断是否我业务员
			if(PisPersonCode.Status.P.toString().equals(codeType.trim().toUpperCase())){
				if(null!=pisPersonCode){
					//通过经济公司名称获取信息
					//获取业务员所属经纪公司编码
					 String brokingFirm = "";
					 if(pisPersonCode.getParent().length()==10){
						  brokingFirm=pisPersonCode.getParent().substring(0, 5);
					 }else{
						 int index = pisPersonCode.getParent().indexOf(PisPersonCode.Status.P.toString());
						  brokingFirm=pisPersonCode.getParent().substring(0, index);
					 }
					 personCode = brokingFirm+personCode;
				}else{
					personCode = PisPersonCode.Status.M+personCode;
				}
			} 
		}
		return personCode;
	}
	
	/**
	 * 插入用户编码
	 * @param pisPersonCode
	 * @return
	 */
	public int insertPersonCode(PisPersonCode pisPersonCode){
		return this.pisPersonCodeMapper.insertPersonCode(pisPersonCode);
	}
	
	/**
	 * 修改用户编码
	 * @param pisPersonCode
	 * @return
	 */
	public int updatePersonCode(PisPersonCode pisPersonCode){
		return this.pisPersonCodeMapper.updatePersonCode(pisPersonCode);
	}
	
 
}
