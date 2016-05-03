package net.yuan.nova.pis.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.yuan.nova.core.vo.DataGridData;
import net.yuan.nova.core.vo.JsonVo;
import net.yuan.nova.pis.entity.PisCity;
import net.yuan.nova.pis.service.PisCityService;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 城市数据控制器
 * 
 * @author leasonlive
 *
 */
@Controller
public class CityController {
	protected final Logger log = LoggerFactory.getLogger(CityController.class);
	@Autowired
	private PisCityService pisCityService;

	/**
	 * 添加一个城市记录
	 * 
	 * @param city
	 * @param request
	 * @param modelMap
	 * @return
	 */
	@RequestMapping(value = "/api/addCity", method = RequestMethod.POST)
	@ResponseBody
	public ModelMap insert(@RequestBody PisCity city, HttpServletRequest request, ModelMap modelMap) {
		JsonVo<Object> jsonVo = new JsonVo<Object>();
		if (StringUtils.isEmpty(city.getCityName())) {
			jsonVo.putError("cityName", "城市名称不能为空");
		}
		// 验证通过后，插入数据
		if (jsonVo.validate()) {
			this.pisCityService.insert(city);
		}
		modelMap.remove("pisRecommend");
		modelMap.addAttribute("result", jsonVo);
		return modelMap;
	}

	/**
	 * 获取城市列表
	 * 
	 * @param request
	 * @param modelMap
	 * @return
	 */
	@RequestMapping(value = "/api/getCitys/{parentCityId}", method = RequestMethod.GET)
	@ResponseBody
	public ModelMap getCityList(@PathVariable String parentCityId, HttpServletRequest request, ModelMap modelMap) {
		if (StringUtils.isEmpty(parentCityId)) {
			parentCityId = null;
		}
		DataGridData<PisCity> dgd = new DataGridData<PisCity>();
		List<PisCity> citys = pisCityService.getCitys(parentCityId);
		dgd.setRows(citys);
		dgd.setTotal(citys.size());
		modelMap.addAttribute("result", dgd);
		return modelMap;
	}

	@RequestMapping(value = "/api/getCitys")
	public ModelMap getCityList(HttpServletRequest request, ModelMap modelMap) {

		String parentCityId = request.getParameter("parentCityId");
		if (StringUtils.isBlank(parentCityId)) {
			parentCityId = null;
		}
		return getCityList(parentCityId, request, modelMap);
	}
}
