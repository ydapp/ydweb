package net.yuan.nova.pis.service;

import java.util.List;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import net.yuan.nova.pis.entity.PisCity;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext.xml")
public class PisCityServiceTest extends AbstractJUnit4SpringContextTests {
	@Autowired
	private PisCityService pisCityService;
	@Test
	public void getCitys(){
		List<PisCity> citys = this.pisCityService.getCitys("");
		for (PisCity pisCity : citys) {
			System.out.println(pisCity.getCityName());
		}
	}
	@Test
	public void insert(){
		PisCity pisCity = new PisCity();
		pisCity.setCityId(UUID.randomUUID().toString());
		pisCity.setCityName("广州");
		pisCity.setParentCityId(null);
		this.pisCityService.insert(pisCity);
	}
	@Test
	public void getCityByName(){
		PisCity pisCity = this.pisCityService.getCityByName("南京");
		Assert.assertNotNull(pisCity.getCityId());
	}
	@Test
	public void getCityById(){
		PisCity pisCity = this.pisCityService.getCityByName("南京");
		PisCity pisCity2 = this.pisCityService.getCityById(pisCity.getCityId());
		Assert.assertNotNull(pisCity2);
	}
}
