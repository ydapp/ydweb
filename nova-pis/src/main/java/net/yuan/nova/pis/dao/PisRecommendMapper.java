package net.yuan.nova.pis.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import net.yuan.nova.pis.controller.model.CustomerModel;
import net.yuan.nova.pis.entity.PisRecommend;

@Repository
public interface PisRecommendMapper {
	PisRecommend getById(String recommendId);
	List<PisRecommend> getWaitingPresent(PisRecommend recommend);
	List<PisRecommend> getWaitingConfirm(@Param("buildingId") String buildingId);
	int insert(PisRecommend recommend);
	int customerPresent(PisRecommend recommend);
	int recommendConfirm(PisRecommend recommend);
	int getCustomerByTodayAndTel(@Param("customerTel") String customerTel,@Param("refreeId") String refreeId);
	int updateRecommendStatusByRecommendId(String recommendId);
	List<PisRecommend> getMyPresent(@Param("presentUserId") String presentUserId);
	List<PisRecommend> getMyPresentByStatus(@Param("presentUserId") String presentUserId,@Param("status") String status);
	List<PisRecommend> getMyConfirm(@Param("confirmUserId") String confirmUserId);
	List<PisRecommend> getMyConfirmByStatus(@Param("confirmUserId") String confirmUserId,@Param("status") String status);
	List<PisRecommend>getRecommendByStatus(@Param("status") String status);
	List<PisRecommend> getAll();
	List<PisRecommend> getByBrokingFirmId(@Param("brokingFirmId") String brokingFirmId);
	List<PisRecommend> getBySaleman(@Param("userId") String userId);
	List<PisRecommend> getByBuildingId(@Param("buildingId") String buildingId);
	@Select("select CUSTOMER_TEL as customerTel,CUSTOMER_NAME as customerName, count(*) as refreeCount from PIS_RECOMMEND group by CUSTOMER_TEL,CUSTOMER_NAME")
	List<CustomerModel> getCustomer();
	@Update("update PIS_RECOMMEND set REMARK=#{remark} where recommend_id=#{recommendId}")
	int recommendAppendRemark(PisRecommend recommend);
}
