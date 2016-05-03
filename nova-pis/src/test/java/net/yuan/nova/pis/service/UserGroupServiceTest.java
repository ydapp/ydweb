package net.yuan.nova.pis.service;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import net.yuan.nova.pis.entity.PisUserGroup;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext.xml")
public class UserGroupServiceTest  extends AbstractJUnit4SpringContextTests {
	@Autowired
	private UserGroupService groupService;
	@Test
	public void getGroupByParentId(){
		List<PisUserGroup> list = this.groupService.getGroupByParentId(null);
		for (PisUserGroup pisUserGroup : list) {
			System.out.println("fatherId:" + pisUserGroup.getFatherId());
			System.out.println("groupId:" + pisUserGroup.getGroupId());
			System.out.println("name:" + pisUserGroup.getName());
			System.out.println("type:" + pisUserGroup.getType());
		}
	}
	@Test
	public void insertAppAdmin(){
		PisUserGroup userGroup = new PisUserGroup();
		userGroup.setFatherId(null);
		userGroup.setIntro("app管理员");
		userGroup.setName("app管理员");
		userGroup.setType(PisUserGroup.TYPE.appAdmin.name());
		Assert.assertEquals(1, this.groupService.insert(userGroup));
	}
	@Test
	public void insertCommissioner(){
		PisUserGroup userGroup = new PisUserGroup();
		userGroup.setFatherId(null);
		userGroup.setIntro("报备专员");
		userGroup.setName("报备专员");
		userGroup.setType(PisUserGroup.TYPE.commissioner.name());
		Assert.assertEquals(1, this.groupService.insert(userGroup));
	}
	@Test
	public void insertBrokingFirm(){
		PisUserGroup userGroup = new PisUserGroup();
		userGroup.setFatherId(null);
		userGroup.setIntro("经纪公司");
		userGroup.setName("经纪公司");
		userGroup.setType(PisUserGroup.TYPE.brokingFirm.name());
		Assert.assertEquals(1, this.groupService.insert(userGroup));
	}
	@Test
	public void insertSalesman(){
		PisUserGroup userGroup = new PisUserGroup();
		userGroup.setFatherId(null);
		userGroup.setIntro("业务员");
		userGroup.setName("业务员");
		userGroup.setType(PisUserGroup.TYPE.salesman.name());
		Assert.assertEquals(1, this.groupService.insert(userGroup));
	}

}
