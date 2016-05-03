package net.yuan.nova.pis.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext.xml")
public class PisProjectServiceTest  extends AbstractJUnit4SpringContextTests{
	@Autowired
	private PisProjectService projectService;
	@Test
	public void insert(){
		
	}
	@Test
	public void getByUserId(){
		
	}
	@Test
	public void getByBuildingId(){
		
	}
}
