package net.yuan.nova.pis.dao;

import java.util.List;
import java.util.Map;

import net.yuan.nova.commons.SystemParam;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * 
 * 加载系统参数数据
 * 
 * @author Administrator
 *
 */
@Repository
public interface PisSystemParamMapper {

	/**
	 * 查询所有参数的列表
	 * 
	 * @return
	 */
	public List<SystemParam> findAll();

	/**
	 * 查询指定的参数
	 * 
	 * @param paramName
	 * @return
	 */
	public SystemParam findSystemParam(@Param("mask")String mask);

	/**
	 * 获得类型
	 * 
	 * @return
	 */
	public List<Map<String, String>> getDataList(@Param("paramSQL") String paramSQL);
	
	/**
	 * 更新系统参数
	 * @param currentValue
	 * @param comments
	 * @param mask 
	 * @return 
	 */
	public int updataSystemParams(@Param("currentValue")String currentValue,@Param("comments")String comments, @Param("mask")String mask);

}
