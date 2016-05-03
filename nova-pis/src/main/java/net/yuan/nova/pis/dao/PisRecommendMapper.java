package net.yuan.nova.pis.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import net.yuan.nova.pis.entity.PisRecommend;

@Repository
public interface PisRecommendMapper {
	PisRecommend getById(String recommendId);
	List<PisRecommend> getWaitingPresent(PisRecommend recommend);
	List<PisRecommend> getWaitingConfirm(String buildingId);
	int insert(PisRecommend recommend);
	int customerPresent(PisRecommend recommend);
	int recommendConfirm(PisRecommend recommend);
	List<PisRecommend> getMyPresent(@Param("presentUserId") String presentUserId);
	List<PisRecommend> getMyConfirm(@Param("confirmUserId") String confirmUserId);
	List<PisRecommend> getAll();
	List<PisRecommend> getByBrokingFirmId(@Param("brokingFirmId") String brokingFirmId);
	List<PisRecommend> getBySaleman(@Param("userId") String userId);
	List<PisRecommend> getByBuildingId(@Param("buildingId") String buildingId);
}
