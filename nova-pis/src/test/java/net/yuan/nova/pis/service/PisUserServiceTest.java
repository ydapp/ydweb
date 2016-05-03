package net.yuan.nova.pis.service;

import static org.junit.Assert.fail;
import net.sf.json.JSONObject;
import net.yuan.nova.core.shiro.PasswordHelper;
import net.yuan.nova.pis.entity.PisUser;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext.xml")
public class PisUserServiceTest extends AbstractJUnit4SpringContextTests {

	@Autowired
	private PisUserService pisUserService;
	@Autowired
	private PasswordHelper passwordHelper;

	@Test
	public void testFindUserById() {
		fail("Not yet implemented");
	}

	@Test
	public void testFindUserByloginName() {
		PisUser pisUser = pisUserService.findUserByUserName("13913913998");
		System.out.println(JSONObject.fromObject(pisUser));

		String encryptPassword = passwordHelper.encryptPassword("123456", pisUser.getSalt());
		Assert.assertEquals(encryptPassword, pisUser.getPassword());
	}

	@Test
	public void testFindUserByTel() {
		PisUser pisUser = pisUserService.findUserByTel("13913913999");
		System.out.println(JSONObject.fromObject(pisUser));
	}

	@Test
	public void testFindUserByEmail() {
		PisUser pisUser = pisUserService.findUserByEmail("test@test.com");
		System.out.println(JSONObject.fromObject(pisUser));
	}

	@Test
	public void testInsertUser() {

		PisUser pisUser = pisUserService.findUserByUserName("admin");
		if (pisUser != null) {
			System.out.println("用户已存在");
			return;
		}
		pisUser = new PisUser();
		pisUser.setUserName("admin");
		pisUser.setNick("admin");
		pisUser.setPassword("admin123");

		pisUser.setEmail("admin@test.com");
		pisUser.setTel("13851452013");
		pisUser.setType("A");

		int count = pisUserService.insertUser(pisUser);
		System.out.println(count);
	}

	@Test
	public void testUpdateUser() {
		fail("Not yet implemented");
	}

	@Test
	public void testUpdateUserPwd() {
		fail("Not yet implemented");
	}

}
