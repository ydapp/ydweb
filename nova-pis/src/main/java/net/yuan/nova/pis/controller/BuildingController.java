package net.yuan.nova.pis.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.yuan.nova.commons.HttpUtils;
import net.yuan.nova.core.entity.Attachment;
import net.yuan.nova.core.service.AttachmentService;
import net.yuan.nova.core.vo.DataGridData;
import net.yuan.nova.core.vo.JsonVo;
import net.yuan.nova.pis.entity.PisBuilding;
import net.yuan.nova.pis.entity.PisProperty;
import net.yuan.nova.pis.entity.vo.PisPropertyVo;
import net.yuan.nova.pis.service.PisBuildingService;
import net.yuan.nova.pis.service.PisCityService;
import net.yuan.nova.pis.service.TemplateService;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

/**
 * 楼盘的控制器
 * 
 * @author leasonlive
 *
 */
@Controller
public class BuildingController {
	protected final Logger log = LoggerFactory.getLogger(BuildingController.class);
	@Autowired
	private PisBuildingService buildingService;
	@Autowired
	private AttachmentService attachmentService;
	@Autowired
	private TemplateService templateService;
	@Autowired
	private PisCityService cityService;

	/**
	 * 获取某城市的楼盘信息
	 * 
	 * @param cityId
	 * @param request
	 * @param modelMap
	 * @return
	 */
	@RequestMapping(value = "/api/buildings/cityId/{cityId}", method = RequestMethod.GET)
	@ResponseBody
	public ModelMap getList(@PathVariable String cityId, HttpServletRequest request, ModelMap modelMap) {
		log.debug("根据城市id获取楼盘:" + cityId);
		JsonVo<List<PisBuilding>> jsonVo = new JsonVo<List<PisBuilding>>();
		// 验证通过后，插入数据
		if (jsonVo.validate()) {
			List<PisBuilding> list = this.buildingService.getBuilding(cityId);
			jsonVo.setResult(list);
		}
		modelMap.addAttribute("result", jsonVo);
		return modelMap;
	}

	// ///////////////////////////////////////////////////////////
	// ////////////主表操作/////////////////////////////
	// ///////////////////////////////////////////////////////////

	/**
	 * 插入数据
	 * 
	 * @param record
	 * @return
	 */
	@RequestMapping("/admin/property/add")
	public Object insertSelective(HttpServletRequest request, ModelMap modelMap) {
		log.debug("添加楼盘");
		JsonVo<Object> json = new JsonVo<Object>();
		MultipartFile file = null;
		MultipartHttpServletRequest multipartRequest = null;
		// 转型为MultipartHttpRequest，如果没有上传图片是会报出异常：ClassCastException
		log.debug("转换request为附件方式");
		try {
			multipartRequest = (MultipartHttpServletRequest) request;
			file = multipartRequest.getFile("cover");
		} catch (ClassCastException cce) {
			log.error("没有上传图片", cce);
		}
		if (file == null) {
			System.out.println("没有附件上传");
		}
		Enumeration<String> names = multipartRequest.getParameterNames();
		while (names.hasMoreElements()){
			String name = names.nextElement();
			String value = multipartRequest.getParameter(name);
			String[] values = multipartRequest.getParameterValues(name);
			log.debug("name:" + name + " value:" + value + " values:" + values);
		}
		PisProperty property = new PisProperty();
		property.setAddress(multipartRequest.getParameter("address"));
		property.setArea(multipartRequest.getParameter("area"));
		property.setAvgPrice(NumberUtils.createInteger(StringUtils.trimToNull(multipartRequest.getParameter("avgPrice"))));
		property.setCharacteristic(multipartRequest.getParameter("characteristic"));
		property.setCity(multipartRequest.getParameter("city"));
		property.setCommission(multipartRequest.getParameter("commission"));
		property.setCountry(multipartRequest.getParameter("country"));
		property.setCounty(multipartRequest.getParameter("county"));
		property.setDecoration(multipartRequest.getParameter("decoration"));
		try {
			String deliveryTime = StringUtils.trimToNull(multipartRequest.getParameter("deliveryTime"));
			if (deliveryTime!= null){
				property.setDeliveryTime(DateUtils.parseDate(deliveryTime, "yyyy-mm-dd"));
			}
		} catch (Exception e){
			log.error("解析deliveryTime失败",e);
		}
		property.setGreenRate(NumberUtils.createBigDecimal(StringUtils.trimToNull(multipartRequest.getParameter("greenRate"))));
		try {
			String openDate = StringUtils.trimToNull(multipartRequest.getParameter("openDate"));
			if (openDate != null){
				property.setOpenDate(DateUtils.parseDate(openDate, "yyyy-mm-dd"));
			}
		} catch (Exception e){
			log.error("解析openDate失败", e);
		}
		property.setPropertyCompany(multipartRequest.getParameter("propertyCompany"));
		property.setPropertyId(multipartRequest.getParameter("propertyId"));
		property.setPropertyName(multipartRequest.getParameter("propertyName"));
		property.setPropertyType(multipartRequest.getParameter("propertyType"));
		property.setProvince(multipartRequest.getParameter("province"));
		property.setRealEstateAgency(multipartRequest.getParameter("realEstateAgency"));
		property.setRecommendedNumber(NumberUtils.createInteger(StringUtils.trimToNull(multipartRequest.getParameter("recommendedNumber"))));
		property.setReservationNumber(NumberUtils.createInteger(StringUtils.trimToNull(multipartRequest.getParameter("reservationNumber"))));
		property.setSubscriptionRules(multipartRequest.getParameter("subscriptionRules"));
		property.setViewTimes(NumberUtils.createInteger(StringUtils.trimToNull(request.getParameter("viewTimes"))));
		property.setYears(NumberUtils.createInteger(StringUtils.trimToNull(multipartRequest.getParameter("years"))));
		if (StringUtils.isEmpty(property.getPropertyName())){
			json.setSuccess(false);
			json.setMessage("楼盘名称不能为空");
		}
		log.debug("属性设置完毕，开始保存数据库");
		if (json.validate()) {
			log.debug("保存业务数据");
			buildingService.addPisProperty(property);
			log.debug("保存图片数据");
			this.attachmentService.addUploadFile(file, file.getOriginalFilename(), property.getPropertyId(), Attachment.TableName.PIS_PROPERTY, Attachment.State.A);
			log.debug("保存building数据");
			PisBuilding building = new PisBuilding();
			building.setBuildingId(property.getPropertyId());
			building.setBuildingName(property.getPropertyName());
			building.setCityId(property.getCity());
			this.buildingService.insert(building);
		}
		return json;
	}

	@RequestMapping("/admin/properties")
	public Object selectPisProperties() {
		DataGridData<PisProperty> dgd = new DataGridData<PisProperty>();
		List<PisProperty> properties = buildingService.selectPisProperties();
		for (PisProperty pisProperty : properties) {
			if (StringUtils.isNotBlank(pisProperty.getCity())){
				pisProperty.setCityTitle(this.cityService.getCityById(pisProperty.getCity()).getCityName());
			}
		}
		dgd.setRows(properties);
		dgd.setTotal(properties.size());
		return dgd;
	}

	@RequestMapping("/api/properties")
	public ModelAndView pisPropertyVoList(HttpServletRequest request, ModelAndView modelAndView) {
		List<PisProperty> properties = buildingService.selectPisProperties();
		List<PisPropertyVo> pisPropertyVoList = new ArrayList<PisPropertyVo>();
		for (PisProperty pisProperty : properties) {
			List<Attachment> attachments = attachmentService.getAttachmentsByKindId(pisProperty.getPropertyId(),
					Attachment.TableName.PIS_PROPERTY, Attachment.State.A);
			Attachment attachment = null;
			if (attachments.size() > 0) {
				attachment = attachments.get(0);
			}
			String filePath = null;
			if (attachment != null) {
				try {
					filePath = attachmentService.thumbnailator(attachment, 42);
				} catch (IOException e) {
					log.error("生成缩略图失败", e);
					filePath = attachment.getSavePath();
				}
			}
			PisPropertyVo pisPropertyVo = new PisPropertyVo();
			try {
				BeanUtils.copyProperties(pisPropertyVo, pisProperty);
			} catch (Exception e) {
				log.error("拷贝数据出错", e);
			}
			pisPropertyVo.setFilePath(filePath);
			pisPropertyVoList.add(pisPropertyVo);
		}
		modelAndView.addObject("success", true);
		modelAndView.addObject("properties", pisPropertyVoList);
		modelAndView.addObject("size", pisPropertyVoList.size());
		modelAndView.setViewName("ydapp/propertyList");
		return modelAndView;
	}

	/**
	 * 楼盘详情
	 * 
	 * @param id
	 * @param request
	 * @param modelMap
	 * @return
	 */
	@RequestMapping("/api/property/{id}")
	public ModelAndView propertyDetail(@PathVariable String id, HttpServletRequest request, ModelAndView modelAndView) {
		PisProperty pisProperty = buildingService.selectByPrimaryKey(id);
		if (pisProperty == null) {
			modelAndView.setViewName(templateService.getErrorTemplate(404));
			return modelAndView;
		}
		PisPropertyVo pisPropertyVo = new PisPropertyVo();
		try {
			BeanUtils.copyProperties(pisPropertyVo, pisProperty);
		} catch (Exception e) {
			log.error("拷贝数据出错", e);
		}
		String viewWidthStr = request.getParameter("viewWidth");
		int width = NumberUtils.toInt(viewWidthStr, 0);
		// 对图片进行相应的压缩
		List<Attachment> attachments = attachmentService.getAttachmentsByKindId(pisProperty.getPropertyId(),
				Attachment.TableName.PIS_PROPERTY, Attachment.State.A);
		Attachment attachment = null;
		if (attachments.size() > 0) {
			attachment = attachments.get(0);
		}
		if (attachment != null) {
			String filePath = attachment.getSavePath();
			if (width > 10) {
				try {
					filePath = attachmentService.thumbnailator(attachment, width - 10);
				} catch (IOException e) {
					log.error("生成缩略图失败", e);
				}
			}
			// 获得请求图片的完整地址
			String basePath = HttpUtils.getBasePath(request);
			pisPropertyVo.setFilePath(basePath + "/" + filePath);
		}
		modelAndView.addObject("pisPropertyVo", pisPropertyVo);
		modelAndView.setViewName("ydapp/propertyDetail");
		return modelAndView;
	}

	/**
	 * 楼盘详情
	 * 
	 * @param id
	 * @param request
	 * @param modelMap
	 * @return
	 */
	@RequestMapping("/api/property")
	public ModelAndView propertyDetail(HttpServletRequest request, ModelAndView modelAndView) {
		String id = request.getParameter("id");
		return propertyDetail(id, request, modelAndView);
	}
}
