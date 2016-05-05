package net.yuan.nova.pis.service;

import java.util.List;
import java.util.UUID;

import net.yuan.nova.core.shiro.vo.UserModel;
import net.yuan.nova.pis.dao.PisCityMapper;
import net.yuan.nova.pis.entity.PisCity;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;

/**
 * 城市
 * 
 * @author leasonlive
 *
 */
@Service
public class PisCityService {
	@Autowired
	private PisCityMapper pisCityMapper;

	/**
	 * 根据上级城市id获取城市列表
	 * 
	 * @param parentCityId
	 * @return
	 */
	@Cacheable(value = "cityCache", key = "'getCitys_'+#parentCityId")
	public List<PisCity> getCitys(String parentCityId) {
		return this.pisCityMapper.getCitys(parentCityId);
	}

	@CacheEvict(value = "cityCache", allEntries = true)
	public int insert(PisCity pisCity) {
		pisCity.setCityId(UUID.randomUUID().toString());
		if (StringUtils.isNotBlank(pisCity.getParentCityId())) {
			PisCity parent = this.getCityById(pisCity.getCityId());
			if (parent == null) {
				throw new RuntimeException("根据城市父id（" + pisCity.getParentCityId() + ")没有找到对应的城市记录");
			}
		}
		return this.pisCityMapper.insertCity(pisCity);
	}

	/**
	 * 根据城市ID获得城市信息
	 * 
	 * @param cityId
	 * @return
	 */
	@Cacheable(value = "cityCache", key = "'getCityById_'+#cityId")
	public PisCity getCityById(String cityId) {
		return this.pisCityMapper.getCityById(cityId);
	}

	/**
	 * 根据城市名称获得城市信息
	 * 
	 * @param cityName
	 * @return
	 */
	@Cacheable(value = "cityCache", key = "'getCityByName_'+#cityName")
	public PisCity getCityByName(String cityName) {
		return this.pisCityMapper.getCityByName(cityName);
	}
	public List<PisCity> getCustomers(int page, int pageSize,String parentCityId) {
		PageHelper.startPage(page, pageSize);
		return this.pisCityMapper.getCitys(parentCityId);
	} 
}
