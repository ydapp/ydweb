package net.yuan.nova.pis.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.yuan.nova.core.shiro.vo.UserModel;
import net.yuan.nova.core.vo.DataGridData;
import net.yuan.nova.core.vo.JsonVo;
import net.yuan.nova.pis.entity.PisCity;
import net.yuan.nova.pis.pagination.DataGridHepler;
import net.yuan.nova.pis.pagination.PageParam;
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
	public ModelMap getCityList(@PathVariable String parentCityId, HttpServletRequest request, ModelMap modelMap, HttpServletResponse response) {
		if (StringUtils.isEmpty(parentCityId)) {
			parentCityId = null;
		}
		PageParam param = DataGridHepler.parseRequest(request);
		List<PisCity> list = this.pisCityService.getCustomers(param.getPage(), param.getPageSize(),parentCityId);
		System.out.println("---------------------------------------------------------------------------------------------------------------------城市数据列表");
		return DataGridHepler.addDataGrid(list, modelMap); 
	}
	@RequestMapping(value = "/api/getCitys")
	public ModelMap getCityList(HttpServletRequest request, ModelMap modelMap, HttpServletResponse response) {

		String parentCityId = request.getParameter("parentCityId");
		if (StringUtils.isBlank(parentCityId)) {
			parentCityId = null;
		}
		return getCityList(parentCityId, request, modelMap,response);
	}
	@RequestMapping(value = "/api/city/{cityId}")
	public PisCity getCity(@PathVariable("cityId") String cityId, HttpServletRequest request, ModelMap modelMap) {

		return this.pisCityService.getCityById(cityId);
	}
}
