package net.yuan.nova.pis.service;

import java.util.List;

import net.sf.json.JSONObject;
import net.yuan.nova.core.vo.TreeNode;
import net.yuan.nova.pis.entity.PisMenuItem;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 菜单的一些操作
 * 
 * @author zhangshuai
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext.xml")
public class MenuItemServiceTest extends AbstractJUnit4SpringContextTests {

	@Autowired
	private MenuItemService menuItemService;

	@Test
	public void testFindMenuItem() {
		PisMenuItem menuItem = menuItemService.findMenuItem("97d83354-48ef-46f4-88e0-f9e147894d40");
		System.out.println(JSONObject.fromObject(menuItem));
	}

	@Test
	public void testSaveMenuItem() {
		// PisMenuItem menuItem = new PisMenuItem();
		// menuItem.setMenuCode("nrgl");
		// menuItem.setText("内容管理");
		// menuItem.setSort(1);
		// menuItemService.saveMenuItem(menuItem);

		// //////////////////////////////////////
		// // 楼盘信息管理
		// PisMenuItem menuItem = new PisMenuItem();
		// menuItem.setParentId("97d83354-48ef-46f4-88e0-f9e147894d40");
		// menuItem.setMenuCode("lpxxgl");
		// menuItem.setText("楼盘信息管理");
		// menuItem.setSort(1);
		// menuItemService.saveMenuItem(menuItem);

		// // 楼盘信息列表
		// PisMenuItem menuItem = new PisMenuItem();
		// menuItem.setParentId("e2b60fb6-9e4e-46c6-a34e-1d1a0d72619b");
		// menuItem.setMenuCode("lpxxlb");
		// menuItem.setText("楼盘信息列表");
		// menuItem.setUrl("/admin/content/property.html");
		// menuItem.setSort(1);
		// menuItemService.saveMenuItem(menuItem);

		// // 咨询信息管理
		// PisMenuItem menuItem = new PisMenuItem();
		// menuItem.setParentId("97d83354-48ef-46f4-88e0-f9e147894d40");
		// menuItem.setMenuCode("zxxxgl");
		// menuItem.setText("咨询信息管理");
		// menuItem.setSort(2);
		// menuItemService.saveMenuItem(menuItem);
		//
		// // 咨询信息列表
		// PisMenuItem menuItem = new PisMenuItem();
		// menuItem.setParentId("dcda5a8c-a530-47b9-abe7-07ac8edb0c6c");
		// menuItem.setMenuCode("zxxxlb");
		// menuItem.setText("咨询信息列表");
		// menuItem.setUrl("/admin/content/information.html");
		// menuItem.setSort(1);
		// menuItemService.saveMenuItem(menuItem);

		// // 客户信息管理
		// PisMenuItem menuItem = new PisMenuItem();
		// menuItem.setParentId("97d83354-48ef-46f4-88e0-f9e147894d40");
		// menuItem.setMenuCode("khxxgl");
		// menuItem.setText("客户信息管理");
		// menuItem.setSort(3);
		// menuItemService.saveMenuItem(menuItem);
		//
		// // 经纪人客户信息
		// PisMenuItem menuItem = new PisMenuItem();
		// menuItem.setParentId("7854aaac-7ef6-4dcc-bdde-33630920da44");
		// menuItem.setMenuCode("zxxxlb");
		// menuItem.setText("经纪人客户信息");
		// menuItem.setUrl("/admin/content/customer.html");
		// menuItem.setSort(1);
		// menuItemService.saveMenuItem(menuItem);

		// ////////////////////////////////////////
		// ////////////////////////////////////////

		// PisMenuItem menuItem = new PisMenuItem();
		// menuItem.setMenuCode("xtgl");
		// menuItem.setText("系统管理");
		// menuItem.setSort(3);
		// menuItemService.saveMenuItem(menuItem);

		// ////////////////////////////////////////
		// // 系统监控
		// PisMenuItem menuItem = new PisMenuItem();
		// menuItem.setParentId("211fc11f-ed14-4fec-a9f0-39007ee10d13");
		// menuItem.setMenuCode("xtjk");
		// menuItem.setText("系统监控");
		// menuItem.setSort(1);
		// menuItemService.saveMenuItem(menuItem);

		// // 系统监控
		// PisMenuItem menuItem = new PisMenuItem();
		// menuItem.setParentId("c1fbdea8-55fe-4d94-8139-fa1ff483596d");
		// menuItem.setMenuCode("monitoring");
		// menuItem.setText("monitoring");
		// menuItem.setUrl("monitoring");
		// menuItem.setSort(1);
		// menuItemService.saveMenuItem(menuItem);

		// // 系统日志
		// PisMenuItem menuItem = new PisMenuItem();
		// menuItem.setParentId("c1fbdea8-55fe-4d94-8139-fa1ff483596d");
		// menuItem.setMenuCode("xtrz");
		// menuItem.setText("系统日志");
		// menuItem.setUrl("/admin/logger.html");
		// menuItem.setSort(2);
		// menuItemService.saveMenuItem(menuItem);

		// // 缓存管理
		// PisMenuItem menuItem = new PisMenuItem();
		// menuItem.setParentId("c1fbdea8-55fe-4d94-8139-fa1ff483596d");
		// menuItem.setMenuCode("hcgl");
		// menuItem.setText("缓存管理");
		// menuItem.setUrl("/admin/cache/cacheManager.html");
		// menuItem.setSort(3);
		// menuItemService.saveMenuItem(menuItem);

		// //////////////////////////////////////
		// // 系统设置
		// PisMenuItem menuItem = new PisMenuItem();
		// menuItem.setParentId("211fc11f-ed14-4fec-a9f0-39007ee10d13");
		// menuItem.setMenuCode("xtsz");
		// menuItem.setText("系统设置");
		// menuItem.setSort(1);
		// menuItemService.saveMenuItem(menuItem);

		// // 系统日志
		// PisMenuItem menuItem = new PisMenuItem();
		// menuItem.setParentId("0ceb122f-d239-43cb-8337-0322b43f848f");
		// menuItem.setMenuCode("xtcs");
		// menuItem.setText("系统参数");
		// menuItem.setUrl("/admin/systemParams.html");
		// menuItem.setSort(1);
		// menuItemService.saveMenuItem(menuItem);
	}
	@Test
	public void testAddCity(){
		 // 楼盘信息列表
		 PisMenuItem menuItem = new PisMenuItem();
		 menuItem.setParentId("e2b60fb6-9e4e-46c6-a34e-1d1a0d72619b");
		 menuItem.setMenuCode("csxxlb");
		 menuItem.setText("城市信息列表");
		 menuItem.setUrl("/admin/content/city.html");
		 menuItem.setSort(1);
		 menuItemService.saveMenuItem(menuItem);
	}
	@Test
	public void testAddUser(){
		 // 楼盘信息列表
		 PisMenuItem menuItem = new PisMenuItem();
		 menuItem.setParentId("e2b60fb6-9e4e-46c6-a34e-1d1a0d72619b");
		 menuItem.setMenuCode("userManager");
		 menuItem.setText("用户管理");
		 menuItem.setUrl("/admin/content/user.html");
		 menuItem.setSort(1);
		 menuItemService.saveMenuItem(menuItem);
	}
	@Test
	public void testDeleteMenuItem() {
		menuItemService.deleteMenuItem("ee1d5a51-b04d-4f73-86ff-a5562c7a9dae");
	}

	@Test
	public void testUpdateMenuItem() {
		PisMenuItem menuItem1 = menuItemService.findMenuItem("97d83354-48ef-46f4-88e0-f9e147894d40");
		menuItem1.setText("内容管理");
		menuItemService.updateMenuItem(menuItem1);
		PisMenuItem menuItem2 = menuItemService.findMenuItem("211fc11f-ed14-4fec-a9f0-39007ee10d13");
		menuItem2.setText("系统管理");
		menuItemService.updateMenuItem(menuItem2);
	}
	@Test
	public void testDownRecommendExcell(){
		 // 楼盘信息列表
		 PisMenuItem menuItem = new PisMenuItem();
		 menuItem.setParentId("211fc11f-ed14-4fec-a9f0-39007ee10d13");
		 menuItem.setMenuCode("xztjexcell");
		 menuItem.setText("导出推荐信息");
		 menuItem.setUrl("/api/excell");
		 menuItem.setSort(1);
		 menuItemService.saveMenuItem(menuItem);
	}
	@Test
	public void testFindMenus() {
		List<PisMenuItem> findMenus = menuItemService.findMenus(null);
		if (findMenus != null) {
			for (PisMenuItem pisMenuItem : findMenus) {
				System.out.println(JSONObject.fromObject(pisMenuItem));
			}
		}
	}

	@Test
	public void testFindMenuTreeNodes() {
		List<TreeNode<PisMenuItem>> findMenuTreeNodes = menuItemService.findMenuTreeNodes(null,"");
		if (findMenuTreeNodes != null) {
			for (TreeNode<PisMenuItem> treeNode : findMenuTreeNodes) {
				System.out.println(JSONObject.fromObject(treeNode));
			}
		}
	}

}
