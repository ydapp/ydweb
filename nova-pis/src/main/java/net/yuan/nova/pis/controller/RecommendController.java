package net.yuan.nova.pis.controller;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.json.JSONObject;
import net.yuan.nova.core.shiro.CurrentUserUtil;
import net.yuan.nova.core.shiro.vo.User;
import net.yuan.nova.core.vo.JsonVo;
import net.yuan.nova.pis.controller.model.CustomerModel;
import net.yuan.nova.pis.entity.PisBuilding;
import net.yuan.nova.pis.entity.PisCity;
import net.yuan.nova.pis.entity.PisRecommend;
import net.yuan.nova.pis.entity.PisUser;
import net.yuan.nova.pis.entity.PisUserGroup;
import net.yuan.nova.pis.entity.vo.PisRecommendVo;
import net.yuan.nova.pis.pagination.DataGridHepler;
import net.yuan.nova.pis.pagination.PageParam;
import net.yuan.nova.pis.service.PisBuildingService;
import net.yuan.nova.pis.service.PisCityService;
import net.yuan.nova.pis.service.PisRecommendService;
import net.yuan.nova.pis.service.PisUserExtendService;
import net.yuan.nova.pis.service.PisUserService;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
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

import com.github.pagehelper.PageInfo;

/**
 * 推荐业务控制器
 * 
 * @author leasonlive
 *
 */
@Controller
public class RecommendController {
	protected final Logger log = LoggerFactory.getLogger(RecommendController.class);
	@Autowired
	private PisRecommendService recommendService;
	@Autowired
	private PisCityService pisCityService;
	@Autowired
	private PisBuildingService buildingService;
	@Autowired
	private PisUserService userService;
	@Autowired
	private PisUserExtendService userExtendService;
	
	private static Map<String, String> users = new HashMap<String, String>();
	private static Map<String, String> cities = new HashMap<String, String>();
	private static Map<String, String> builds = new HashMap<String, String>();

	/**
	 * 经纪人提交一个报备信息
	 * 
	 * @param recommendJson
	 * @param request
	 * @param modelMap
	 * @return
	 */
	@RequestMapping(value = "/api/addRecommend", method = RequestMethod.POST)
	@ResponseBody
	public ModelMap addRecommend(@RequestBody String recommendJson, HttpServletRequest request, ModelMap modelMap) {
		PisRecommend recommend = (PisRecommend) JSONObject.toBean(JSONObject.fromObject(recommendJson),
				PisRecommend.class);
		JsonVo<Object> jsonVo = new JsonVo<Object>();
		if (StringUtils.isBlank(recommend.getCustomerName())) {
			jsonVo.putError("customerName", "客户名称不能为空");
		}
		if (StringUtils.isBlank(recommend.getCustomerTel())) {
			jsonVo.putError("customerTel", "客户电话不能空");
		}
		if (StringUtils.isBlank(recommend.getCityId())) {
			jsonVo.putError("cityId", "推荐城市不能为空");
		}
		if (StringUtils.isBlank(recommend.getBuildingId())) {
			jsonVo.putError("buildingId", "推荐楼盘不能空");
		}
		if (recommend.getAppointmentLookHouseDate() == null) {
			jsonVo.putError("recommendDate", "预约看房日期不能为空");
		}
		if(this.recommendService.getCustomerByTodayAndTel(recommend.getCustomerTel(),recommend.getRecommendConfirmUserId())>3){
			jsonVo.putError("customerTelTopthread", "同一个客户当天不能报备超过三条");
		}
		if (StringUtils.isBlank(recommend.getRefreeId())) {
			User shiroUser = CurrentUserUtil.getShiroUser();
			recommend.setRefreeId(shiroUser.getUserId());
		}
		
		log.debug("json:" + JSONObject.fromObject(recommend).toString());
		// 验证通过后，插入数据
		if (jsonVo.validate()) {
			this.recommendService.insert(recommend);
		}
		modelMap.remove("pisRecommend");
		modelMap.addAttribute("result", jsonVo);
		return modelMap;
	}

	/**
	 * 经纪人查看所有楼盘待客户到场的报备信息
	 * 
	 * @param userId
	 *            经纪人id
	 * @param request
	 * @param modelMap
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/api/recommends/waitingPresent/{userId}", method = RequestMethod.GET)
	public ModelMap getWaitingPresent(@PathVariable String userId, HttpServletRequest request, ModelMap modelMap) {
		JsonVo<Object> jsonVo = new JsonVo<Object>();
		log.debug("userId:" + userId);
		List<PisRecommend> list = this.recommendService.getWaitingPresent(userId);
		jsonVo.setResult(list);
		return getResponse(jsonVo, modelMap);
	}

	/**
	 * 经纪人得到某个楼盘的待到场报备信息
	 * 
	 * @param userId
	 *            经纪人id
	 * @param buildingId
	 *            楼盘id
	 * @param request
	 * @param modelMap
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/api/recommends/waitingPresent/{userId}/{buildingId}", method = RequestMethod.GET)
	public ModelMap getWaitingPresent(@PathVariable String userId, @PathVariable String buildingId,
			HttpServletRequest request, ModelMap modelMap) {
		JsonVo<Object> jsonVo = new JsonVo<Object>();
		log.debug("userId:" + userId);
		log.debug("buildingId:" + buildingId);
		List<PisRecommend> list = this.recommendService.getWaitingPresent(userId, buildingId);
		jsonVo.setResult(list);
		return this.getResponse(jsonVo, modelMap);
	}

	/**
	 * 经纪人确认客户到场操作
	 * 
	 * @param recommendId
	 *            报备id
	 * @param presentUserId
	 *            经纪人id
	 * @param request
	 * @param modelMap
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/api/recommends/customerPresent/{recommendId}/{presentUserId}", method = RequestMethod.PUT)
	public ModelMap customerPresent(@PathVariable String recommendId, @PathVariable String presentUserId,
			HttpServletRequest request, ModelMap modelMap) {
		JsonVo<Object> jsonVo = new JsonVo<Object>();
		log.debug("recommendId:" + recommendId);
		log.debug("presentUserId:" + presentUserId);
		int rows = this.recommendService.customerPresent(recommendId, presentUserId);
		if (rows == 0) {
			jsonVo.putError("rows", "未能匹配记录");
		}
		return this.getResponse(jsonVo, modelMap);
	}

	/**
	 * 我的到场报备列表
	 * 
	 * @param presentUserId
	 * @param request
	 * @param modelMap
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/api/recommends/presented/{presentUserId}", method = RequestMethod.GET)
	public ModelMap getMyPresent(@PathVariable String presentUserId, HttpServletRequest request, ModelMap modelMap) {
		JsonVo<Object> jsonVo = new JsonVo<Object>();
		log.debug("presentUserId:" + presentUserId);
		List<PisRecommendVo> list = this.recommendService.getMyPresent(presentUserId);
		jsonVo.setResult(list);
		return this.getResponse(jsonVo, modelMap);
	}

	/**
	 * 报备专员得到需要确认的列表
	 * 
	 * @param userId
	 *            报备专员id
	 * @param request
	 * @param modelMap
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/api/recommends/waitingConfirm/{userId}", method = RequestMethod.GET)
	public ModelMap getWaitingConfirm(@PathVariable String userId, HttpServletRequest request, ModelMap modelMap) {
		JsonVo<Object> jsonVo = new JsonVo<Object>();
		log.debug("userId:" + userId);
		List<PisRecommend> list = this.recommendService.getWaitingConfirm(userId);
		jsonVo.setResult(list);
		return getResponse(jsonVo, modelMap);
	}

	/**
	 * 报备专员进行报备确认操作
	 * 
	 * @param recommendId
	 *            报备id
	 * @param confirmUserId
	 *            报备专员id
	 * @param request 里面一般会包含recommendConfirmAdvice参数
	 * @param modelMap
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/api/recommends/recommendConfirm/{recommendId}/{confirmUserId}", method = RequestMethod.PUT)
	public ModelMap recommendConfirm(@PathVariable String recommendId, @PathVariable String confirmUserId,
			HttpServletRequest request, ModelMap modelMap) {
		JsonVo<Object> jsonVo = new JsonVo<Object>();
		log.debug("recommendId:" + recommendId);
		log.debug("confirmUserId:" + confirmUserId);
		String recommendConfirmAdvice = request.getParameter("recommendConfirmAdvice");
		log.debug("recommendConfirmAdvice:" + recommendConfirmAdvice);
		int rows = this.recommendService.recommendConfirm(recommendId, confirmUserId,recommendConfirmAdvice);
		if (rows == 0) {
			jsonVo.putError("rows", "未能匹配记录");
		}
		return this.getResponse(jsonVo, modelMap);
	}

	@ResponseBody
	@RequestMapping(value = "/api/recommends/confirmed/{confirmUserId}", method = RequestMethod.GET)
	public ModelMap getMyConfirm(@PathVariable String confirmUserId, HttpServletRequest request, ModelMap modelMap) {
		JsonVo<Object> jsonVo = new JsonVo<Object>();
		log.debug("confirmUserId:" + confirmUserId);
		List<PisRecommend> list = this.recommendService.getMyConfirm(confirmUserId);
		jsonVo.setResult(list);
		return this.getResponse(jsonVo, modelMap);
	}

	private ModelMap getResponse(JsonVo<Object> jsonVo, ModelMap modelMap) {
		jsonVo.validate();
		modelMap.addAttribute("result", jsonVo);
		return modelMap;
	}

	/**
	 * 根据ID获得详细信息
	 * 
	 * @param recommendId
	 * @param modelMap
	 * @return
	 */
	@RequestMapping(value = "/api/recommends/{recommendId}")
	public ModelMap getById(@PathVariable String recommendId, ModelMap modelMap) {
		JsonVo<Object> jsonVo = new JsonVo<Object>();
		jsonVo.validate();
		PisRecommendVo recommend = recommendService.getById(recommendId);
		jsonVo.setResult(recommend);
		modelMap.addAttribute("result", jsonVo);
		return modelMap;
	}

	@ResponseBody
	@RequestMapping(value = "/api/excell", method = RequestMethod.GET)
	public ModelMap downAsExcell(HttpServletRequest request, ModelMap modelMap, HttpServletResponse response) {
		List<PisRecommend> list = null;
		User user = CurrentUserUtil.getShiroUser();
		log.debug("根据用户类型确定导出范围");
		PisUser pisUser = this.userService.findUserById(user.getUserId());
		PisUserGroup group = this.userService.getPisUserGroup(user.getUserId());
		if (group == null){
			JsonVo<Object> jsonVo = new JsonVo<Object>();
			jsonVo.setSuccess(false);
			jsonVo.setMessage("该用户不具备导出报备数据权限(非APP管理员/驻场专员/经纪公司/经纪人)");
			jsonVo.validate();
			modelMap.addAttribute("result", jsonVo);
			return modelMap;
		}
		if (StringUtils.equals(PisUserGroup.TYPE.appAdmin.name(), group.getType())){
			log.debug("当前用户是app管理员，可以导出所有的推荐信息");
			list = this.recommendService.getAll();
		} else if (StringUtils.equals(PisUserGroup.TYPE.brokingFirm.name(), group.getType())){
			log.debug("当前用户是经纪公司，可以导出该经纪公司下面的所有的推荐信息");
			String brokingFirmId = this.userExtendService.selectByUserId(user.getUserId()).getBrokingFirmId();
			list = this.recommendService.getByBrokingFirmId(brokingFirmId);
		}else if (StringUtils.equals(PisUserGroup.TYPE.salesman.name(), group.getType())){
			log.debug("当前用户是经纪人，可以导出该经纪人的所有的推荐信息");
			list = this.recommendService.getBySaleman(user.getUserId());
		}else if (StringUtils.equals(PisUserGroup.TYPE.commissioner.name(), group.getType())){
			log.debug("当前用户是驻场专员，可以导出该人的楼盘的所有的推荐信息");
			String buildingId = this.userExtendService.selectByUserId(user.getUserId()).getBuildingId();
			list = this.recommendService.getByBuildingId(buildingId);
		}
		
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet("报备信息");
		HSSFRow rowHeard = sheet.createRow(0);
		List<String> header = new ArrayList<String>();
		header.add("推荐id");
		header.add("客户名称");
		header.add("客户电话");
		header.add("城市");
		header.add("楼盘");
		//header.add("预计看房时间");
		header.add("推荐人");
		header.add("详情");
		header.add("推荐状态");
		header.add("推荐时间");
		header.add("客户到场时间");
		header.add("客户到场确认人");
		header.add("推荐确认时间");
		header.add("推荐确认人");
		header.add("推荐确认意见");
		for (int i = 0; i < header.size(); i++) {
			HSSFCell cellHeard = rowHeard.createCell(i);
			cellHeard.setCellValue(header.get(i));
		}
		for (int i = 0; i < list.size(); i++) {
			HSSFRow rowData = sheet.createRow(i + 1);
			PisRecommend recommend = list.get(i);
			List<String> data = new ArrayList<String>();
			data.add(recommend.getRecommendId());
			data.add(recommend.getCustomerName());
			data.add(recommend.getCustomerTel());
			data.add(this.getCityName(recommend.getCityId()));
			data.add(this.getBuildingName(recommend.getBuildingId()));
			//data.add(this.formatDate(recommend.getAppointmentLookHouseDate()));
			data.add(this.getUserName(recommend.getRefreeId()));
			data.add(recommend.getRemark());
			data.add(recommend.getStatusName());
			data.add(formatDate(recommend.getRecommendDate()));
			if (recommend.getCustomerPresentDate() != null) {
				data.add(formatDate(recommend.getCustomerPresentDate()));
				data.add(this.getUserName(recommend.getCustomerPresentUserId()));
			} else {
				data.add("");
				data.add("");
			}
			if (recommend.getRecommendConfirmDate() != null) {
				data.add(formatDate(recommend.getRecommendConfirmDate()));
				data.add(this.getUserName(recommend.getRecommendConfirmUserId()));
				data.add(recommend.getRecommendConfirmAdvice());
			} else {
				data.add("");
				data.add("");
				data.add("");
			}
			for (int j = 0; j < data.size(); j++) {
				HSSFCell cellData = rowData.createCell(j);
				cellData.setCellValue(data.get(j));
			}
		}
		try {
			response.setContentType("application/octet-stream");
			response.setHeader("Content-disposition", "attachment; filename=recommends.xls");
			OutputStream os = response.getOutputStream();
			wb.write(os);
			os.flush();
			os.close();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return null;

		//
		// // 设置文件大小
		// response.setHeader("Content-Length", String.valueOf(fileLength));
		// IOUtils.copyLarge(bis, bos);
	}

	private String formatDate(Date date) {
		if (date == null) {
			return "";
		}
		return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
	}

	private String getUserName(String userId) {
		if (!this.users.containsKey(userId)) {
			PisUser user = this.userService.findUserById(userId);
			this.users.put(userId, user.getNick() + "(" + user.getTel() + ")");
		}
		return this.users.get(userId);
	}

	private String getCityName(String cityId) {
		if (!this.cities.containsKey(cityId)) {
			PisCity city = this.pisCityService.getCityById(cityId);
			this.cities.put(cityId, city.getCityName());
		}
		return this.cities.get(cityId);
	}

	private String getBuildingName(String buildingId) {
		if (!this.builds.containsKey(buildingId)) {
			PisBuilding building = this.buildingService.getById(buildingId);
			this.builds.put(buildingId, building.getBuildingName());
		}
		return this.builds.get(buildingId);
	}
	@ResponseBody
	@RequestMapping(value = "/api/customers", method = RequestMethod.GET)
	public ModelMap customer(HttpServletRequest request, ModelMap modelMap, HttpServletResponse response) {
		PageParam param = DataGridHepler.parseRequest(request);
		List<CustomerModel> list = this.recommendService.getCustomers(param.getPage(), param.getPageSize());
		return DataGridHepler.addDataGrid(list, modelMap);
	}
}
