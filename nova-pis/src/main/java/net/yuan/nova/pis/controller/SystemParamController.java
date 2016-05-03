package net.yuan.nova.pis.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.yuan.nova.commons.SystemParam;
import net.yuan.nova.core.vo.DataGridData;
import net.yuan.nova.core.vo.JsonVo;
import net.yuan.nova.pis.service.SystemParamService;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 字典管理的控制器
 * 
 * @author zhangshuai
 * 
 */
@Controller
public class SystemParamController {

	protected final Logger log = LoggerFactory.getLogger(SystemParamController.class);

	@Autowired
	private SystemParamService systemParamService;

	/**
	 * 系统参数列表
	 * 
	 * @return
	 */
	@RequestMapping(value = "/admin/systemParams")
	public Object getAllSystemParam() {
		List<SystemParam> list = systemParamService.getAllSystemParam();
		DataGridData<SystemParam> dataGridData = new DataGridData<SystemParam>();
		dataGridData.setRows(list);
		dataGridData.setTotal(list.size());
		return dataGridData;
	}

	/**
	 * 修改系统参数列表
	 * 
	 * @return
	 */
	@RequestMapping(value = { "/admin/updataSystemParams" }, method = RequestMethod.POST)
	public void updataSystemParams(@RequestParam(value = "currentValue", defaultValue = "") String currentValue,
			@RequestParam(value = "comments", defaultValue = "") String comments,
			@RequestParam(value = "mask", defaultValue = "") String mask, ModelMap map) {
		try {
			systemParamService.updataSystemParams(currentValue, comments, mask);
		} catch (Exception e) {
			map.addAttribute("success", false);
			map.addAttribute("message", "更新失败");
		}
	}

	/**
	 * 获得指定的系统参数
	 * 
	 * @param paramName
	 * @param modelMap
	 * @return
	 */
	@RequestMapping(value = { "/api/systemParam/{mask}", "/admin/systemParam/{mask}" })
	public Object getSystemParam(@PathVariable("mask") String mask, ModelMap modelMap) {
		JsonVo<SystemParam> json = new JsonVo<SystemParam>();
		SystemParam systemParam = systemParamService.getSystemParam(mask);
		if (systemParam == null) {
			json.setMessage("您请求的系统参数不存在");
		} else {
			json.setResult(systemParam);
			json.setSuccess(true);
		}
		return json;
	}

	/**
	 * 获得指定的系统参数
	 * 
	 * @param request
	 * @param modelMap
	 * @return
	 */
	@RequestMapping(value = { "/api/systemParam", "/admin/systemParam" })
	public Object getSystemParam(HttpServletRequest request, ModelMap modelMap) {
		String mask = StringUtils.trimToEmpty(request.getParameter("mask"));
		if (StringUtils.isBlank(mask)) {
			mask = StringUtils.trimToEmpty(request.getParameter("paramName"));
		}
		if (StringUtils.isBlank(mask)) {
			JsonVo<SystemParam> json = new JsonVo<SystemParam>();
			json.putError("paramName", "欲请求的参数名称不能为空");
			return json;
		}
		return getSystemParam(mask, modelMap);
	}

}
