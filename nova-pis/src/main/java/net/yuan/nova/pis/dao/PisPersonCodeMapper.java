package net.yuan.nova.pis.dao;

import org.springframework.stereotype.Repository;

import net.yuan.nova.pis.entity.PisPersonCode;

/**
 * 操作用户编码
 * @author sway
 *
 */
@Repository
public interface PisPersonCodeMapper {
	
	/**
	 * 通过类型获取用户编码
	 * @param key
	 * @return
	 */
	public PisPersonCode selectPersonByKey(String key);
	
	/**
	 * 新增用户编码
	 */
	public int insertPersonCode(PisPersonCode pisPersonCode);
	
	/**
	 * 修改用户编码
	 * @param pisPersonCode
	 * @return
	 */
	public int updatePersonCode(PisPersonCode pisPersonCode);

	/**
	 * 删除用户编码信息
	 * @param pisPersonCode
	 * @return
	 */
	public int deletePersonCode(String parent);
}
