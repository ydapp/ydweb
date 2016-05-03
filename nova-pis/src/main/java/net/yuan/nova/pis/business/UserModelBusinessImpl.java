package net.yuan.nova.pis.business;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import net.yuan.nova.core.shiro.service.UserModelBusiness;
import net.yuan.nova.core.shiro.vo.User;
import net.yuan.nova.core.shiro.vo.UserModel;
import net.yuan.nova.pis.entity.PisUser;
import net.yuan.nova.pis.entity.PisUserExtend;
import net.yuan.nova.pis.entity.PisUserGroup;
import net.yuan.nova.pis.service.PisBrokingFirmService;
import net.yuan.nova.pis.service.PisBuildingService;
import net.yuan.nova.pis.service.PisUserExtendService;
import net.yuan.nova.pis.service.PisUserService;
import net.yuan.nova.pis.service.UserGroupService;
import net.yuan.nova.pis.service.UserGroupShipKeyService;

@Component
public class UserModelBusinessImpl implements UserModelBusiness{
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
	public UserModel getUserModel(User user) {
		UserModel userModel = new UserModel();
		
		PisUser pisUser = this.pisUserService.findUserById(user.getUserId());
		PisUserGroup group = this.pisUserService.getPisUserGroup(user.getUserId());
		if (group != null){
			userModel.setGroupType(group.getType());
			userModel.setGroupTypeTitle(group.getTypeTitle());
		}
		userModel.setNick(pisUser.getNick());
		userModel.setTel(pisUser.getTel());
		
		PisUserExtend userExtend = this.userExtendService.selectByUserId(user.getUserId());
		if (userExtend != null){
			if (StringUtils.isNoneEmpty(userExtend.getBrokingFirmId())){
				userModel.setBrokingFirm(this.brokingFirmService.findById(userExtend.getBrokingFirmId()).getBrokingFirmName());
			}
			if (StringUtils.isNoneEmpty(userExtend.getBuildingId())){
				userModel.setBuilding(this.buildingService.getById(userExtend.getBuildingId()).getBuildingName());
			}
		}
		return userModel;
	}
}
