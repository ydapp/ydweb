package net.yuan.nova.pis.service;

import java.util.Set;

import net.yuan.nova.core.shiro.service.UserService;
import net.yuan.nova.core.shiro.vo.User;
import net.yuan.nova.core.shiro.vo.User.Type;
import net.yuan.nova.pis.entity.PisUser;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("userService")
public class UserServiceImpl implements UserService {

	@Autowired
	private PisUserService pisUserService;

	@Override
	public User findUser(String username, Type userType) {
		PisUser pisUser = pisUserService.findUserByUserName(username);
		String type = null;
		if (Type.pubUser.equals(userType)) {
			type = "U";
		} else if (Type.staff.equals(userType)) {
			type = "A";
		}
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
