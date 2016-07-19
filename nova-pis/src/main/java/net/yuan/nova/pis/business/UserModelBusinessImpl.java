package net.yuan.nova.pis.business;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import net.yuan.nova.core.shiro.service.UserModelBusiness;
import net.yuan.nova.core.shiro.vo.User;
import net.yuan.nova.core.shiro.vo.UserModel;
import net.yuan.nova.pis.entity.PisBrokingFirm;
import net.yuan.nova.pis.entity.PisBuilding;
import net.yuan.nova.pis.entity.PisCity;
import net.yuan.nova.pis.entity.PisProperty;
import net.yuan.nova.pis.entity.PisUser;
import net.yuan.nova.pis.entity.PisUserExtend;
import net.yuan.nova.pis.entity.PisUserGroup;
import net.yuan.nova.pis.service.PisBrokingFirmService;
import net.yuan.nova.pis.service.PisBuildingService;
import net.yuan.nova.pis.service.PisCityService;
import net.yuan.nova.pis.service.PisUserExtendService;
import net.yuan.nova.pis.service.PisUserService;
import net.yuan.nova.pis.service.UserGroupService;
import net.yuan.nova.pis.service.UserGroupShipKeyService;

@Component
public class UserModelBusinessImpl implements UserModelBusiness{
	private static Log log = LogFactory.getLog(UserModelBusinessImpl.class);
	@Autowired
	private PisUserService pisUserService;
	@Autowired
	private UserGroupShipKeyService keyService;
	@Autowired
	private UserGroupService groupService;
	@Autowired
	private PisUserExtendService userExtendService;
	@Autowired
	private PisBuildingService buildingService;
	@Autowired
	private PisBrokingFirmService brokingFirmService;
	@Autowired
	private PisCityService pisCityService;
	
	public UserModel getUserModel(String userId) {
		UserModel userModel = new UserModel();
		
		PisUser pisUser = this.pisUserService.findUserById(userId);
		PisUserGroup group = this.pisUserService.getPisUserGroup(userId);
		if (group != null){
			userModel.setGroupType(group.getType());
			userModel.setGroupTypeTitle(group.getTypeTitle());
		}
		userModel.setNick(pisUser.getNick());
		userModel.setTel(pisUser.getTel());
		userModel.setUserId(pisUser.getUserId());
		userModel.setType(pisUser.getType());
		
		PisUserExtend userExtend = this.userExtendService.selectByUserId(userId);
		if (userExtend != null){
			if (StringUtils.isNoneEmpty(userExtend.getBrokingFirmId())){
				PisBrokingFirm pisBrokingFirm = this.brokingFirmService.findById(userExtend.getBrokingFirmId());
				if(null != pisBrokingFirm){
					userModel.setBrokingFirm(pisBrokingFirm.getBrokingFirmName());
				}
			}
			PisProperty pisProperty = this.buildingService.selectByPrimaryKey(userExtend.getBuildingId());
			if(null != pisProperty){
				userModel.setPropertyName(pisProperty.getPropertyName());
				PisCity pisCity = this.pisCityService.getCityById(pisProperty.getCity());
				if(null != pisCity){
					userModel.setCityName(pisCity.getCityName());
				}
			}
			if (StringUtils.isNoneEmpty(userExtend.getBuildingId())){
				log.debug("buildingId:" + userExtend.getBuildingId());
				PisBuilding building = this.buildingService.getById(userExtend.getBuildingId());
				log.debug("building:" + building);
				userModel.setBuilding(building.getBuildingName());
			}
		}
		return userModel;
	}
}
