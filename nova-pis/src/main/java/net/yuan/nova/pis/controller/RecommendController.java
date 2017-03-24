package net.yuan.nova.pis.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import net.yuan.nova.pis.controller.model.AppendRemarkModel;
import net.yuan.nova.pis.controller.model.ConfirmModel;
import net.yuan.nova.pis.controller.model.CustomerModel;
import net.yuan.nova.pis.entity.PisBuilding;
import net.yuan.nova.pis.entity.PisCity;
import net.yuan.nova.pis.entity.PisProperty;
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

import com.sun.tools.internal.ws.processor.model.Model;

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
	private static List<Map<String,Object>> recommends = new ArrayList<>();
	

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
//		if (recommend.getAppointmentLookHouseDate() == null) {
//			jsonVo.putError("recommendDate", "预约看房日期不能为空");
//		}
		if (StringUtils.isBlank(recommend.getRemark())) {
			jsonVo.putError("remark", "详情不能空");
		}
		if(this.recommendService.getCustomerByTodayAndTel(recommend.getCustomerTel(),recommend.getRefreeId())>3){
			jsonVo.putError("customerTelTopthread", "同一个客户当天不能报备超过三条");
		}
		if (StringUtils.isBlank(recommend.getRefreeId())) {
			User shiroUser = CurrentUserUtil.getShiroUser();
			recommend.setRefreeId(shiroUser.getUserId());
		}
		recommend.setStatus(PisRecommend.Status.appointment);
		
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
	 *  经纪人查看所有楼盘待客户到场的报备信息
	 * @param request
	 * @param modelMap
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/api/recommends/waitingPresentKey", method = RequestMethod.GET)
	public ModelMap getWaitingPersentKey(HttpServletRequest request, ModelMap modelMap){
		JsonVo<Object> jsonVo = new JsonVo<Object>();
		String userId = request.getParameter("userId");
		String keyWord = request.getParameter("keyWord");
	    List<PisRecommend> list = this.recommendService.getWaitingPresentByKey(keyWord,userId);
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
	 * 得到详细信息
	 * @return 
	 */
	@ResponseBody
	@RequestMapping(value = "/api/getRecommends", method = RequestMethod.GET)
	public ModelMap getRecommends( HttpServletRequest request, ModelMap modelMap){
		JsonVo<Object> jsonVo = new JsonVo<Object>();
		String recommendId = request.getParameter("recommendId");
		PisRecommendVo recommend = recommendService.getById(recommendId);
		jsonVo.setResult(recommend);
		return this.getResponse(jsonVo, modelMap);
	}
	
	/**
	 * 获取报备状态非“报备确认”的报备集合
	 * @param presentUserId
	 * @param request
	 * @param modelMap
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/api/recommends/presented/doing/{presentUserId}", method = RequestMethod.GET)
	public  ModelMap getMyDoingPresent(@PathVariable String presentUserId, HttpServletRequest request, ModelMap modelMap){
		JsonVo<Object> jsonVo = new JsonVo<Object>();
		log.debug("presentUserId:" + presentUserId);
		//组装状态非“确认报备”
		String status = "'"+PisRecommend.Status.present+"','"+PisRecommend.Status.pledges+"','"+PisRecommend.Status.order+"'";
		//获取自己提交的并且状态非“报备确认”的报备集合
		List<PisRecommendVo> list =this.recommendService.getMyPresentByStatus(presentUserId, status);
		//装载数据集合
		jsonVo.setResult(list);
		return this.getResponse(jsonVo, modelMap);
	}
	
	@ResponseBody
	@RequestMapping(value = "/api/recommends/presented/doingKey", method = RequestMethod.GET)
	public ModelMap getMyPresentByStatusKey(HttpServletRequest request, ModelMap modelMap){
		JsonVo<Object> jsonVo = new JsonVo<Object>();
		String userId = request.getParameter("userId");
		String keyWord = request.getParameter("keyWord");
		//组装状态非“确认报备”
		//String status = "'"+PisRecommend.Status.present+"','"+PisRecommend.Status.pledges+"','"+PisRecommend.Status.order+"'";
		PisRecommend pisRecommend = new PisRecommend();
		pisRecommend.setRefreeId(userId);
		pisRecommend.setCustomerTel(keyWord);
		//获取自己提交的并且状态非“报备确认”的报备集合
		List<PisRecommendVo> list =this.recommendService.getMyPresentByStatusKey(pisRecommend);
		//装载数据集合
		jsonVo.setResult(list);
		return this.getResponse(jsonVo, modelMap);
	}
	
	/**
	 * 获取报备状态“报备确认”的报备列表
	 * @param presentUserId
	 * @param request
	 * @param modelMap
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/api/recommends/presented/done/{presentUserId}", method = RequestMethod.GET)
	public ModelMap getMyDonePresent(@PathVariable String presentUserId, HttpServletRequest request, ModelMap modelMap){
		JsonVo<Object> jsonVo = new JsonVo<Object>();
		log.debug("presentUserId:" + presentUserId);
		//获取自己提交的并且状态“报备确认”的报备集合
		List<PisRecommendVo> list = this.recommendService.getMyPresentByStatus(presentUserId,"'"+PisRecommend.Status.confirm+"'");
		//装载数据集合
		jsonVo.setResult(list);
		return this.getResponse(jsonVo, modelMap);
	}

	/**
	 * 驻场专员得到需要确认的列表
	 * 包括来
	 * 
	 * @param userId
	 *            驻场专员id
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
	 * 通过关键字获取信息
	 * @param request
	 * @param modelMap
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/api/recommends/waitingConfirmByKey", method = RequestMethod.GET)
	public ModelMap getWaitingConfirmByKey(HttpServletRequest request, ModelMap modelMap){
		JsonVo<Object> jsonVo = new JsonVo<Object>();
		String userId = request.getParameter("userId");
		String keyWord = request.getParameter("keyWord");
		log.debug("userId:" + userId);
		List<PisRecommend> list = this.recommendService.getWaitingConfirmByKey(keyWord,userId);
		jsonVo.setResult(list);
		return getResponse(jsonVo, modelMap);
	}
	
	
	/**
	 * 驻场专员确认“来”
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/api/recommends/waitingComeKey", method = RequestMethod.GET)
	public ModelMap getWaitingComeKey(HttpServletRequest request, ModelMap modelMap){
		JsonVo<Object> jsonVo = new JsonVo<Object>();
		String userId = request.getParameter("userId");
		String keyword = request.getParameter("keyWord");
		List<PisRecommend> list = this.recommendService.getWaitingComeKey(keyword,userId);
		jsonVo.setResult(list);
		return getResponse(jsonVo, modelMap);
	} 
	/**
	 * 驻场专员确认“来”
	 * @param userId
	 * 				驻场专员ID
	 * @param request
	 * @param modelMap
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/api/recommends/waitingCome/{userId}", method = RequestMethod.GET)
	public ModelMap getWaitingCome(@PathVariable String userId, HttpServletRequest request, ModelMap modelMap){
		JsonVo<Object> jsonVo = new JsonVo<Object>();
		log.debug("userId:" + userId);
		List<PisRecommend> list = this.recommendService.getWaitingCome(userId);
		jsonVo.setResult(list);
		return getResponse(jsonVo, modelMap);
	}

	/**
	 * 驻场专员进行报备确认操作
	 * 筹、订、购、报备确认四个过程，合成一个方法
	 * @param recommendId
	 *            报备id
	 * @param confirmUserId
	 *            驻场专员id
	 * @param request 里面一般会包含recommendConfirmAdvice参数
	 * @param modelMap
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/api/recommends/recommendConfirm", method = RequestMethod.POST)
	public ModelMap recommendConfirm(@RequestBody String confirmModelJSON, HttpServletRequest request,ModelMap modelMap) {
		log.debug("confirmModelJSON:" + confirmModelJSON);
		ConfirmModel confirmModel = (ConfirmModel)JSONObject.toBean(JSONObject.fromObject(confirmModelJSON),ConfirmModel.class);
		JsonVo<Object> jsonVo = new JsonVo<Object>();
		
		log.debug("recommendId:" + confirmModel.getRecommendId());
		log.debug("confirmUserId:" + confirmModel.getConfirmUserId());
		log.debug("recommendConfirmAdvice:" + confirmModel.getRecommendConfirmAdvice());
		int rows = this.recommendService.recommendConfirm(confirmModel.getRecommendId(), confirmModel.getConfirmUserId(),confirmModel.getRecommendConfirmAdvice());
		if (rows == 0) {
			jsonVo.putError("rows", "未能匹配记录");
		}
		return this.getResponse(jsonVo, modelMap);
	}
 
	/**
	 * 追加详情操作
	 * 状态只要不是确认，都可以随时追加详情
	 * @param recommendId
	 *            报备id
	 * @param confirmUserId
	 *            驻场专员id
	 * @param request 里面一般会包含recommendConfirmAdvice参数
	 * @param modelMap
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/api/recommends/recommendAppendRemark", method = RequestMethod.POST)
	public ModelMap recommendAppendRemark(@RequestBody String appendRemarkModelJSON, HttpServletRequest request,ModelMap modelMap) {
		log.debug("appendRemarkModelJSON:" + appendRemarkModelJSON);
		AppendRemarkModel appendRemarkModel = (AppendRemarkModel)JSONObject.toBean(JSONObject.fromObject(appendRemarkModelJSON),AppendRemarkModel.class);
		JsonVo<Object> jsonVo = new JsonVo<Object>();
		
		log.debug("recommendId:" + appendRemarkModel.getRecommendId());
		log.debug("appendUserId:" + appendRemarkModel.getAppendUserId());
		log.debug("recommendAppendRemark:" + appendRemarkModel.getRecommendAppendRemark());
		int rows = this.recommendService.recommendAppendRemark(appendRemarkModel.getRecommendId(), appendRemarkModel.getAppendUserId(), appendRemarkModel.getRecommendAppendRemark());
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
	
	/**
	 * 驻场专员正在处理的报备
	 * @param confirmUserId
	 * @param request
	 * @param modelMap
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/api/recommends/waitingCome/doing/{confirmUserId}", method = RequestMethod.GET)
	public ModelMap getMyDoingWaitingCome(@PathVariable String confirmUserId, HttpServletRequest request, ModelMap modelMap){
		JsonVo<Object> jsonVo = new JsonVo<Object>();
		//组装状态非“确认报备”
		String status = "'"+PisRecommend.Status.present+"','"+PisRecommend.Status.pledges+"','"+PisRecommend.Status.order+"'";
		//通过推荐用户ID和状态获取非“确认报备”报备集合信息
		List<PisRecommend> list = this.recommendService.getMyWaitingComeByStatus(confirmUserId, status);
		//装载数据集合
		jsonVo.setResult(list);
		return this.getResponse(jsonVo, modelMap);
	}
	
	/**
	 * 驻场专员正在处理的报备
	 * @param request
	 * @param modelMap
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/api/recommends/waitingCome/doingKey", method = RequestMethod.GET)
	public ModelMap getMyWaitingComeByStatusKey(HttpServletRequest request, ModelMap modelMap){
		JsonVo<Object> jsonVo = new JsonVo<Object>();
		String userId = request.getParameter("userId");
		String keyWord = request.getParameter("keyWord");
		PisRecommend pisRecommend = new PisRecommend();
		pisRecommend.setCustomerPresentUserId(userId);
		pisRecommend.setCustomerTel(keyWord);
		List<PisRecommend> list = this.recommendService.getMyWaitingComeByStatusKey(pisRecommend);
		//装载数据集合
		jsonVo.setResult(list);
		return this.getResponse(jsonVo, modelMap);
	}
	
	/**
	 * 通过推荐人ID获取状态非“报备确认”报备集合
	 * @param confirmUserId
	 * @param request
	 * @param modelMap
	 * @return
	 */
	 @ResponseBody
    @RequestMapping(value = "/api/recommends/confirmed/doing/{confirmUserId}", method = RequestMethod.GET)
	public ModelMap getMyDoingConfirm(@PathVariable String confirmUserId, HttpServletRequest request, ModelMap modelMap){
		JsonVo<Object> jsonVo = new JsonVo<Object>();
		//组装状态非“确认报备”
		String status = "'"+PisRecommend.Status.present+"','"+PisRecommend.Status.pledges+"','"+PisRecommend.Status.order+"'";
		//通过推荐用户ID和状态获取非“确认报备”报备集合信息
		List<PisRecommend> list = this.recommendService.getMyConfirmByStatus(confirmUserId, status);
		//装载数据集合
		jsonVo.setResult(list);
		return this.getResponse(jsonVo, modelMap);
	}
	 
	 @ResponseBody
	 @RequestMapping(value = "/api/recommends/confirmed/doingKey", method = RequestMethod.GET)
	 public ModelMap getMyConfirmByStatusKey(HttpServletRequest request, ModelMap modelMap){
		 JsonVo<Object> jsonVo = new JsonVo<Object>();
		 String userId = request.getParameter("userId");
		String keyWord = request.getParameter("keyWord");
		PisRecommend pisRecommend = new PisRecommend();
		pisRecommend.setCustomerPresentUserId(userId);
		pisRecommend.setCustomerTel(keyWord);
		//通过推荐用户ID和状态获取非“确认报备”报备集合信息
		List<PisRecommend> list = this.recommendService.getMyConfirmByStatusKey(pisRecommend);
		jsonVo.setResult(list);
		return this.getResponse(jsonVo, modelMap);
	 }
	 
	 /**
	  * 驻场专员“确定报备”来
	  * @param confirmUserId
	  * @param request
	  * @param modelMap
	  * @return
	  */
	 @ResponseBody
	 @RequestMapping(value = "/api/recommends/waitingCome/done/{confirmUserId}", method = RequestMethod.GET)
	 public ModelMap getMyDoneWaitingCome(@PathVariable String confirmUserId, HttpServletRequest request, ModelMap modelMap){
			JsonVo<Object> jsonVo = new JsonVo<Object>();
			//通过推荐人ID和状态获取“确认报备”集合信息
			List<PisRecommend> list = this.recommendService.getMyWaitingComeByStatus(confirmUserId, "'"+PisRecommend.Status.confirm+"'");
			//装载数据集合
			jsonVo.setResult(list);
			return this.getResponse(jsonVo, modelMap);
	 }
	 /**
	  * 通过推荐人ID获取状态“确认报备”报备集合
	  * @param confirmUserId
	  * @param request
	  * @param modelMap
	  * @return
	  */
	 @ResponseBody
	 @RequestMapping(value = "/api/recommends/confirmed/done/{confirmUserId}", method = RequestMethod.GET)
	 public  ModelMap getMyDoneConfirm(@PathVariable String confirmUserId, HttpServletRequest request, ModelMap modelMap){
			JsonVo<Object> jsonVo = new JsonVo<Object>();
			//通过推荐人ID和状态获取“确认报备”集合信息
			List<PisRecommend> list = this.recommendService.getMyConfirmByStatus(confirmUserId, "'"+PisRecommend.Status.confirm+"'");
			//装载数据集合
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

	@SuppressWarnings("resource")
	@ResponseBody
	@RequestMapping(value = "/api/excell", method = RequestMethod.GET)
	public ModelMap downAsExcell(HttpServletRequest request, ModelMap modelMap, HttpServletResponse response) {
		List<PisRecommend> list = null;
		User user = CurrentUserUtil.getShiroUser();
		log.debug("根据用户类型确定导出范围");
		PisUserGroup group = this.userService.getPisUserGroup(user.getUserId());
		if (group == null){
			try {
				response.getWriter().write("该用户不具备导出报备数据权限(非APP管理员/驻场专员/经纪公司/经纪人)");
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			return null;
		}
		if (StringUtils.equals(PisUserGroup.TYPE.appAdmin.name(), group.getType())){
			log.debug("当前用户是app管理员，可以导出所有的推荐信息");
			list = this.recommendService.getAll(0,0);
		} else if (StringUtils.equals(PisUserGroup.TYPE.brokingFirm.name(), group.getType())){
			log.debug("当前用户是经纪公司，可以导出该经纪公司下面的所有的推荐信息");
			String brokingFirmId = this.userExtendService.selectByUserId(user.getUserId()).getBrokingFirmId();
			list = this.recommendService.getByBrokingFirmId(brokingFirmId,0,0);
		}else if (StringUtils.equals(PisUserGroup.TYPE.salesman.name(), group.getType())){
			log.debug("当前用户是经纪人，可以导出该经纪人的所有的推荐信息");
			list = this.recommendService.getBySaleman(user.getUserId(),0,0);
		}else if (StringUtils.equals(PisUserGroup.TYPE.commissioner.name(), group.getType())){
			log.debug("当前用户是驻场专员，可以导出该人的楼盘的所有的推荐信息");
			String buildingId = this.userExtendService.selectByUserId(user.getUserId()).getBuildingId();
			list = this.recommendService.getByBuildingId(buildingId,0,0);
		}
		String building = request.getParameter("building");
		String startDate = request.getParameter("startDate");
		String endDate = request.getParameter("endDate");
		int date_01 = 0;
		int date_02 = 0;
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");//小写的mm表示的是分钟
		Date date = null;
		if("" != startDate && startDate.length()>0){
			try {
				  date=sdf.parse(startDate);
			} catch (ParseException e) {
				log.debug(startDate+"转换Date错误"+e.getMessage());
			}  
			String day = String.valueOf(new java.text.SimpleDateFormat("yyyyMMdd").format(date)).trim();
			date_01 = Integer.parseInt(day);
		}
		if("" != endDate && endDate.length()>0){
			try {
				  date=sdf.parse(endDate);
			} catch (ParseException e) {
				log.debug(endDate+"转换Date错误"+e.getMessage());
			}  
			String day = String.valueOf(new java.text.SimpleDateFormat("yyyyMMdd").format(date)).trim();
			date_02 = Integer.parseInt(day);
		}
		list = this.filterList(building, date_01, date_02, list);
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
		if(null != list&&list.size()>0){
			Collections.sort(list, new Comparator<PisRecommend>(){
				@Override
				public int compare(PisRecommend pisRecommend_01, PisRecommend pisRecommend_02) {
					if(null != pisRecommend_01 && null != pisRecommend_02){
							String day_01 = String.valueOf(new java.text.SimpleDateFormat("yyyyMMdd").format(pisRecommend_01.getRecommendDate())).trim();
							String day_02 =String.valueOf(new java.text.SimpleDateFormat("yyyyMMdd").format(pisRecommend_02.getRecommendDate())).trim();
							String time_01 = String.valueOf(new java.text.SimpleDateFormat("HHmmss").format(pisRecommend_01.getRecommendDate())).trim();
							String time_02 = String.valueOf(new java.text.SimpleDateFormat("HHmmss").format(pisRecommend_02.getRecommendDate())).trim();
							int recommendDay_01 = Integer.parseInt(day_01);
							int recommendDay_02 = Integer.parseInt(day_02);
							if(recommendDay_01 == recommendDay_02){
								int recommendTime_01 = Integer.parseInt(time_01);
								int recommendTime_02 = Integer.parseInt(time_02);
								 return  recommendTime_01 - recommendTime_02>0?-1:0;
							}else{
								return recommendDay_01 - recommendDay_02>0?-1:0;
							}
					}
					return 0;
				}
			});
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
	
	
	/**
	 * 推荐信息导出列表
	 * @param request
	 * @param modelMap
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/recommend/getList",method=RequestMethod.GET)
	public Object getRecommendList(HttpServletRequest request, ModelMap modelMap, HttpServletResponse response){
		List<PisRecommend> list = null;
		User user = CurrentUserUtil.getShiroUser();
		log.debug("根据用户类型确定导出范围");
		PisUserGroup group = this.userService.getPisUserGroup(user.getUserId());
		if (group == null){
			try {
				response.getWriter().write("该用户不具备导出报备数据权限(非APP管理员/驻场专员/经纪公司/经纪人)");
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		String building = request.getParameter("building");
		String startDate = request.getParameter("startDate");
		String endDate = request.getParameter("endDate");
		if(null == building){building ="";}
		if(null == startDate){startDate="";}
		if(null == endDate){endDate ="";}
		PageParam param = DataGridHepler.parseRequest(request);
		if (StringUtils.equals(PisUserGroup.TYPE.appAdmin.name(), group.getType())){
			log.debug("当前用户是app管理员，可以导出所有的推荐信息");
			if("" != building){
				list = this.recommendService.getByBuildingIds(building, param.getPage(),param.getPageSize());
			}else{
				list = this.recommendService.getAll(param.getPage(),param.getPageSize());
			}
		} else if (StringUtils.equals(PisUserGroup.TYPE.brokingFirm.name(), group.getType())){
			log.debug("当前用户是经纪公司，可以导出该经纪公司下面的所有的推荐信息");
			String brokingFirmId = this.userExtendService.selectByUserId(user.getUserId()).getBrokingFirmId();
			list = this.recommendService.getByBrokingFirmId(brokingFirmId,param.getPage(),param.getPageSize());
		}else if (StringUtils.equals(PisUserGroup.TYPE.salesman.name(), group.getType())){
			log.debug("当前用户是经纪人，可以导出该经纪人的所有的推荐信息");
			list = this.recommendService.getBySaleman(user.getUserId(),param.getPage(),param.getPageSize());
		}else if (StringUtils.equals(PisUserGroup.TYPE.commissioner.name(), group.getType())){
			log.debug("当前用户是驻场专员，可以导出该人的楼盘的所有的推荐信息");
			String buildingId = this.userExtendService.selectByUserId(user.getUserId()).getBuildingId();
			list = this.recommendService.getByBuildingId(buildingId,param.getPage(),param.getPageSize());
		}
		int date_01 = 0;
		int date_02 = 0;
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");//小写的mm表示的是分钟
		Date date = null;
		if("" != startDate && startDate.length()>0){
			try {
				  date=sdf.parse(startDate);
			} catch (ParseException e) {
				log.debug(startDate+"转换Date错误"+e.getMessage());
			}  
			String day = String.valueOf(new java.text.SimpleDateFormat("yyyyMMdd").format(date)).trim();
			date_01 = Integer.parseInt(day);
		}
		if("" != endDate && endDate.length()>0){
			try {
				  date=sdf.parse(endDate);
			} catch (ParseException e) {
				log.debug(endDate+"转换Date错误"+e.getMessage());
			}  
			String day = String.valueOf(new java.text.SimpleDateFormat("yyyyMMdd").format(date)).trim();
			date_02 = Integer.parseInt(day);
		}
		if(date_01>0 || date_02 >0){
			list = this.filterList(date_01, date_02, list);
		}
		if(null != list&&list.size()>0){
			//按照中文排序业务员集合
			Collections.sort(list, new Comparator<PisRecommend>(){
				@Override
				public int compare(PisRecommend pisRecommend_01, PisRecommend pisRecommend_02) {
					if(null != pisRecommend_01 && null != pisRecommend_02){
							String day_01 = String.valueOf(new java.text.SimpleDateFormat("yyyyMMdd").format(pisRecommend_01.getRecommendDate())).trim();
							String day_02 =String.valueOf(new java.text.SimpleDateFormat("yyyyMMdd").format(pisRecommend_02.getRecommendDate())).trim();
							String time_01 = String.valueOf(new java.text.SimpleDateFormat("HHmmss").format(pisRecommend_01.getRecommendDate())).trim();
							String time_02 = String.valueOf(new java.text.SimpleDateFormat("HHmmss").format(pisRecommend_02.getRecommendDate())).trim();
							int recommendDay_01 = Integer.parseInt(day_01);
							int recommendDay_02 = Integer.parseInt(day_02);
							if(recommendDay_01 == recommendDay_02){
								int recommendTime_01 = Integer.parseInt(time_01);
								int recommendTime_02 = Integer.parseInt(time_02);
								 return  recommendTime_01 - recommendTime_02>0?-1:0;
							}else{
								return recommendDay_01 - recommendDay_02>0?-1:0;
							}
					}
					return 0;
				}
			});
			for (int i = 0; i < list.size(); i++) {
				PisRecommend pisRec = list.get(i);
				if(null != pisRec){
					pisRec.setCityName(this.getCityName(pisRec.getCityId()));
					pisRec.setBuildingName(this.getBuildingName(pisRec.getBuildingId()));
					pisRec.setRefreeName(this.getUserName(pisRec.getRefreeId()));
					pisRec.setCustomerPresentName(this.getUserName(pisRec.getCustomerPresentUserId()));
					pisRec.setRecommendConfirmName(this.getUserName(pisRec.getRecommendConfirmUserId()));
				}
			}
		}
		return  DataGridHepler.addDataGrid(list, modelMap); //DataGridHepler.addDataGrid(list_01, new PageInfo(list).getTotal(), modelMap);
	}

	private String formatDate(Date date) {
		if (date == null) {
			return "";
		}
		return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
	}

	@SuppressWarnings("static-access")
	private String getUserName(String userId) {
		if (!this.users.containsKey(userId)) {
			PisUser user = this.userService.findUserById(userId);
			if(null!=user){
				this.users.put(userId, user.getNick() + "(" + user.getTel() + ")");
			}
		}
		return this.users.get(userId);
	}
	@SuppressWarnings("static-access")
	private String getCityName(String cityId) {
		if (!this.cities.containsKey(cityId)) {
			PisCity city = this.pisCityService.getCityById(cityId);
			this.cities.put(cityId, city.getCityName());
		}
		return this.cities.get(cityId);
	}
	@SuppressWarnings("static-access")
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
	/**
	 * 每天晚上十二点定时检查推荐信息
	 */
	public void validateRecommend(){
		SimpleDateFormat test= new SimpleDateFormat("yyyy-MM-dd HH:MM:ss");//设置日期格式
		log.debug("开始执行定时验证推荐信息是否设置为'来',执行时间："+test.format(new Date()));
		boolean flag = false;
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");//设置日期格式
		String day=df.format(new Date());//获取当天日期
		//获取报备的推荐信息状态为“appointment”申请报备
		List<PisRecommend>recoList = recommendService.getRecommendByStatus(PisRecommend.Status.appointment.toString());
		//判断内存数据集合是否为空
		if(null!=recommends&&recommends.size()>0){
				//判断数据集合不为空
				if(null!=recoList&&recoList.size()>0){
					 //判断两个集合长度是否一致
					 log.debug("判断内存集合与数据集合长度是否一样");
					 if(recommends.size()==recoList.size()){
						 //判断内存集合中是否包含数据集
						 String pisRecommendStr="";
						 String  recommendStr="";
						 String key="";
						 //循环读取内存集合中key
						 for (Map<String,Object> map_:recommends) {
							 key=map_.keySet().toString();
							 key=key.substring(1,key.length()-1);
							 recommendStr+=key+",";
						}
						 //循环读取数据集合中主键ID
						 for (PisRecommend pisRecommend : recoList) {
							 pisRecommendStr+=pisRecommend.getRecommendId()+",";
						}
						 //根据集合顺序比对是否一样
						 if(recommendStr.equals(pisRecommendStr)){
							 flag = true;
						 }
					 }
					 if(!flag){
						 List<Map<String,Object>> newListMap = new ArrayList<>();//用于存新添加的推荐信息
						 log.debug("以数据集合为准判断是否有添加新的申请推荐信息");
						 addRecommends(recoList,recommends,newListMap,day);
						 log.debug("以内存集合为准判断是否有改变状态的推荐信息");
						 //以内存集合为准判断是否有改变的推荐信息
						 cleanRecommends(recoList,recommends);
						 //将新增的推荐信息存入内存集合中
						 if(null!=newListMap&&newListMap.size()>0){
							 log.debug("新增的推荐信息存入内存集合中");
							 recommends.addAll(newListMap);
						 }
					 }
					 log.debug("判断推荐信息中是否有状态为'申请'持续五天或以上没有改变的");
					 //判断推荐信息中是否有持续5天以上没有修改的
					 Map<String,Object> map=null;
					 for (int a= 0; a < recommends.size(); a++) {
						 map = recommends.get(a);
					 	 String key = map.keySet().toString();
					 	 key=key.substring(1,key.length()-1);
						 String dayStr=String.valueOf(map.get(key));
						 long d=0;
						 try {
							 //计算内存集合中的推荐信息与当前时间的差
							 long c=df.parse(day).getTime()-df.parse(dayStr).getTime();
							 d = c/(3600 * 24 * 1000);//天
						} catch (ParseException e) {
							 log.debug("计算内存集合中状态为'申请'的推荐信息与当前的时间差异常:"+e.getMessage());
						}
						 PisRecommendVo pisRecommendVo = recommendService.getById(key);
						  if (null != pisRecommendVo && pisRecommendVo.getBuildingId()!="") {
							  System.out.println(pisRecommendVo.getBuildingId());
							  PisProperty pisProperty =  buildingService.selectByPrimaryKey(pisRecommendVo.getBuildingId());
								 if (null != pisProperty) {
									 int passTime = Integer.parseInt(pisProperty.getPassTime());
									 //判断内存集合中是否存在7天或以上的推荐信息
									 if(d>passTime){
										 int ret = this.recommendService.updateRecommendStatusByRecommendId(String.valueOf(key));
										 if(ret>0){
											 log.debug("修改推荐信息状态为X成功,ID："+key);
											 //以内存集合为准判断是否有改变的推荐信息
											 recoList = this.recommendService.getRecommendByStatus(PisRecommend.Status.appointment.toString());
											 cleanRecommends(recoList,recommends);
										 }else{
											 log.debug("修改推荐信息状态为X失败,ID："+key);
										 }
									 }
								}
						}
					}
				}
		}else{
			 log.debug("第一次读取即时数据存入内存集合中");
			//第一次存入内存集合
			Map<String,Object> map=null;//声明内存集合map
			//循环遍历数据集合存入内存中
			 for (PisRecommend pisRecommend : recoList) {
				 map = new HashMap<>();
				 map.put(pisRecommend.getRecommendId(),day);
				 recommends.add(map);
			}
		}
	}
	
	/**
	 * 新增内存集合数据
	 * @param recoList 即时数据集合
	 * @param recommends 内存数据集合
	 * @param newListMap 新增数据集合
	 */
	public void addRecommends(List<PisRecommend> recoList,List<Map<String,Object>> recommends,List<Map<String,Object>>newListMap,String day){
		 boolean isExist=true;//是否存在
		 String key= null;//内存集合对象
		 PisRecommend pisR_01 =null;//数据集合对象
		 Map<String,Object> map = null;
		 //以数据集合为准判断是否有添加的新推荐信息
		 for(int a=0;a<=recoList.size()-1;a++){
			 for (int b = 0; b <recommends.size(); b++) {
				 key = recommends.get(b).keySet().toString();
				 key=key.substring(1,key.length()-1);
				 pisR_01=recoList.get(a);
				 if(pisR_01.getRecommendId().equals(key)){
					 isExist = false; 
				 }
			}
			 //组装新的推荐信息
			 if(isExist){
				 map = new HashMap<>();
				 map.put(pisR_01.getRecommendId(),day);
				 newListMap.add(map);
			 }
			 isExist = true;
		 }
	}
	/**
	 * 清除内存集合中旧数据
	 * @param recoList即时数据集合
	 * @param recommends内存数据集合
	 */
	public void cleanRecommends(List<PisRecommend> recoList,List<Map<String,Object>> recommends){
		 log.debug("清理内存集合中已经修改状态为X的推荐信息");
		 String key="";
		 boolean isExist=true;//是否存在
		 PisRecommend pisR_01 =null;//数据集合对象
		 List<Map<String,Object>> cleanListMap_=new ArrayList<>();//创建清空数据集合对象
		 if((null!=recoList&&recoList.size()>0)&&(null!=recommends&&recommends.size()>0)){
			 int len=recommends.size()-1;
			 for(int b=0;b<=len;b++){
				 key = recommends.get(b).keySet().toString();
				 key=key.substring(1,key.length()-1);
				 for(int c=0;c<recoList.size();c++){
					 pisR_01 =recoList.get(c);
					  //判断内存集合对象中是否存在于数据集合对象
					 if(key.equals(pisR_01.getRecommendId())){
						 isExist = true; 
						 break;
					 }else{
						 isExist = false; 
					 }
				 }
				 //删除不存在的数据
				 if(!isExist){
					 cleanListMap_.add(recommends.get(b));
				 }
			 }
		 }else{
			 //清空集合
			 recommends.clear();
		 }
		 recommends.removeAll(cleanListMap_);
	}
	
	public List<PisRecommend> filterList(int startDate,int endDate,List<PisRecommend> list){
		List<PisRecommend> pisRecList = new ArrayList<>();
		for (PisRecommend pisRecommend : list) {
			int day = Integer.parseInt(String.valueOf(new java.text.SimpleDateFormat("yyyyMMdd").format(pisRecommend.getRecommendDate())).trim());
			if(0 != startDate && day < startDate){
				pisRecList .add(pisRecommend);
			}
			if(0 != endDate &&  day > endDate){
				pisRecList .add(pisRecommend);
			}
		}
		 list.removeAll(pisRecList);
		 return list;
	}
	/**
	 * 根据传入的参数过滤集合信息
	 * @param building
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public List<PisRecommend> filterList(String building,int startDate,int endDate,List<PisRecommend> list){
		List<PisRecommend> pisRecList = new ArrayList<>();
		//判断集合不为空
		if(null != list && list.size()>0){
			if("" == building &&  0==startDate &&  0== endDate){
				 pisRecList = list;
				 return  pisRecList;
			}
			for (int i = 0; i < list.size(); i++) {
				PisRecommend pisRec = list.get(i);
				//判断楼盘ID不为空
				int day = Integer.parseInt(String.valueOf(new java.text.SimpleDateFormat("yyyyMMdd").format(pisRec.getRecommendDate())).trim());
				if("" != building && "null" != building){
					//判断楼盘ID
					if(building.equals(pisRec.getBuildingId())){
						//判断开始于截止时间是否都为空
						if(startDate > 0  && endDate > 0){
							if(day > startDate && day < endDate){
								pisRecList.add(pisRec);
							}
						}
						//判断开始不为空，截止时间为空
						if(startDate > 0 && endDate == 0){
							if(day > startDate){
								pisRecList.add(pisRec);
							}
						}
						//判断截止时间不为空，开始时间为空
						if(endDate > 0 && startDate == 0){
							if(day < endDate){
								pisRecList.add(pisRec);
							}
						}
						//判断开始、截止时间都为空
						if(startDate == 0 && endDate == 0){
							pisRecList.add(pisRec);
						}
					}
				}else{
					//else如果楼盘信息为空，判断时间
					if(startDate > 0  && endDate > 0){
						if(day > startDate && day < endDate){
							pisRecList.add(pisRec);
						}
					}
					//判断开始不为空，截止时间为空
					if(startDate > 0 && endDate == 0){
						if(day > startDate){
							pisRecList.add(pisRec);
						}
					}
					//判断截止时间不为空，开始时间为空
					if(endDate > 0 && startDate == 0){
						if(day < endDate){
							pisRecList.add(pisRec);
						}
					}
					//判断开始、截止时间都为空
					if(startDate == 0 && endDate == 0){
						pisRecList.add(pisRec);
					}
				}
			}
		}
		return pisRecList;
	}
}
