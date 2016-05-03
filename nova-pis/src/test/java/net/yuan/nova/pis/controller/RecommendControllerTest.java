package net.yuan.nova.pis.controller;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.methods.RequestBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.TransactionStatus;
import org.springframework.ui.ModelMap;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.yuan.nova.core.vo.JsonVo;
import net.yuan.nova.pis.entity.PisBuilding;
import net.yuan.nova.pis.entity.PisCity;
import net.yuan.nova.pis.entity.PisRecommend;
import net.yuan.nova.pis.entity.PisUser;
import net.yuan.nova.pis.service.PisBuildingService;
import net.yuan.nova.pis.service.PisCityService;
import net.yuan.nova.pis.service.PisProjectService;
import net.yuan.nova.pis.service.PisRecommendService;
import net.yuan.nova.pis.service.PisUserService;

public class RecommendControllerTest extends BaseControllerTest{
	private static Log log = LogFactory.getLog(RecommendControllerTest.class);
	@Autowired
	private RecommendController controller;
	@Autowired
	private PisUserService userService;
	@Autowired
	private PisCityService cityService;
	@Autowired
	private PisBuildingService buildingService;
	@Autowired
	private PisProjectService projectService;
	@Autowired
	private PisRecommendService recommendService;
	@Override
	Object getController() {
		return this.controller;
	}
	
	@Test
	public void addRecommend(){
		PisUser refreeUser = this.userService.findUserByUserName("13913913998");
		PisCity city = this.cityService.getCityByName("南京");
		PisBuilding building = this.buildingService.getByName("牛首山楼盘");
		
		ModelMap modelMap = new ModelMap();
		PisRecommend recommend = new PisRecommend();
		recommend.setCustomerName("李四");
		recommend.setCustomerTel("13887654321");
		recommend.setCityId(city.getCityId());
		recommend.setBuildingId(building.getBuildingId());
		recommend.setAppointmentLookHouseDate(new Date());
		recommend.setRefreeId(refreeUser.getUserId());
		recommend.setRemark("controller单元测试用");
		JSONObject json = JSONObject.fromObject(recommend);
		String resp = mockPost("/api/addRecommend", json.toString());
		Assert.assertTrue(JSONObject.fromObject(resp).getBoolean("success"));
		
		this.getWaitingPresentAll();
		this.customerPresent();
		this.getWaitingConfirm();
		this.recommendConfirm();
		//下面这个应该会保存，找不到数据了
		this.recommendConfirm();
	}
	
	@Test
	public void getWaitingPresentAll() {
		PisUser refreeUser = this.userService.findUserByUserName("13913913998");
		String resp = this.mockGet("/api/recommends/waitingPresent/" + refreeUser.getUserId(), null);
		System.out.println("waitingPresent:" + resp);
	}
	@Test
	public void getWaitingPresent() throws UnsupportedEncodingException, Exception{
		PisUser refreeUser = this.userService.findUserByUserName("13913913998");
		PisBuilding building = this.buildingService.getByName("牛首山楼盘");
		String resp = this.mockGet("/api/recommends/waitingPresent/" + refreeUser.getUserId() + "/" + building.getBuildingId(), null);
		System.out.println("waitingPresent:" + resp);
	}
	@Test
	public void customerPresent(){
		PisUser refreeUser = this.userService.findUserByUserName("13913913998");
		PisBuilding building = this.buildingService.getByName("牛首山楼盘");
		List<PisRecommend> list = this.recommendService.getWaitingPresent(refreeUser.getUserId(), building.getBuildingId());
		Assert.assertFalse(list.isEmpty());
		String recommendId = list.get(0).getRecommendId();
		String uri = "/api/recommends/customerPresent/" + recommendId + "/" + refreeUser.getUserId();
		String resp = this.mockPut(uri, null);
		System.out.println("customerPresent:" + resp);
	}
	@Test
	public void getWaitingConfirm(){
		PisUser confirmUser = this.userService.findUserByUserName("牛首山报备专员");
		String uri = "/api/recommends/waitingConfirm/" + confirmUser.getUserId();
		String resp = this.mockGet(uri, null);
		System.out.println("getWaitingConfirm:" + resp);
	}
	@Test
	public void recommendConfirm(){
		PisUser confirmUser = this.userService.findUserByUserName("牛首山报备专员");
		PisBuilding building = this.buildingService.getByName("牛首山楼盘");
		List<PisRecommend> list = this.recommendService.getWaitingConfirm(confirmUser.getUserId());
		Assert.assertFalse(list.isEmpty());
		String recommendId = list.get(0).getRecommendId();
		String uri = "/api/recommends/recommendConfirm/" + recommendId + "/" + confirmUser.getUserId();
		String resp = this.mockPut(uri, null);
		System.out.println("recommendConfirm:" + resp);
	}
	@Test
	public void getMyPresent() {
		PisUser refreeUser = this.userService.findUserByUserName("13913913998");
		String resp = this.mockGet("/api/recommends/presented/" + refreeUser.getUserId(), null);
		System.out.println("getMyPresent:" + resp);
	}
	@Test
	public void getMyConfirm() {
		PisUser confirmUser = this.userService.findUserByUserName("13815892591");
		String resp = this.mockGet("/api/recommends/confirmed/" + confirmUser.getUserId(), null);
		System.out.println("getMyConfirm:" + resp);
	}
	@Test
	public void downAsExcell() {
		String resp = this.mockGet("/api/excell/nanjing", null);
		System.out.println("getMyConfirm:" + resp);
	}
}
