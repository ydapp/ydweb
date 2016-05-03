package net.yuan.nova.pis.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.yuan.nova.core.vo.JsonVo;
import net.yuan.nova.core.vo.TreeNode;
import net.yuan.nova.pis.entity.PisMenuItem;
import net.yuan.nova.pis.service.MenuItemService;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 系统菜单控制器
 * 
 * @author zhangshuai
 * 
 */
@Controller
public class MenuController {

	protected final Logger log = LoggerFactory.getLogger(MenuController.class);

	@Autowired
	private MenuItemService menuItemService;

	/**
	 * 获得相应的菜单列表数据
	 * 
	 * @param request
	 * @param response
	 * @param modelMap
	 * @return
	 */
	@RequestMapping(value = "/admin/menus")
	public ModelMap menus(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) {
		log.debug("获得菜单列表");
		// 父节点的ID
		String parentId = StringUtils.trimToEmpty(request.getParameter("id"));
		if ("-1".equals(parentId)) {
			parentId = null;
		}
		List<TreeNode<PisMenuItem>> list = menuItemService.findMenuTreeNodes(parentId);
		return modelMap.addAttribute("menus", list);
	}

	/**
	 * 根据当前用户的权限获得相应的菜单列表数据
	 * 
	 * @param request
	 * @param response
	 * @param modelMap
	 * @return
	 */
	@RequestMapping(value = "/admin/usermenus")
	public ModelMap menuslevel(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) {
		String parentId = StringUtils.trimToEmpty(request.getParameter("id"));
		List<TreeNode<PisMenuItem>> list = null;
		list = menuItemService.findMenuTreeNodes(parentId);
		return modelMap.addAttribute("menus", list);
	}

	/**
	 * 菜单
	 * 
	 * @param request
	 * @param response
	 * @param modelMap
	 * @return
	 */
	@RequestMapping(value = "/admin/menu")
	public String operateMenu(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap,
			@RequestParam("edittype") String edittype) {
		PisMenuItem menu = new PisMenuItem();
		String privName = StringUtils.trimToEmpty(request.getParameter("privName"));
		String menuCode = StringUtils.trimToEmpty(request.getParameter("menuCode"));
		String url = StringUtils.trimToEmpty(request.getParameter("url"));
		int sort = NumberUtils.toInt(StringUtils.trimToEmpty(request.getParameter("sort")), 1);
		String comments = StringUtils.trimToEmpty(request.getParameter("comments"));
		String parentMenuId = StringUtils.trimToEmpty(request.getParameter("parentMenuId"));
		String menuItemId = StringUtils.trimToEmpty(request.getParameter("menuItemId"));
		menu.setPrivName(privName);
		menu.setMenuCode(menuCode);
		menu.setUrl(url);
		menu.setSort(sort);
		menu.setComments(comments);
		menu.setParentId(parentMenuId);
		menu.setId(menuItemId);
		JsonVo<PisMenuItem> jsonVo = new JsonVo<PisMenuItem>();
		if ("add".equals(edittype)) {
			log.debug("添加新菜单");
			if (StringUtils.isBlank(menu.getPrivName())) {
				jsonVo.putError("privName", "菜单文本不能为空");
			}
			if (StringUtils.isBlank(menu.getMenuCode())) {
				jsonVo.putError("menuCode", "菜单名称不能为空");
			}
			if (jsonVo.validate()) {
				modelMap.addAttribute("json", jsonVo);
				modelMap.remove("menu");
			} else {
				modelMap.addAttribute("json", jsonVo);
				modelMap.remove("menu");
			}
		} else if ("del".equals(edittype)) {
			log.debug("删除菜单及相应的子菜单");
			String ItemId = menu.getId();
			if (StringUtils.isBlank(ItemId)) {
				jsonVo.putError("id", "菜单ID不能为空");
			}
			jsonVo.setResult(menu);
			modelMap.addAttribute("json", jsonVo);
			modelMap.remove("menu");
			if (jsonVo.validate()) {
				menuItemService.deleteMenuItem(menu.getId());
			}
		} else {
			log.debug("更新当前菜单");
			String ItemId = menu.getId();
			if (StringUtils.isBlank(ItemId)) {
				jsonVo.putError("id", "菜单ID不能为空");
			}
			if (StringUtils.isBlank(menu.getPrivName())) {
				jsonVo.putError("privName", "菜单文本不能为空");
			}
			if (StringUtils.isBlank(menu.getMenuCode())) {
				jsonVo.putError("menuCode", "菜单名称不能为空");
			}
			jsonVo.setResult(menu);
			modelMap.addAttribute("json", jsonVo);
			modelMap.remove("menu");
			if (jsonVo.validate()) {
				menuItemService.updateMenuItem(menu);
			}
		}
		return "menu";
	}

	/**
	 * 菜单详情
	 * 
	 * @param request
	 * @param response
	 * @param modelMap
	 * @return
	 */
	@RequestMapping(value = "/api/menu/{menuId}")
	public String userInfo(@PathVariable("menuId") String menuId, HttpServletRequest request,
			HttpServletResponse response, ModelMap modelMap) {
		// 获得菜单详情

		// 获得菜单的各级父菜单

		return "menu";
	}

}
