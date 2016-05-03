package net.yuan.nova.pis.service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import net.sf.json.JSONObject;
import net.yuan.nova.pis.entity.PisBuilding;
import net.yuan.nova.pis.entity.PisCity;
import net.yuan.nova.pis.entity.PisRecommend;
import net.yuan.nova.pis.entity.PisUser;
import net.yuan.nova.pis.entity.vo.PisRecommendVo;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 报备业务测试 
 * 1、经纪人添加报备 insert ok 
 * 2、经纪人查询在某个楼盘的报备信息 getWaitingPresent ok
 * 3、经纪人点击客户到现场，customerPresent ok
 * 4、报备专员查询某个楼盘的报备信息（客户已到现场，还未确认的）getWaitingConfirm ok 
 * 5、报备员点击确认报备 recommendConfirm ok
 * 
 * 中间还涉及到，报备员关联到某个楼盘（项目） 1、城市管理 PisCityService ok 2、楼盘管理 PisBuildingService ok
 * 3、项目管理（楼盘和报备专员关联），PisProjectService ok
 * 
 * @author leasonlive
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext.xml")
public class PisRecommendServiceTest extends AbstractJUnit4SpringContextTests {
	@Autowired
	private PisRecommendService recommendService;
	@Autowired
	private PisUserService userService;
	@Autowired
	private PisCityService cityService;
	@Autowired
	private PisBuildingService buildingService;
	@Autowired
	private PisProjectService projectService;

	@Test
	public void insert() {
		// 测试插入一个报备信息
		PisUser refreeUser = this.userService.findUserByUserName("13913913998");
		PisCity city = this.cityService.getCityByName("南京");
		PisBuilding building = this.buildingService.getByName("牛首山楼盘");
		Assert.assertEquals(city.getCityId(), building.getCityId());
		PisRecommend recommend = new PisRecommend();
		recommend.setRecommendId(UUID.randomUUID().toString());
		recommend.setCustomerName("张三");
		recommend.setCustomerTel("13815892591");
		recommend.setCityId(city.getCityId());
		recommend.setBuildingId(building.getBuildingId());
		recommend.setAppointmentLookHouseDate(new Date());
		recommend.setRefreeId(refreeUser.getUserId());
		recommend.setRemark("单元测试用");
		this.recommendService.insert(recommend);
	}

	@Test
	public void getById() {
		// 测试根据报备id查找报备信息
		PisRecommendVo recommend = this.recommendService.getById("bcc667fe-7e1a-4913-8725-8f0eab87ccd6");
//		Assert.assertEquals(PisRecommend.Status.appointment, recommend.getStatus());
		System.out.println(JSONObject.fromObject(recommend));
	}

	@Test
	public void getWaitingPresent() {
		// 测试查找等待客户到现场的报备信息
		PisUser refreeUser = this.userService.findUserByUserName("13913913998");
		PisBuilding building = this.buildingService.getByName("牛首山楼盘");
		List<PisRecommend> list = this.recommendService.getWaitingPresent(refreeUser.getUserId());
		Assert.assertTrue(list.size() > 0);
		List<PisRecommend> listWithBuilding = this.recommendService.getWaitingPresent(refreeUser.getUserId(),
				building.getBuildingId());
		Assert.assertTrue(listWithBuilding.size() > 0);
	}

	@Test
	public void customerPresent() {
		// 测试客户到现场， 经纪人点击到场按钮
		PisRecommend recommend = this.recommendService.getById("bcc667fe-7e1a-4913-8725-8f0eab87ccd6");
		PisUser refreeUser = this.userService.findUserByUserName("13913913998");
		this.recommendService.customerPresent(recommend.getRecommendId(), refreeUser.getUserId());
		PisRecommend recommendPresent = this.recommendService.getById("bcc667fe-7e1a-4913-8725-8f0eab87ccd6");
		Assert.assertEquals(PisRecommend.Status.present, recommendPresent.getStatus());
	}

	@Test
	public void getWaitingConfirm() {
		// 执行这个方法之前，需要先做报备员信息的初始化
		PisUser confirmUser = this.userService.findUserByUserName("牛首山报备专员");
		List<PisRecommend> list = this.recommendService.getWaitingConfirm(confirmUser.getUserId());
		Assert.assertTrue(list.size() > 0);
	}

	@Test
	public void addConfirmUserInfo() {
		// 添加报备专员账号
		PisUser confirmUser = new PisUser();
		confirmUser.setUserName("牛首山报备专员");
		confirmUser.setNick("牛报员");
		confirmUser.setPassword("123456");

		confirmUser.setEmail("test@test.com");
		confirmUser.setTel("13101234567");
		confirmUser.setType("U");
		this.userService.insertUser(confirmUser);
	}

	@Test
	public void addConfirmUserToBuilding() {
		PisBuilding building = this.buildingService.getByName("牛首山楼盘");
		// 把专员关联到楼盘上
		PisUser confirmUser = this.userService.findUserByUserName("牛首山报备专员");
		this.projectService.insert(confirmUser.getUserId(), building.getBuildingId());
	}

	@Test
	public void recommendConfirm() {
		PisRecommend recommend = this.recommendService.getById("bcc667fe-7e1a-4913-8725-8f0eab87ccd6");
		PisUser confirmUser = this.userService.findUserByUserName("牛首山报备专员");
		this.recommendService.recommendConfirm(recommend.getRecommendId(), confirmUser.getUserId());
		PisRecommend recommendConfirmed = this.recommendService.getById("bcc667fe-7e1a-4913-8725-8f0eab87ccd6");
		Assert.assertEquals(PisRecommend.Status.confirm, recommendConfirmed.getStatus());

	}
}
