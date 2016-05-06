package net.yuan.nova.pis.service;

import java.util.Set;

import net.yuan.nova.core.shiro.service.UserService;
import net.yuan.nova.core.shiro.vo.User;
import net.yuan.nova.core.shiro.vo.User.Type;
import net.yuan.nova.pis.entity.PisUser;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("userService")
public class UserServiceImpl implements UserService {
	private static Log log = LogFactory.getLog(UserServiceImpl.class);
	@Autowired
	private PisUserService pisUserService;

	@Override
	public User findUser(String username, Type userType) {
		PisUser pisUser = pisUserService.findUserByUserName(username);
		if (pisUser.getType().equals("F")){
			log.debug("自由用户，不受登陆限制");
			return pisUser.toUser();
		}
		String type = null;
		if (Type.pubUser.equals(userType)) {
			type = "U";
		} else if (Type.staff.equals(userType)) {
			type = "A";
		} else if (Type.free.equals(userType)){
			type = "F";
		}
		if ("F".equals(type)){
			log.debug("登陆方式不受限制，任何用户都类型可以");
			return pisUser.toUser();
		}
		log.debug("type:" + type + " pisUser.type:" + pisUser.getType());
		if (StringUtils.isNotBlank(type) && type.equals(pisUser.getType())) {
			return pisUser.toUser();
		}
		return null;
	}

	@Override
	public User findUserByUid(String oauthId, String oauthPlatType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> findRoles(String username, Type userType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> findPermissions(String username, Type userType) {
		// TODO Auto-generated method stub
		return null;
	}

}
