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

import net.yuan.nova.pis.entity.PisBuilding;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext.xml")
public class PisBuildingServiceTest extends AbstractJUnit4SpringContextTests {
	@Autowired
	private PisBuildingService buildingService;
	@Test
	public void insert(){
		PisBuilding pisBuilding = new PisBuilding();
		pisBuilding.setCityId("bae84efdf597");
		pisBuilding.setBuildingId(UUID.randomUUID().toString());
		pisBuilding.setBuildingName("牛首山楼盘");
		this.buildingService.insert(pisBuilding);
	}
	@Test
	public void getBuilding(){
		String cityId = "bae84efdf597";
		List<PisBuilding> list = this.buildingService.getBuilding(cityId);
		for (PisBuilding pisBuilding : list) {
			System.out.println("id:" + pisBuilding.getBuildingId() + " name:" + pisBuilding.getBuildingName());
		}
	}
	@Test
	public void getById(){
		String buildingId = "9d857b92-e7ce-4128-88c8-769d767ec7de";
		PisBuilding pisBuilding = this.buildingService.getById(buildingId);
		Assert.assertEquals("牛首山楼盘", pisBuilding.getBuildingName());
	}
	@Test
	public void getByName(){
		PisBuilding pisBuilding =this.buildingService.getByName("牛首山楼盘");
		Assert.assertNotNull(pisBuilding.getBuildingId());
	}
}
