package net.yuan.nova.pis.controller;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.json.JSONObject;
import net.yuan.nova.commons.HttpUtils;
import net.yuan.nova.commons.SystemConstant;
import net.yuan.nova.core.entity.Attachment;
import net.yuan.nova.core.entity.Attachment.TableName;
import net.yuan.nova.core.service.AttachmentService;
import net.yuan.nova.core.vo.JsonVo;
import net.yuan.nova.pis.entity.PisBuilding;
import net.yuan.nova.pis.entity.PisCity;
import net.yuan.nova.pis.entity.PisProperty;
import net.yuan.nova.pis.entity.PisUser;
import net.yuan.nova.pis.entity.PisUserExtend;
import net.yuan.nova.pis.entity.PisUserGroup;
import net.yuan.nova.pis.entity.vo.PisPropertyVo;
import net.yuan.nova.pis.pagination.DataGridHepler;
import net.yuan.nova.pis.pagination.PageParam;
import net.yuan.nova.pis.service.PisBuildingService;
import net.yuan.nova.pis.service.PisCityService;
import net.yuan.nova.pis.service.PisUserExtendService;
import net.yuan.nova.pis.service.PisUserService;
import net.yuan.nova.pis.service.TemplateService;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.BigDecimalConverter;
import org.apache.commons.beanutils.converters.DateConverter;
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
import com.github.pagehelper.PageInfo;

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
	@Autowired
	private PisUserExtendService userExtendService;
	@Autowired
	private PisUserService pisUserService;
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
	
	/**
	 * 获取所有的楼盘信息
	 * @param request
	 * @param modelMap
	 * @return
	 */
	@RequestMapping(value = "/api/getAllBuildings", method = RequestMethod.GET)
	public ModelMap getAllBuildingList(HttpServletRequest request, ModelMap modelMap){
		JsonVo<List<PisBuilding>> jsonVo = new JsonVo<List<PisBuilding>>();
		List<PisBuilding> list = this.buildingService.getAllBuildingList();
		if(null != list && list.size() > 0){
			jsonVo.setResult(list);
			jsonVo.setSuccess(true);
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
		
		property.setPropertyTel(multipartRequest.getParameter("propertyTel"));
		property.setTrafficFacilities(multipartRequest.getParameter("trafficFacilities"));
		property.setHouseType(multipartRequest.getParameter("houseType"));
		
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
	
	
	/**
	 * 修改楼盘信息
	 * @return
	 */
	@RequestMapping("/admin/property/update")
	@SuppressWarnings("unchecked")
	public JsonVo<Object> updateProperty(HttpServletRequest request, ModelMap modelMap){
		log.debug("修改楼盘");
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
		property.setAvgPrice(NumberUtils.createInteger(String.valueOf(StringUtils.trimToNull(multipartRequest.getParameter("avgPrice")))));
		property.setCharacteristic(multipartRequest.getParameter("characteristic"));
		property.setCity(multipartRequest.getParameter("city"));
		property.setCommission(multipartRequest.getParameter("commission"));
		property.setCountry(multipartRequest.getParameter("country"));
		property.setCounty(multipartRequest.getParameter("county"));
		property.setDecoration(multipartRequest.getParameter("decoration"));
		try {
			String deliveryTime = StringUtils.trimToNull(multipartRequest.getParameter("deliveryTime"));
			if (deliveryTime!= null){
				DateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");  
				property.setDeliveryTime(format1.parse(deliveryTime));
			}
		} catch (Exception e){
			log.error("解析deliveryTime失败",e);
		}
		property.setGreenRate(NumberUtils.createBigDecimal(StringUtils.trimToNull(multipartRequest.getParameter("greenRate"))));
		try {
			String openDate = StringUtils.trimToNull(multipartRequest.getParameter("openDate"));
			if (openDate != null){
				DateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");  
				property.setOpenDate(format1.parse(openDate));
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
		property.setPropertyTel(multipartRequest.getParameter("propertyTel").trim());
		property.setTrafficFacilities(multipartRequest.getParameter("trafficFacilities"));
		property.setHouseType(multipartRequest.getParameter("houseType"));
		property.setPassTime(multipartRequest.getParameter("passTime"));
		
		if (StringUtils.isEmpty(property.getPropertyName())){
			json.setSuccess(false);
			json.setMessage("楼盘名称不能为空");
		}
		log.debug("属性设置完毕，开始修改数据库");
		if (json.validate()) {
			log.debug("修改业务数据");
			this.buildingService.update(property);
			log.debug("保存图片数据");
			if(null!=file&&!StringUtils.isEmpty(file.getOriginalFilename())){
				this.attachmentService.addUploadFile(file, file.getOriginalFilename(), property.getPropertyId(), Attachment.TableName.PIS_PROPERTY, Attachment.State.A);
			}
			log.debug("修改building数据");
			PisBuilding building = new PisBuilding();
			building.setBuildingId(property.getPropertyId());
			building.setBuildingName(property.getPropertyName());
			building.setCityId(property.getCity());
			this.buildingService.updateBuilding(building);
			json.setSuccess(true);
			json.setMessage("修改成功");
		}
		return json;
	}
	/**
	 * 获取楼盘分页信息
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/admin/properties",method=RequestMethod.GET)
	public Object selectPisProperties(HttpServletRequest request, ModelMap modelMap, HttpServletResponse response) {
		PageParam page = DataGridHepler.parseRequest(request);
		List<PisProperty> properties = buildingService.selectPisProperties(page.getPage(), page.getPageSize());
		for (PisProperty pisProperty : properties) {
			if (StringUtils.isNotBlank(pisProperty.getCity())){
				pisProperty.setCityTitle(this.cityService.getCityById(pisProperty.getCity()).getCityName());
			}
		}
		return DataGridHepler.addDataGrid(properties, modelMap); 
	}

	@RequestMapping(value="/api/properties",method=RequestMethod.GET)
	@ResponseBody
	public ModelMap pisPropertyVoList(HttpServletRequest request, ModelMap modelMap) {
		String server = request.getRequestURL().substring(0, request.getRequestURL().length()-request.getRequestURI().length());
		log.debug("server:" + server);
		String serverURL = server + request.getContextPath();
		log.debug("serverURL:" + serverURL);
		PageParam page = DataGridHepler.parseRequest(request);
		log.debug("page:" + page.getPage());
		log.debug("pageSize:" + page.getPageSize());
		List<PisProperty> properties = buildingService.selectPisProperties(page.getPage(), page.getPageSize());
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
				ConvertUtils.register(new DateConverter(null), java.util.Date.class); 
				ConvertUtils.register(new BigDecimalConverter(null), BigDecimal.class);
				BeanUtils.copyProperties(pisPropertyVo, pisProperty);
			} catch (Exception e) {
				log.error("拷贝数据出错", e);
			}
			pisPropertyVo.setFilePath(filePath);
			pisPropertyVoList.add(pisPropertyVo);
		}
		
		//modelAndView.addObject("properties", pisPropertyVoList);
		long total = new PageInfo(properties).getTotal();
		log.debug("本次获取的大小为:" + properties.size());
		log.debug("total:" + total);
		
		StringBuilder sb = new StringBuilder();
		log.debug("pisPropertyVoList.size:" + pisPropertyVoList.size());
		for (PisPropertyVo pisPropertyVo : pisPropertyVoList) {
			log.debug("filePath:" + pisPropertyVo.getFilePath());
			sb.append("<li class=\"mui-table-view-cell mui-media\">");
			sb.append("<a href=\"property-detail.html?index=" + pisPropertyVo.getPropertyId() + "\">");
			sb.append("	<img ");
			//这里不添加样式，使用图片本身大小
			sb.append("class=\"mui-media-object mui-pull-left\"");
			sb.append(" src=\"" + serverURL + "/" + pisPropertyVo.getFilePath() + "\">");
			sb.append("	<div class=\"mui-media-body\">");
			sb.append("		<h4 class=\"mui-ellipsis\">" + pisPropertyVo.getPropertyName() + "</h4>");
			sb.append("		<p><font style=\"background-color:darkgray;color:white;\">"+pisPropertyVo.getDecoration()+"</font>");
			sb.append("     <font style=\"background-color:darkgray;color:white;\">"+pisPropertyVo.getPropertyType()+"</font></p>");
			sb.append("		<p class=\"mui-ellipsis\">特点：" + StringUtils.trimToEmpty(pisPropertyVo.getCharacteristic()) + "</p>");
			sb.append("		<p class=\"mui-ellipsis\" style=\"color:#E58E26\">均价：" + pisPropertyVo.getAvgPrice() + "元/㎡</p>");
			sb.append("	</div>");
			sb.append("</a>");
			sb.append("</li>");
		}
		log.debug("html:" + sb.toString());
		Map<String, Object> json = new HashMap<String, Object>();
		json.put("total", total);
		json.put("html", sb.toString());
		modelMap.addAttribute("result", json);
		//modelMap.addAttribute("html", sb.toString());
//		modelAndView.setViewName("ydapp/propertyList");
		return modelMap;
	}

	/**
	 * 楼盘详情
	 * 
	 * @param id
	 * @param request
	 * @param modelMap
	 * @return
	 */
	@RequestMapping(value="/api/property/{id}", method=RequestMethod.GET)
	@ResponseBody
	public ModelMap  propertyDetail(@PathVariable String id, HttpServletRequest request, ModelMap modelMap) {
		PisProperty pisProperty = buildingService.selectByPrimaryKey(id);
		if (StringUtils.isNotEmpty(pisProperty.getCity())){
			PisCity city = this.cityService.getCityById(pisProperty.getCity());
			pisProperty.setCityTitle(city.getCityName());
		}
		PisPropertyVo pisPropertyVo = new PisPropertyVo();
		try {
			ConvertUtils.register(new DateConverter(null), java.util.Date.class); 
			ConvertUtils.register(new BigDecimalConverter(null), BigDecimal.class);
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
		Map<String, Object> json = new HashMap<String, Object>();
		DateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");  
		 SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		try {
			pisPropertyVo.setDeliveryTime(format1.parse(formatter.format(pisPropertyVo.getDeliveryTime())));
			pisPropertyVo.setOpenDate(format1.parse(formatter.format(pisPropertyVo.getOpenDate())));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		json.put("building", pisPropertyVo);
		log.debug("楼盘名称:" + pisPropertyVo.getPropertyName());
		//处理楼盘电话
		String tel = pisProperty.getPropertyTel();
		log.debug("楼盘电话:" + tel);
		List<String> tels = new ArrayList<>();
		if (StringUtils.isNoneEmpty(tel)){
			String[] tmp  = StringUtils.split(tel, ";");
			for (String string : tmp) {
				log.debug("电话:" + string);
				if(""!=string){
					tels.add("楼盘电话:"+string);
				}
			}
		}
		//得到案场专员列表
		List<PisUserExtend> list = this.userExtendService.selectByBuildingId(1, 30, pisProperty.getPropertyId());
		for (PisUserExtend pisUserExtend : list) {
			PisUser user = this.pisUserService.findUserById(pisUserExtend.getUserId());
			if(null!=user){
				 //获取用户类型
				 PisUserGroup group = pisUserService.getPisUserGroup(user.getUserId());
				log.debug("usertel:" + user.getTel()  + " nick:" + user.getNick());
				if("" != user.getTel() && tel.indexOf(user.getTel())==-1 && null != group && "commissioner".equals(group.getType())){
					tels.add(user.getNick() + ":" + user.getTel());
				}
			}
		}
		String telString = StringUtils.join(tels, ";");
		pisPropertyVo.setPropertyTel(telString);
		modelMap.addAttribute("result", json);
		return modelMap;
	}
	
	
	/**
	 * 楼盘详情
	 * 
	 * @param id
	 * @param request
	 * @param modelMap
	 * @return
	 */
	public ModelMap  propertyInfo(@PathVariable String id, HttpServletRequest request, ModelMap modelMap) {
		PisProperty pisProperty = buildingService.selectByPrimaryKey(id);
		if (StringUtils.isNotEmpty(pisProperty.getCity())){
			PisCity city = this.cityService.getCityById(pisProperty.getCity());
			pisProperty.setCityTitle(city.getCityName());
		}
		PisPropertyVo pisPropertyVo = new PisPropertyVo();
		try {
			ConvertUtils.register(new DateConverter(null), java.util.Date.class); 
			ConvertUtils.register(new BigDecimalConverter(null), BigDecimal.class);
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
		Map<String, Object> json = new HashMap<String, Object>();
		DateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");  
		 SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		try {
			pisPropertyVo.setDeliveryTime(format1.parse(formatter.format(pisPropertyVo.getDeliveryTime())));
			pisPropertyVo.setOpenDate(format1.parse(formatter.format(pisPropertyVo.getOpenDate())));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		json.put("building", pisPropertyVo);
		log.debug("楼盘名称:" + pisPropertyVo.getPropertyName());
		//处理楼盘电话
		modelMap.addAttribute("result", json);
		return modelMap;
	}

	/**
	 * 楼盘详情
	 * 
	 * @param id
	 * @param request
	 * @param modelMap
	 * @return
	 */
	@RequestMapping(value = "/api/property", method=RequestMethod.GET)
	@ResponseBody
	public ModelMap propertyDetail(HttpServletRequest request, ModelMap modelMap) {
		String id = request.getParameter("id"); 
		return propertyInfo(id, request, modelMap);
	}
	/**
	 * 插入数据
	 * 
	 * @param record
	 * @return
	 */
	@RequestMapping("/admin/property/houseType/add")
	public Object insertHouseTypeImage(HttpServletRequest request, ModelMap modelMap) {
		log.debug("添加户型");
		JsonVo<Object> json = new JsonVo<Object>();
		MultipartFile file = null;
		MultipartHttpServletRequest multipartRequest = null;
		// 转型为MultipartHttpRequest，如果没有上传图片是会报出异常：ClassCastException
		log.debug("转换request为附件方式");
		try {
			multipartRequest = (MultipartHttpServletRequest) request;
			file = multipartRequest.getFile("houseTypeImage");
		} catch (ClassCastException cce) {
			log.error("没有上传图片", cce);
		}
		if (file == null) {
			System.out.println("没有附件上传");
		}
		String houseTypeName = multipartRequest.getParameter("houseTypeName");
		String houseTypeImage = null;
		String viewWidthStr = request.getParameter("viewWidth");
		int width = NumberUtils.toInt(viewWidthStr, 0);
		if (json.validate()) {
			log.debug("保存图片数据");
			Attachment attachment = this.attachmentService.addUploadFile(file, file.getOriginalFilename(), null, TableName.NULL_TALBE, Attachment.State.A);
			String filePath = attachment.getSavePath();
			if (width > 10) {
				try {
					filePath = attachmentService.thumbnailator(attachment, width - 10);
				} catch (IOException e) {
					log.error("生成缩略图失败", e);
				}
			}
			houseTypeImage = filePath;
			
		}
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("houseTypeName", houseTypeName);
		jsonObject.put("houseTypeImage", houseTypeImage);
		log.debug("houseType:" + jsonObject);
		json.setResult(jsonObject);
		return json;
	}
	
	/**
	 *  删除楼盘信息
	 * @param request
	 * @param response
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping(value="/api/removeProperty")
	public JsonVo deleteProperty(HttpServletRequest request, HttpServletResponse response){
		JsonVo json = new JsonVo();
		//获取用户ID
		String propertyId =  StringUtils.trimToEmpty(request.getParameter("propertyId"));
		boolean flag =this.buildingService.deleteProperty(propertyId);
		//设置返回值
		json.setSuccess(flag);
		return json;
	}
}
