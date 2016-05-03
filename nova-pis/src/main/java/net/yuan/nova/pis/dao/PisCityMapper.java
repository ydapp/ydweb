package net.yuan.nova.pis.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import net.yuan.nova.pis.entity.PisCity;

@Repository
public interface PisCityMapper {
	List<PisCity> getCitys(@Param(value="parentCityId") String parentCityId);
	int insertCity(PisCity pisCity);
	 PisCity getCityByName(String cityName);
	 PisCity getCityById(String cityId);
	
}
