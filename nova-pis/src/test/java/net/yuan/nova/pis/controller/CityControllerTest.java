package net.yuan.nova.pis.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import net.yuan.nova.pis.entity.PisCity;
import net.yuan.nova.pis.service.PisCityService;

public class CityControllerTest  extends BaseControllerTest{
	private static Log log = LogFactory.getLog(CityControllerTest.class);
	@Autowired
	private CityController cityController;
	@Autowired
	private PisCityService cityService;
	@Override
	Object getController() {
		return this.cityController;
	}
	@Test
	public void getList(){
		
		String resp = this.mockGet("/api/getCitys/null", null);
		System.out.println("定级城市下面的子城市返回结果" + resp);
		PisCity parentCity = this.cityService.getCityByName("南京");
		String respNanjing = this.mockGet("/api/getCitys/" + parentCity.getCityId(), null);
		System.out.println("南京的下面的子城市返回结果:" + respNanjing);
	}
	
}
