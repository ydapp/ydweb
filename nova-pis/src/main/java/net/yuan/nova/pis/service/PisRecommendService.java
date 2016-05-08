package net.yuan.nova.pis.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import net.yuan.nova.pis.controller.model.CustomerModel;
import net.yuan.nova.pis.dao.PisRecommendMapper;
import net.yuan.nova.pis.entity.PisBrokingFirm;
import net.yuan.nova.pis.entity.PisBuilding;
import net.yuan.nova.pis.entity.PisCity;
import net.yuan.nova.pis.entity.PisProject;
import net.yuan.nova.pis.entity.PisProperty;
import net.yuan.nova.pis.entity.PisRecommend;
import net.yuan.nova.pis.entity.PisRecommend.Status;
import net.yuan.nova.pis.entity.PisUser;
import net.yuan.nova.pis.entity.PisUserExtend;
import net.yuan.nova.pis.entity.vo.PisRecommendVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.github.pagehelper.PageHelper;

/**
 * 推荐
 * 
 * @author leasonlive
 *
 */
@Service
public class PisRecommendService {
	@Autowired
	private PisRecommendMapper recommendMapper;
	@Autowired
	private PisProjectService projectService;
	@Autowired
	private PisCityService pisCityService;
	@Autowired
	private PisBuildingService pisBuildingService;
	@Autowired
	private PisUserService pisUserService;
	@Autowired
	private PisUserExtendService userExtendService;
	@Autowired
	private PisBrokingFirmService brokingFirmService;

	/**
	 * 根据推荐id得到推荐对象
	 * 
	 * @param recommendId
	 * @return
	 */
	public PisRecommendVo getById(String recommendId) {
		PisRecommend recommend = this.recommendMapper.getById(recommendId);
		if (recommend == null) {
			return null;
		}
		PisRecommendVo pisRecommendVo = new PisRecommendVo(recommend);
		// 获得城市数据
		PisCity city = pisCityService.getCityById(recommend.getCityId());
		if (city != null) {
			pisRecommendVo.setCityName(city.getCityName());
		}
		// 楼盘名称
		PisBuilding building = pisBuildingService.getById(recommend.getBuildingId());
		if (building != null) {
			pisRecommendVo.setBuildingName(building.getBuildingName());
		}
		// 推荐人
		PisUser refree = pisUserService.findUserById(recommend.getRefreeId());
		if (refree != null) {
			pisRecommendVo.setRefreeName(refree.getUserName());
			pisRecommendVo.setRefreeNick(refree.getNick());
			PisUserExtend userExtend = this.userExtendService.selectByUserId(recommend.getRefreeId());
			if (userExtend == null){
				pisRecommendVo.setRefreeExtend("未配置经纪公司");
			} else {
				PisBrokingFirm brokingFirm = this.brokingFirmService.findById(userExtend.getBrokingFirmId());
				pisRecommendVo.setRefreeExtend(brokingFirm.getBrokingFirmName());
			}
			
		}
		// 客户到场确认人
		String customerPresentUserId = recommend.getCustomerPresentUserId();
		if (StringUtils.isNotBlank(customerPresentUserId)) {
			PisUser presentUser = pisUserService.findUserById(customerPresentUserId);
			if (refree != null) {
				pisRecommendVo.setCustomerPresentUserName(presentUser.getUserName());
				pisRecommendVo.setCustomerPresentUserNick(presentUser.getNick());
			}
		}
		// 推荐确认人
		String recommendConfirmUserId = recommend.getRecommendConfirmUserId();
		if (StringUtils.isNotBlank(recommendConfirmUserId)) {
			PisUser confirmUser = pisUserService.findUserById(recommendConfirmUserId);
			if (refree != null) {
				pisRecommendVo.setRecommendConfirmUserName(confirmUser.getUserName());
				pisRecommendVo.setRecommendConfirmUserNick(confirmUser.getNick());
			}
		}
		pisRecommendVo.setStatusTitle(recommend.getStatusName());
		return pisRecommendVo;
	}

	/**
	 * 查询某个经纪人在某个楼盘，等待客户到场的报备，经纪人用
	 * 
	 * @param refreeId
	 *            推荐人
	 * @param buildingId
	 *            楼盘
	 * @return
	 */
	public List<PisRecommend> getWaitingPresent(String refreeId, String buildingId) {
		PisRecommend recommend = new PisRecommend();
		recommend.setRefreeId(refreeId);
		recommend.setBuildingId(buildingId);
		recommend.setStatus(PisRecommend.Status.appointment);
		return this.recommendMapper.getWaitingPresent(recommend);
	}

	/**
	 * 查询某个经纪人在所有楼盘，等待客户到场的报备，经纪人用
	 * 
	 * @param refreeId
	 * @return
	 */
	public List<PisRecommend> getWaitingPresent(String refreeId) {
		PisRecommend recommend = new PisRecommend();
		recommend.setRefreeId(refreeId);
		recommend.setStatus(PisRecommend.Status.appointment);
		return this.recommendMapper.getWaitingPresent(recommend);
	}

	/**
	 * 某个楼盘中等待确认的推荐（客户已经到场的）,驻场专员用
	 * 
	 * @param userId
	 *            报备专员ID
	 * @return
	 */
	public List<PisRecommend> getWaitingConfirm(String userId) {
		// 先根据报备人员id查找楼盘id，然后根据楼盘id查找报备带确认信息
		PisUserExtend userExtend = this.userExtendService.selectByUserId(userId);
		if (userExtend == null){
			return new ArrayList<PisRecommend>();
		}
		return this.recommendMapper.getWaitingConfirm(userExtend.getBuildingId());
	}

	/**
	 * 新建一条推荐
	 * 
	 * @param recommend
	 * @return 推荐的id
	 */
	public String insert(PisRecommend recommend) {
		if (StringUtils.isEmpty(recommend.getRecommendId())) {
			recommend.setRecommendId(UUID.randomUUID().toString());
		}
		if (recommend.getRecommendDate() == null) {
			recommend.setRecommendDate(new Date());
		}
		recommend.setStatus(PisRecommend.Status.appointment);
		this.recommendMapper.insert(recommend);
		this.pisBuildingService.recommend(recommend.getBuildingId());
		return recommend.getRecommendId();
	}

	/**
	 * 客户到现场
	 * 
	 * @param recommendId
	 * @param presentUserId
	 */
	public int customerPresent(String recommendId, String presentUserId) {
		PisRecommend recommend = new PisRecommend();
		recommend.setRecommendId(recommendId);
		recommend.setCustomerPresentUserId(presentUserId);
		recommend.setCustomerPresentDate(new Date());
		recommend.setStatus(PisRecommend.Status.present);
		return this.recommendMapper.customerPresent(recommend);
	}

	public List<PisRecommendVo> getMyPresent(String presentUserId) {
		List<PisRecommend> myPresent = this.recommendMapper.getMyPresent(presentUserId);
		if (myPresent == null) {
			return null;
		}
		List<PisRecommendVo> list = new ArrayList<PisRecommendVo>();
		for (PisRecommend pisRecommend : myPresent) {
			PisRecommendVo pisRecommendVo = new PisRecommendVo(pisRecommend);
			// 获得城市数据
			PisCity city = pisCityService.getCityById(pisRecommend.getCityId());
			if (city != null) {
				pisRecommendVo.setCityName(city.getCityName());
			}
			// 楼盘名称
			PisBuilding building = pisBuildingService.getById(pisRecommend.getBuildingId());
			if (building != null) {
				pisRecommendVo.setBuildingName(building.getBuildingName());
			}
			list.add(pisRecommendVo);
		}
		return list;
	}

	/**
	 * 报备确认
	 * 
	 * @param recommendId
	 * @param confirmUserId
	 */
	public int recommendConfirm(String recommendId, String confirmUserId, String recommendConfirmAdvice) {
		PisRecommend old = this.getById(recommendId);
		Status confirmStatus = old.getNextStatus();
		recommendConfirmAdvice = PisRecommend.getStatusName(confirmStatus) + ":" + StringUtils.trimToEmpty(recommendConfirmAdvice);
		if (StringUtils.isNotEmpty(old.getRecommendConfirmAdvice())){
			recommendConfirmAdvice = old.getRecommendConfirmAdvice() + "\n\r" + recommendConfirmAdvice;
		}
		PisRecommend recommend = new PisRecommend();
		recommend.setRecommendId(recommendId);
		recommend.setRecommendConfirmUserId(confirmUserId);
		recommend.setRecommendConfirmDate(new Date());
		recommend.setRecommendConfirmAdvice(recommendConfirmAdvice);
		recommend.setStatus(confirmStatus);
		int record= this.recommendMapper.recommendConfirm(recommend);
		if (confirmStatus != null){
			if (confirmStatus.equals(Status.order)){
				this.pisBuildingService.order(old.getBuildingId());
			} else if (confirmStatus.equals(Status.buy)){
				this.pisBuildingService.buy(old.getBuildingId());
			}
		}
		return record;
	}

	/**
	 * 得到我的报备确认数据
	 * 
	 * @param confirmUserId
	 * @return
	 */
	public List<PisRecommend> getMyConfirm(String confirmUserId) {
		return this.recommendMapper.getMyConfirm(confirmUserId);
	}
	/**
	 * 得到所有报备信息，一般用于生成excell用的
	 * @return
	 */
	public List<PisRecommend> getAll(){
		return this.recommendMapper.getAll();
	}
	/**
	 * 根据经纪公司ID获取所有推荐信息
	 * @return
	 */
	public List<PisRecommend> getByBrokingFirmId(String brokingFirmId){
		return this.recommendMapper.getByBrokingFirmId(brokingFirmId);
	}
	/**
	 * 根据经纪人id获取该人的所有推荐
	 * @param userId
	 * @return
	 */
	public List<PisRecommend> getBySaleman(String userId){
		return this.recommendMapper.getBySaleman(userId);
	}
	/**
	 * 根据楼盘id获取所有的推荐
	 * @param buildingId
	 * @return
	 */
	public List<PisRecommend> getByBuildingId(String buildingId){
		return this.recommendMapper.getByBuildingId(buildingId);
	}
	/**
	 * 通过用户名判断当天同一个用户是否录入过三次
	 * @param customer_name
	 * @return
	 */
	public int getCustomerByTodayAndTel(String customer_tel,String refreeId){
		 return this.recommendMapper.getCustomerByTodayAndTel(customer_tel,refreeId);
	}

	public List<CustomerModel> getCustomers(int page, int pageSize) {
		PageHelper.startPage(page, pageSize);
		return this.recommendMapper.getCustomer();
	}
	
}
