package net.yuan.nova.pis.dao;

import org.springframework.stereotype.Repository;

import net.yuan.nova.pis.entity.PisProject;

@Repository
public interface PisProjectMapper {
	PisProject getByUserId(String userId);
	PisProject getByBuildingId(String buildingId);
	int insert(PisProject project);
}
