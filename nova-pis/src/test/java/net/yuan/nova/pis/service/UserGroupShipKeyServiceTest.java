package net.yuan.nova.pis.service;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import net.yuan.nova.core.shiro.service.UserService;
import net.yuan.nova.pis.entity.PisUser;
import net.yuan.nova.pis.entity.PisUserGroup;
import net.yuan.nova.pis.entity.PisUserGroupShipKey;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext.xml")
public class UserGroupShipKeyServiceTest extends AbstractJUnit4SpringContextTests {
	@Autowired
	private UserGroupShipKeyService keyService;
	@Autowired
	private UserGroupService userGroupService;
	@Autowired
	private PisUserService userService;
	@Test
	public void insert(){
		PisUserGroup group = this.userGroupService.getByGroupName("业务员");
		PisUser user = this.userService.findUserByUserName("13913913998");
		PisUserGroupShipKey key = new PisUserGroupShipKey();
		key.setGroupId(group.getGroupId());
		key.setUserId(user.getUserId());
		this.keyService.insert(key);
	}
	@Test
	public void selectByUserId(){
		PisUser user = this.userService.findUserByUserName("13913913998");
		List<PisUserGroupShipKey> keys = this.keyService.getByUserId(user.getUserId());
		System.out.println("用户查询成功");
		for (PisUserGroupShipKey pisUserGroupShipKey : keys) {
			System.out.println("userId:" + pisUserGroupShipKey.getUserId());
			System.out.println("groupId:" + pisUserGroupShipKey.getGroupId());
		}
	}
}
