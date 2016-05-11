package net.yuan.nova.pis.dao;

import java.util.List;

import net.yuan.nova.pis.entity.PisBuilding;

import org.springframework.stereotype.Repository;

@Repository
public interface PisBuildingMapper {
	int insert(PisBuilding pisBuilding);

	List<PisBuilding> getBuilding(String cityId);

	PisBuilding getById(String buildingId);

	List<PisBuilding> getByName(String buildingName);
	
	int updateBuilding(PisBuilding pisBuilding);
}
