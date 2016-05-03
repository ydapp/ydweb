package net.yuan.nova.pis.dao;

import java.util.List;

import net.yuan.nova.pis.entity.PisMenuItem;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * 用户菜单管理
 * 
 * @author suntiecheng
 */
@Repository
public interface PisMenuItemMapper {

	/** 增加PispMenuItem */
	public int saveMenuItem(PisMenuItem menuItem);

	/** 根据主键进行查询 */
	public PisMenuItem findMenuItem(@Param("id") String menuItemId);

	/** 删除PispMenuItem */
	public int delMenuItem(@Param("id") String menuItemId);

	/** 修改PispMenuItem */
	public int updateMenuItem(PisMenuItem menuItem);

	/**
	 * 返回某节点下的所有菜单
	 */
	public List<PisMenuItem> findChildren(@Param("parentId") String parentId);

	public int getChildrenCount(@Param("parentId") String parentId);

}
