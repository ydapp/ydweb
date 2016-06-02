package net.yuan.nova.pis.controller;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.yuan.nova.commons.SystemConstant;
import net.yuan.nova.core.entity.Attachment;
import net.yuan.nova.core.service.AttachmentBlobService;
import net.yuan.nova.core.service.AttachmentService;
import net.yuan.nova.core.shiro.CurrentUserUtil;
import net.yuan.nova.core.shiro.PubUserAuthenticationToken;
import net.yuan.nova.core.shiro.vo.User;
import net.yuan.nova.core.shiro.vo.UserModel;
import net.yuan.nova.core.vo.JsonVo;
import net.yuan.nova.pis.business.UserModelBusinessImpl;
import net.yuan.nova.pis.entity.PisBrokingFirm;
import net.yuan.nova.pis.entity.PisBuilding;
import net.yuan.nova.pis.entity.PisUser;
import net.yuan.nova.pis.entity.PisUserExtend;
import net.yuan.nova.pis.entity.PisUserGroup;
import net.yuan.nova.pis.entity.PisUserGroupShipKey;
import net.yuan.nova.pis.entity.PisUserInfo;
import net.yuan.nova.pis.entity.vo.UserInfoVo;
import net.yuan.nova.pis.pagination.DataGridHepler;
import net.yuan.nova.pis.pagination.PageParam;
import net.yuan.nova.pis.service.PisBrokingFirmService;
import net.yuan.nova.pis.service.PisBuildingService;
import net.yuan.nova.pis.service.PisUserExtendService;
import net.yuan.nova.pis.service.PisUserInfoService;
import net.yuan.nova.pis.service.PisUserService;
import net.yuan.nova.pis.service.UserGroupService;
import net.yuan.nova.pis.service.UserGroupShipKeyService;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.servlet.Cookie;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.github.pagehelper.PageInfo;

@Controller
public class PisUserController {

	protected final Logger log = LoggerFactory.getLogger(PisUserController.class);

	@Autowired
	private PisUserService pisUserService;
	@Autowired
	private PisUserInfoService pisUserInfoService;
	@Autowired
	private UserGroupShipKeyService keyService;
	@Autowired
    private AttachmentService attachmentService;
	@Autowired
	private AttachmentBlobService attachmentBlobService;
	@Autowired
	private UserGroupService groupService;
	@Autowired
	private PisUserExtendService userExtendService;
	@Autowired
	private PisBuildingService buildingService;
	@Autowired
	private PisBrokingFirmService brokingFirmService;
	@Autowired
	private UserModelBusinessImpl userModelBusiness;
	/**
	 * 获得当前登录用户信息
	 * 
	 * @return
	 */
	@RequestMapping(value = "/api/currentUser")
	public JsonVo<User> currentUser() {
		JsonVo<User> json = new JsonVo<User>();
		User user = CurrentUserUtil.getShiroUser();
		if (user == null) {
			json.setSuccess(false);
			json.setMessage("当前未登录或登录超时");
		} else {
			json.setSuccess(true);
			json.setResult(user);
		}
		return json;
	}
	@RequestMapping(value = "/api/currentUserModel")
	public JsonVo<UserModel> currentUserModel() {
		JsonVo<UserModel> json = new JsonVo<UserModel>();
		User user = CurrentUserUtil.getShiroUser();
		if (user == null) {
			json.setSuccess(false);
			json.setMessage("当前未登录或登录超时");
		} else {
			json.setSuccess(true);
			UserModel userModel = this.userModelBusiness.getUserModel(user.getUserId());
			json.setResult(userModel);
		}
		return json;
	}
	

	@RequestMapping(value = "/api/addUser")
	public ModelMap addUser(HttpServletRequest request, ModelMap modelMap, PisUser pisUser) {
		@SuppressWarnings("rawtypes")
		JsonVo jsonVo = new JsonVo();
		// 手机号码
		if (StringUtils.isBlank(pisUser.getTel())) {
			jsonVo.putError("TEL", "手机号码不能为空");
		} else {
			pisUser.setUserName(pisUser.getTel());
			PisUser user = pisUserService.findUserByTel(pisUser.getTel());
			if (user != null) {
				jsonVo.putError("TEL", "手机号码已被使用");
			}
		}
		// 邮箱
		if (StringUtils.isBlank(pisUser.getEmail())) {
			jsonVo.putError("email", "邮箱不能为空");
		} else {
			PisUser user = pisUserService.findUserByEmail(pisUser.getTel());
			if (user != null) {
				jsonVo.putError("TEL", "手机号码已被使用");
			}
		}
		// 密码
		if (StringUtils.isBlank(pisUser.getPassword())) {
			jsonVo.putError("password", "密码不能为空");
		} else {

		}
		if (jsonVo.validate()) {// 验证通过后

			if (StringUtils.isBlank(pisUser.getPromoCodeP()) && StringUtils.isNotBlank(pisUser.getPromoCode())) {
				// 设置推荐人
				pisUser.setPromoCodeP(pisUser.getPromoCode());
			}
			pisUser.setUserName(pisUser.getTel());
			pisUser.setType("U");
			int success = pisUserService.insertUser(pisUser);
			if (success == 1) {
				jsonVo.setMessage("用户注册成功");
			} else {
				jsonVo.setMessage("用户注册成功");
			}
			jsonVo.setSuccess(success == 1);
		}
		modelMap.remove("pisUser");
		modelMap.addAttribute("result", jsonVo);
		return modelMap;

	}

	/**
	 * 获得用户所在组
	 * 
	 * @param userId
	 * @param request
	 * @param modelMap
	 * @return
	 */
	@RequestMapping(value = "/api/user/group", method = RequestMethod.GET)
	@ResponseBody
	public Object getUserGroup(HttpServletRequest request, ModelMap modelMap) {
		JsonVo<PisUserGroup> json = new JsonVo<PisUserGroup>();
		json.validate();
		User user = CurrentUserUtil.getShiroUser();
		PisUserGroup pisUserGroup = null;
		try {
			if (user != null) {
				pisUserGroup = pisUserService.getPisUserGroup(user.getUserId());
				if (pisUserGroup != null) {
					json.setResult(pisUserGroup);
				}
			}
		} catch (Exception e) {
			log.error("获得用户组信息失败", e);
		}
		if (pisUserGroup == null) {
			json.setSuccess(false);
			json.setMessage("获得用户组信息失败");
		} else {
			json.setSuccess(true);
			json.setResult(pisUserGroup);
		}
		return json;
	}

	/**
	 * 用户登陆
	 * 
	 * @param loginName
	 * @param password
	 * @param request
	 * @param modelMap
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/api/login")
	public Object login(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) {
		log.debug("PubUserController is login start");
		JsonVo<UserInfoVo> json = new JsonVo<UserInfoVo>();
		String userName = request.getParameter("userName");
		String password = request.getParameter("password");
		String deviceId = request.getParameter("deviceId");
		PisUser pisUser = pisUserService.findUserByUserName(userName);
		if (pisUser == null) {
			log.debug("用户名不存在。。。。。");
			json.setSuccess(false);
			json.setMessage("用户名不存在！");
			modelMap.addAttribute("json", json);
			return modelMap;
		}

		if (StringUtils.isBlank(userName) || StringUtils.isBlank(password)) {
			json.putError("loginName", "用户名密码不能为空");
		}
		if (StringUtils.isBlank(deviceId)) {
			json.putError("deviceId", "设备标识不能为空");
		}
		if (json.validate()) {
			PubUserAuthenticationToken token = new PubUserAuthenticationToken(userName, password, true, deviceId);
			// 执行登录
			doLogin(token, request, response, json);
		}
		modelMap.addAttribute("result", json);
		return modelMap;
	}

	private void doLogin(AuthenticationToken token, HttpServletRequest request, HttpServletResponse response,
			JsonVo<UserInfoVo> json) {
		String deviceId = request.getParameter("deviceId");
		// String clientUserId = request.getParameter("clientUserId");
		// String channelId = request.getParameter("channelId");
		// String platType = request.getParameter("platType");
		// String version = request.getParameter("version");
		Subject currentUser = SecurityUtils.getSubject();
		try {
			currentUser.login(token);
		} catch (AuthenticationException e) {
			log.warn(e.getMessage());
		}
		// 判断登录是否成功
		if (currentUser.isAuthenticated()) {
			// 用户登录成功，将imei数据写如cookie
			SimpleCookie cookie = new SimpleCookie();
			cookie.setName("imei");
			cookie.setValue(deviceId);
			cookie.setHttpOnly(true);
			cookie.setMaxAge(Cookie.ONE_YEAR);
			cookie.saveTo(request, response);
			// 给出相应返回值，当前用户信息
			UserInfoVo userInfoVo = new UserInfoVo();
			Session session = currentUser.getSession();
			User user = (User) session.getAttribute(SystemConstant.SESSION_USER);
			userInfoVo.setUser(user);
			PisUserGroup pisUserGroup = pisUserService.getPisUserGroup(user.getUserId());
			userInfoVo.setUserGroup(pisUserGroup);
			userInfoVo.setUserModel(this.userModelBusiness.getUserModel(user.getUserId()));
			json.setResult(userInfoVo);
		} else {
			json.setSuccess(false);
			json.setMessage("用户名密码不匹配！");
		}
	}
	/**
	 * 查找所有的用户，一般用户通讯录和用于信息维护界面
	 * @param request
	 * @param modelMap
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@ResponseBody
	@RequestMapping(value = "/api/userInfos", method=RequestMethod.GET)
	public ModelMap getUserInfos(HttpServletRequest request, ModelMap modelMap, HttpServletResponse response) {
		PageParam param = DataGridHepler.parseRequest(request);
		List<PisUser> users = this.pisUserService.getCustomers(param.getPage(), param.getPageSize());
		List<UserModel> userInfoList = new ArrayList<>();
		List<PisUser> pisUserList = this.sortUserByUserName(users);
		for (PisUser user : pisUserList) {
			UserModel vo = this.userModelBusiness.getUserModel(user.getUserId());
			userInfoList.add(vo);
		}
		return  DataGridHepler.addDataGrid(userInfoList, new PageInfo(users).getTotal(), modelMap); 
	}
	/**
	 * 添加一个用户，同时添加用户信息和组
	 * @param request
	 * @param modelMap
	 * @return
	 */
	@RequestMapping(value = "/api/userInfo",method=RequestMethod.POST)
	public JsonVo<UserModel> addUserInfo(@RequestBody UserModel userModel, ModelMap modelMap) {
		JsonVo<UserModel> json = new JsonVo<UserModel>();
		if (StringUtils.equals(PisUserGroup.TYPE.appAdmin.name(), userModel.getGroupType())){
			UserModel appManager = this.getAppAdmin();
			if (appManager != null){
					json.setSuccess(false);
					json.setMessage("app管理员已经存在:" + appManager.getNick() + "(" + appManager.getTel() + ")");
					return json;
			}
		}
		PisUser user = this.pisUserService.findUserByTel(userModel.getTel());
		if (user != null){
			json.setSuccess(false);
			json.setMessage("此电话号码已经存在:" + user.getNick() + "(" + user.getTel() + ")");
			return json;
		}
		
		log.debug("添加用户:" + userModel.getNick() + "(" + userModel.getTel() + ")");
		PisUser pisUser = new PisUser();
		pisUser.setNick(userModel.getNick());
		pisUser.setUserName(userModel.getTel());
		pisUser.setType("F");
		pisUser.setTel(userModel.getTel());
		pisUser.setPassword("123456");
		this.pisUserService.insertUser(pisUser);
		log.debug("添加用户和组的关系:" + userModel.getGroupType());
		String groupType = userModel.getGroupType();
		PisUserGroup userGroup = this.groupService.getByType(groupType);
		PisUserGroupShipKey key = new PisUserGroupShipKey();
		key.setGroupId(userGroup.getGroupId());
		key.setUserId(pisUser.getUserId());
		this.keyService.insert(key);
		log.debug("添加用户扩展信息");
		if (StringUtils.equals(PisUserGroup.TYPE.brokingFirm.name(), userModel.getGroupType())){
			log.debug("添加经纪公司");
			String brokingFirmId = this.brokingFirmService.add(userModel.getBrokingFirm());
			log.debug("关联经纪公司");
			PisUserExtend userExtend = new PisUserExtend();
			userExtend.setBrokingFirmId(brokingFirmId);
			userExtend.setUserId(pisUser.getUserId());
			this.userExtendService.insert(userExtend);
		} else if (StringUtils.equals(PisUserGroup.TYPE.salesman.name(), userModel.getGroupType())){
			log.debug("关联经纪公司");
			String brokingFirmId = this.brokingFirmService.findByName(userModel.getBrokingFirm()).getBrokingFirmId();
			PisUserExtend userExtend = new PisUserExtend();
			userExtend.setBrokingFirmId(brokingFirmId);
			userExtend.setUserId(pisUser.getUserId());
			this.userExtendService.insert(userExtend);
			
		}else if (StringUtils.equals(PisUserGroup.TYPE.commissioner.name(), userModel.getGroupType())){
			log.debug("关联楼盘");
			String buildingId = userModel.getBuilding();
			PisUserExtend userExtend = new PisUserExtend();
			userExtend.setBuildingId(buildingId);
			userExtend.setUserId(pisUser.getUserId());
			this.userExtendService.insert(userExtend);
		}else if(StringUtils.equals(PisUserGroup.TYPE.channelManager.name(), userModel.getGroupType())){
			log.debug("关联楼盘");
			String buildingId = userModel.getBuilding();
			PisUserExtend userExtend = new PisUserExtend();
			userExtend.setBuildingId(buildingId);
			userExtend.setUserId(pisUser.getUserId());
			this.userExtendService.insert(userExtend);
		}
		json.setSuccess(true);
		json.setMessage("添加成功");
		return json;
	}
	@RequestMapping(value="/api/appManager", method=RequestMethod.GET)
	public JsonVo<UserModel> getAppManager(ModelMap modelMap){
		UserModel vo = getAppAdmin();
		JsonVo<UserModel> json = new JsonVo<UserModel>();
		if (vo == null) {
			json.setSuccess(false);
			json.setMessage("未找到app管理员");
		} else {
			json.setSuccess(true);
			json.setResult(vo);
		}
		return json;
	}

	private UserModel getAppAdmin() {
		PisUserGroup userGroup = this.groupService.getByType(PisUserGroup.TYPE.appAdmin.name());
		List<PisUserGroupShipKey> keys = this.keyService.getByGroupId(userGroup.getGroupId());
		UserModel vo = null;
		if (keys.size() > 0){
			PisUser user = this.pisUserService.findUserById(keys.get(0).getUserId());
			vo = new UserModel();
			vo.setTel(user.getTel());
			vo.setNick(user.getNick());
			vo.setGroupType(PisUserGroup.TYPE.appAdmin.name());
		}
		return vo;
	}
	/**
	 * 验证密码是否正确
	 * @return true:原密码正确，false:原密码错误
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping(value="/api/staff/checkPassword")
	public JsonVo  checkPassword(HttpServletRequest request, HttpServletResponse response){
		JsonVo json = new JsonVo();
		//获取原始密码
		String oldPwd = StringUtils.trimToEmpty(request.getParameter("oldPwd"));
		//获取登录账号
		String loginName = StringUtils.trimToEmpty(request.getParameter("loginName"));
		//执行密码修改
		boolean flag = this.pisUserService.checkPassword(oldPwd,loginName);
		json.setSuccess(flag);
		return json;
	}
	/**
	 * 执行密码修改操作
	 * @return true:操作成功，false:操作失败
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping(value="/api/staff/changePassword")
	public JsonVo changePassword(HttpServletRequest request, HttpServletResponse response){
		JsonVo json = new JsonVo();
		//获取新密码
		String newPwd = StringUtils.trimToEmpty(request.getParameter("pwd1"));
		//获取用户ID
		String loginName = StringUtils.trimToEmpty(request.getParameter("loginName"));
		//组装参数
		PisUser pisUser = new PisUser();
		pisUser.setUserName(loginName);
		pisUser.setPassword(newPwd);
		//执行密码修改操作
		boolean flag = this.pisUserService.changePassword(pisUser);
		//设置返回值
		json.setSuccess(flag);
		return json;
	}
	/**
	 * 执行用户删除操作
	 * @return true:操作成功，false:操作失败
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping(value="/api/removeUser")
	public JsonVo removeUser(HttpServletRequest request, HttpServletResponse response){
		JsonVo json = new JsonVo();
		//获取用户ID
		String userId = StringUtils.trimToEmpty(request.getParameter("userId"));
		boolean flag = this.pisUserService.removeUser(userId);
		//设置返回值
		json.setSuccess(flag);
		return json;
	}
	/**
	 * 通过用户ID获取用户信息
	 * @return PisUser 用户实体类
	 */
	@RequestMapping(value="/api/getUserByUserId")
	public UserModel getUserByUserId(HttpServletRequest request, HttpServletResponse response){
		//接收用户ID
		String userId = StringUtils.trimToEmpty(request.getParameter("userId"));
		//通过用户ID获取用户信息
		PisUser pisUser =  this.pisUserService.findUserById(userId);
		//通过用户ID获取组
		PisUserGroup pisUserGroup =this.pisUserService.getPisUserGroup(userId);
		//通过用户ID获取扩展信息
		PisUserExtend pisUserExtend = this.userExtendService.selectByUserId(userId);
		//获取经纪公司
		PisBrokingFirm pisBrokingFirm = this.brokingFirmService.findById(null!=pisUserExtend?pisUserExtend.getBrokingFirmId():"");
		//获取楼盘
		PisBuilding pisBuilding = null;
		if(null!=pisUserExtend){
			pisBuilding = this.buildingService.getById(pisUserExtend.getBuildingId());
		}
		//组装返回数据
		UserModel userModel=null;
		userModel = new UserModel();
		userModel.setTel(null!=pisUser?pisUser.getTel():"");
		userModel.setNick(null!=pisUser?pisUser.getNick():"");
		userModel.setUserId(userId);
		userModel.setGroupId(null!=pisUserGroup?pisUserGroup.getGroupId():"");
		userModel.setGroupType(null!=pisUserGroup?pisUserGroup.getType():"");
		userModel.setGroupTypeTitle(null!=pisUserGroup?pisUserGroup.getTypeTitle():"");
		userModel.setBrokingFirm(null!=pisBrokingFirm?pisBrokingFirm.getBrokingFirmName():"");
		userModel.setBuilding(null!=pisBuilding?pisBuilding.getBuildingId():"");
		return userModel;
	}
	/**
	 * 执行修改用户操作
	 * @return
	 */
	@RequestMapping(value="/api/updateUserByUserId")
	public JsonVo<UserModel> updateUserByUserId(@RequestBody UserModel userModel, ModelMap modelMap){
		JsonVo<UserModel> json = new JsonVo<UserModel>();
		log.debug("修改用户:" + userModel.getNick() + "(" + userModel.getTel() + ")");
		//组装参数
		PisUser pisUser = new PisUser();
		pisUser.setTel(userModel.getTel());
		pisUser.setUserId(userModel.getUserId());
		PisUser user = this.pisUserService.selectUserByTelFaultIs(pisUser);
		if (user != null){
			json.setSuccess(false);
			json.setMessage("此电话号码已经存在:" + user.getNick() + "(" + user.getTel() + ")");
			return json;
		}
		//组装参数
		pisUser = new PisUser();
		pisUser.setTel(userModel.getTel());
		pisUser.setNick(userModel.getNick());
		pisUser.setUserName(userModel.getTel());
		pisUser.setUserId(userModel.getUserId());
		this.pisUserService.updateUser(pisUser);
		log.debug("修改用户和组的关系:" + userModel.getGroupType());
		//删除原有用户与组关联关系
		PisUserGroupShipKey key_01 = new PisUserGroupShipKey();
		key_01.setGroupId(userModel.getGroupId());
		key_01.setUserId(userModel.getUserId());
		this.keyService.delete(key_01);
		//新增用户与组关联
		String groupType = userModel.getGroupType();
		PisUserGroup userGroup = this.groupService.getByType(groupType);
		PisUserGroupShipKey key_02 = new PisUserGroupShipKey();
		key_02.setGroupId(userGroup.getGroupId());
		key_02.setUserId(userModel.getUserId());
		this.keyService.insert(key_02);
		log.debug("修改用户扩展信息");
		if (StringUtils.equals(PisUserGroup.TYPE.brokingFirm.name(),groupType)){
			log.debug("添加关联经纪公司");
			String brokingFirmId ="";
			PisBrokingFirm pisBrokingFirm=this.brokingFirmService.findByName(userModel.getBrokingFirm());
			if(null==pisBrokingFirm){
				brokingFirmId = this.brokingFirmService.add(userModel.getBrokingFirm());
			}else{
				brokingFirmId=pisBrokingFirm.getBrokingFirmId();
			}
			log.debug("关联经纪公司");
			PisUserExtend userExtend = new PisUserExtend();
			userExtend.setBrokingFirmId(brokingFirmId);
			userExtend.setUserId(userModel.getUserId());
			this.userExtendService.updateByUserId(userExtend);
		}else if(StringUtils.equals(PisUserGroup.TYPE.salesman.name(), userModel.getGroupType())){
			log.debug("关联经纪公司");
			PisBrokingFirm pisBrokingFirm=this.brokingFirmService.findByName(userModel.getBrokingFirm());
			String brokingFirmId="";
			if(null==pisBrokingFirm){
				brokingFirmId = this.brokingFirmService.add(userModel.getBrokingFirm());
			}else{
				brokingFirmId=pisBrokingFirm.getBrokingFirmId();
			}
			PisUserExtend userExtend = new PisUserExtend();
			userExtend.setBrokingFirmId(brokingFirmId);
			userExtend.setUserId(userModel.getUserId());
			this.userExtendService.updateByUserId (userExtend);
		}else if (StringUtils.equals(PisUserGroup.TYPE.commissioner.name(), userModel.getGroupType())){
			log.debug("关联楼盘");
			PisUserExtend userExtend = new PisUserExtend();
			userExtend.setBuildingId(userModel.getBuilding());
			userExtend.setUserId(userModel.getUserId());
			this.userExtendService.updateByUserId(userExtend);
		}else if(StringUtils.equals(PisUserGroup.TYPE.channelManager.name(), userModel.getGroupType())){
			log.debug("关联楼盘");
			PisUserExtend userExtend = new PisUserExtend();
			userExtend.setBuildingId(userModel.getBuilding());
			userExtend.setUserId(userModel.getUserId());
			this.userExtendService.updateByUserId(userExtend);
		}
		json.setSuccess(true);
		json.setMessage("修改成功");
		return json;
	}
	
	/**
	 * 通过经纪公司ID获取公司下的通讯录中的用户
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/api/getUserByBrokingFirm", method=RequestMethod.GET)
	public ModelMap getUserByBrokingFirm(HttpServletRequest request, HttpServletResponse response,ModelMap modelMap){
		PageParam param = DataGridHepler.parseRequest(request);
		//获取经济公司主键id
		String brokingFirmId = request.getParameter("brokingFirmId");
		//通过经济公司主键获取该公司下的用户ID
		List<PisUserExtend>userExtendList = this.userExtendService.selectByBrokingfirmId(param.getPage(), param.getPageSize(),brokingFirmId);
		//创建存储用户集合
		List<PisUser> pisUserList = new ArrayList<>();
		//判断集合不为空
		if(null!=userExtendList&&userExtendList.size()>0){
			//遍历循环获取用户ID
			for (PisUserExtend pisUserExtend : userExtendList) {
				//组装集合
				pisUserList.add(this.pisUserService.findUserById(pisUserExtend.getUserId()));
			}
		}
		return DataGridHepler.addDataGrid(pisUserList, new PageInfo(userExtendList).getTotal(),modelMap);
	}
	
	/**
	 * 根据用户ID获取用户信息
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/api/getUserInfoByUserId", method=RequestMethod.GET)
	public UserModel getUserInfoByUserId(HttpServletRequest request, HttpServletResponse response){
		//获取用户ID
		String userId = request.getParameter("userId");
		//根据用户ID获取用户信息
		PisUser pisUser = this.pisUserService.findUserById(userId);
		//根据用户ID获取用户基本信息
		PisUserInfo pisUserInfo = null;
		this.pisUserInfoService.findUserInfoById(userId);
		UserModel  vo = new UserModel();
		vo.setTel(null!=pisUser?pisUser.getTel():"");
		vo.setUserIcon(null!=pisUser?pisUser.getUserIcon():"");
		vo.setUserId(userId);
		vo.setIdNumber(null!=pisUserInfo?pisUserInfo.getIdNumber():"");
		vo.setName(null!=pisUserInfo?pisUserInfo.getName():"");
		vo.setSex(null!=pisUserInfo?pisUserInfo.getSex():"");
		vo.setAddress(null!=pisUserInfo?pisUserInfo.getAddress():"");
		vo.setFrontPhoto(null!=pisUserInfo?pisUserInfo.getFrontPhoto():"");
		return vo;
	}
	
	/**
	 * 维护用户以及用户基本信息
	 * @param userModel
	 * @param modelMap
	 * @return
	 */
	@RequestMapping("/api/updateUserAndUserInfo")
	public JsonVo<UserModel> updateUserAndUserInfo(HttpServletRequest request){
		JsonVo<UserModel> json = new JsonVo<UserModel>();
		log.debug("获取用户个人头像");
		MultipartFile file_01 = null;
		MultipartFile file_02 = null;
		MultipartHttpServletRequest multipartRequest = null;
		String userIcon=UUID.randomUUID().toString();
		String frontPhoto=UUID.randomUUID().toString();
		log.debug("转换request为附件方式");
		try {
			multipartRequest = (MultipartHttpServletRequest) request;
			file_01 = multipartRequest.getFile("userIcon");
			file_02 = multipartRequest.getFile("cardPhoto");
		} catch (ClassCastException cce) {
			log.error("没有上传图片", cce);
		}
		//组装参数
		PisUser pisUser = new PisUser();
		pisUser.setUserId(multipartRequest.getParameter("userId"));
		pisUser.setTel(multipartRequest.getParameter("tel"));
		pisUser.setUserIcon(userIcon);
		PisUserInfo pisUserInfo  = new PisUserInfo();
		pisUserInfo.setFrontPhoto(frontPhoto);
		pisUserInfo.setName(multipartRequest.getParameter("name"));
		pisUserInfo.setIdNumber(multipartRequest.getParameter("cardId"));
		pisUserInfo.setSex(multipartRequest.getParameter("sex"));
		pisUserInfo.setAddress(multipartRequest.getParameter("address"));
		pisUserInfo.setUserId(multipartRequest.getParameter("userId"));
		boolean flag = this.pisUserService.updateUserAndUserInfo(pisUserInfo, pisUser);
		if(flag){
			String oldUserIcon = multipartRequest.getParameter("userIcon");
			String oldFrontPhoto = multipartRequest.getParameter("frontPhoto");
			log.debug("保存用户个人头像数据");
			if(null!=file_01&&!StringUtils.isEmpty(file_01.getOriginalFilename())){
			   this.attachmentService.deleteAttachmentsByKindId(oldUserIcon, Attachment.TableName.PIS_USER);
			   this.attachmentBlobService.cleanAttachmentBlob();
			   this.attachmentService.addUploadFile(file_01, file_01.getOriginalFilename(), userIcon,Attachment.TableName.PIS_USER,Attachment.State.A);
			}
			log.debug("保存用户身份证正面照片数据");
			if(null!=file_02&&!StringUtils.isEmpty(file_02.getOriginalFilename())){
				  this.attachmentService.deleteAttachmentsByKindId(oldFrontPhoto, Attachment.TableName.PIS_USER_INFO);
				  this.attachmentBlobService.cleanAttachmentBlob();
				 this.attachmentService.addUploadFile(file_02, file_02.getOriginalFilename(), frontPhoto,Attachment.TableName.PIS_USER_INFO,Attachment.State.A);
			}
		}
		json.setSuccess(flag);
		return json;
	}
	/**
	 * 判断系统中是否存在app管理员
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/api/vaildAppManager",method=RequestMethod.GET)
	public  JsonVo vaildAppManager(){
		JsonVo json = new JsonVo();
		UserModel appManager = this.getAppAdmin();
		if (appManager != null){
			json.setSuccess(false);
		}else{
			json.setSuccess(true);
		}
		return json;
	} 
	
	/**
	 * 判断登录用户是否是：业务员、案场专员、渠道经理
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/api/loginUserType",method=RequestMethod.GET)
	public JsonVo loginUserType(){
		JsonVo json = new JsonVo();
		Subject currentUser = SecurityUtils.getSubject();
		// 判断登录是否成功
		if (currentUser.isAuthenticated()) {
			Session session = currentUser.getSession();
			User user = (User) session.getAttribute(SystemConstant.SESSION_USER);
			PisUserGroup pisUserGroup = pisUserService.getPisUserGroup(user.getUserId());
			String type = null!=pisUserGroup?pisUserGroup.getType():"";
			String  types=PisUserGroup.TYPE.salesman+","+PisUserGroup.TYPE.commissioner+","+PisUserGroup.TYPE.channelManager;
			if(types.indexOf(type)!=-1){
				json.setSuccess(false);
			}else{
				json.setSuccess(true);
			}
		}
		return json;
	}
	/**
	 * 通过用户名进行排序
	 * @return
	 */
	public List<PisUser> sortUserByUserName(List<PisUser> userList){
		//创建返回值对象
				List<PisUser> userList_03=new ArrayList<>();
				if(null!=userList&&userList.size()>0){
					//创建案场电话集合
					List<PisUser> userList_01=new ArrayList<>();
					//创建非案场电话集合
					List<PisUser> userList_02=new ArrayList<>();
					//遍历循环判断用户是否案场专员
					for (int i = 0; i < userList.size(); i++) {
						PisUser pisUser = userList.get(i);
						 PisUserGroup group =pisUserService.getPisUserGroup(pisUser.getUserId());
						 if(null!=group&&!"".equals(group.getType())){
							 if("commissioner".equals(group.getType())){
									userList_01.add(pisUser);
								}else{
									userList_02.add(pisUser);
								}
						 }else{
								userList_02.add(pisUser);
						 }
					}
					//按照中文姓名排序案场电话集合
					Collections.sort(userList_01, new Comparator<PisUser>(){
						@Override
						public int compare(PisUser user1, PisUser user2) {
							return  Collator.getInstance(java.util.Locale.CHINA).compare(user1.getNick(),user2.getNick());
						}
					});
					//按照中文排序非案场电话集合
					Collections.sort(userList_02, new Comparator<PisUser>(){
						@Override
						public int compare(PisUser user1, PisUser user2) {
							return  Collator.getInstance(java.util.Locale.CHINA).compare(user1.getNick(),user2.getNick());
						}
					});
					//按照顺序组装返回值 
					userList_03.addAll(userList_01);
					userList_03.addAll(userList_02);
				} 
				return userList_03;
	}
}
