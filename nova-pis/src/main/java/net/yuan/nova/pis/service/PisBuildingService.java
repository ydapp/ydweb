package net.yuan.nova.pis.service;

import java.util.List;
import java.util.UUID;

import net.yuan.nova.pis.dao.PisBuildingMapper;
import net.yuan.nova.pis.dao.PisPropertyMapper;
import net.yuan.nova.pis.entity.PisBuilding;
import net.yuan.nova.pis.entity.PisProperty;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;

@Service
public class PisBuildingService {
	@Autowired
	private PisBuildingMapper pisBuildingMapper;
	@Autowired
	private PisPropertyMapper pisPropertyMapper;

	@CacheEvict(value = "buildingCache", allEntries = true)
	public int insert(PisBuilding pisBuilding) {
		if (StringUtils.isEmpty(pisBuilding.getBuildingId())){
			pisBuilding.setBuildingId(UUID.randomUUID().toString());
		}
		return this.pisBuildingMapper.insert(pisBuilding);
	}

	/**
	 * 获取某城市的楼盘信息
	 * 
	 * @param cityId
	 * @return
	 */
	@Cacheable(value = "buildingCache", key = "'getBuilding_'+#cityId")
	public List<PisBuilding> getBuilding(String cityId) {
		return this.pisBuildingMapper.getBuilding(cityId);
	}

	/**
	 * 根据ID获得楼盘详情
	 * 
	 * @param buildingId
	 * @return
	 */
	@Cacheable(value = "buildingCache", key = "'getById_'+#buildingId")
	public PisBuilding getById(String buildingId) {
		return this.pisBuildingMapper.getById(buildingId);
	}

	/**
	 * 根据名称获得楼盘信息
	 * 
	 * @param buildingName
	 * @return
	 */
	@Cacheable(value = "buildingCache", key = "'getByName_'+#buildingName")
	public PisBuilding getByName(String buildingName) {
		List<PisBuilding> byName = this.pisBuildingMapper.getByName(buildingName);
		if (byName != null && byName.size() > 0) {
			return byName.get(0);
		}
		return null;
	}

	// ///////////////////////////////////////////////////////////
	// ////////////主表操作/////////////////////////////
	// ///////////////////////////////////////////////////////////

	public int addPisProperty(PisProperty pisProperty) {
		pisProperty.setPropertyId(UUID.randomUUID().toString());
		return pisPropertyMapper.insertSelective(pisProperty);
	}

	public List<PisProperty> selectPisProperties(int page, int pageSize) {
		PageHelper.startPage(page, pageSize);
		return pisPropertyMapper.selectPisProperties();
	}

	/**
	 * 查询楼市详细信息
	 * 
	 * @param id
	 * @return
	 */
	public PisProperty selectByPrimaryKey(String id) {
		return pisPropertyMapper.selectByPrimaryKey(id);
	}

}
