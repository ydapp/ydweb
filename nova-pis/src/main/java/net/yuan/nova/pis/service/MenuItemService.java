package net.yuan.nova.pis.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.yuan.nova.core.vo.TreeNode;
import net.yuan.nova.pis.dao.PisMenuItemMapper;
import net.yuan.nova.pis.entity.PisMenuItem;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author suntiecheng 菜单服务类
 */
@Service
public class MenuItemService {

	protected final Logger log = LoggerFactory.getLogger(MenuItemService.class);

	@Autowired
	private PisMenuItemMapper pisMenuItemMapper;
	@Autowired
	private PisUserService pisUserService;

	/** 用主键查询PispMenuItem表 */
	public PisMenuItem findMenuItem(String menuItemId) {
		return pisMenuItemMapper.findMenuItem(menuItemId);
	}

	/** 保存PispMenuItem表信息 菜单表 */
	@Transactional
	public int saveMenuItem(PisMenuItem menuItem) {
		String menuItemId = UUID.randomUUID().toString();
		String menuItemPath = menuItemId;
		menuItem.setId(menuItemId);
		menuItem.setStatus("A");
		if (StringUtils.isBlank(menuItem.getParentId())) {
			menuItem.setMenuLevel(1);
			menuItem.setMenuPath(menuItemPath);
		} else {
			String parentId = menuItem.getParentId();
			PisMenuItem fatherMenu = this.findMenuItem(parentId);
			if (fatherMenu != null) {
				menuItem.setMenuLevel(fatherMenu.getMenuLevel() + 1);
				menuItem.setMenuPath(fatherMenu.getMenuPath() + "#" + menuItemPath);
			}
		}
		/** 保存PispPriv表信息 是菜单表的权限关联表 */
		// int insertPispPriv = menuItemDao.savePispPriv(menuItem);
		menuItem.setPrivId(menuItem.getId());
		menuItem.setPrivType("M");
		return pisMenuItemMapper.saveMenuItem(menuItem);
	}

	/** 删除PispMenuItem表信息 菜单表 */
	@Transactional
	public void deleteMenuItem(String menuItemId) {
		// 先移除子节点
		List<PisMenuItem> menuItems = pisMenuItemMapper.findChildren(menuItemId);
		if (menuItems != null && menuItems.size() > 0) {
			this.deleteMenuItem(menuItemId);
		}
		pisMenuItemMapper.delMenuItem(menuItemId);
	}

	/** 修改PispMenuItem表信息 菜单表 */
	@Transactional
	public int updateMenuItem(PisMenuItem menuItem) {
		/** 先更新PispPriv表信息 是菜单表的权限关联表 */
		menuItem.setPrivId(menuItem.getId());
		menuItem.setPrivType("M");
		return pisMenuItemMapper.updateMenuItem(menuItem);
	}

	/**
	 * 返回某节点下的所有菜单
	 */
	public List<PisMenuItem> findMenus(String parentId) {
		return pisMenuItemMapper.findChildren(parentId);
	}

	/** 返回某节点下的所有菜单 */
	public List<TreeNode<PisMenuItem>> findMenuTreeNodes(String parentId) {
		List<PisMenuItem> menus = pisMenuItemMapper.findChildren(parentId);
		List<TreeNode<PisMenuItem>> nodes = new ArrayList<TreeNode<PisMenuItem>>();
		for (PisMenuItem menu : menus) {
			TreeNode<PisMenuItem> node = new TreeNode<PisMenuItem>();
			String Id = menu.getId();
			node.setId(Id);
			node.setText(menu.getText());
			// 判断是否是子节点
			if (!isLeaf(Id)) {
				node.setState("closed");
			}
			node.setAttributes(menu);
			nodes.add(node);// stc添加，修正之前nodes没有赋值，空值返回的问题
		}
		return nodes;
	}

	/**
	 * 判断菜单是否是子节点
	 * 
	 * @param id
	 * @return
	 */
	private boolean isLeaf(String parentId) {
		if (pisMenuItemMapper.getChildrenCount(parentId) > 0) {
			return false;
		}
		return true;
	}

}
