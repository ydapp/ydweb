package net.yuan.nova.core.shiro;

import net.yuan.nova.commons.SystemConstant;
import net.yuan.nova.core.shiro.service.UserService;
import net.yuan.nova.core.shiro.vo.User;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ByteSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 后台管理员登录使用的realm
 * 
 * @author Administrator
 *
 */
public class AdminAuthorizingRealm extends AuthorizingRealm {

	private final Logger log = LoggerFactory.getLogger(AdminAuthorizingRealm.class);

	protected UserService userService;

	@Override
	public String getName() {
		return "adminRealm";
	}

	/*
	 * 获取用户权限时该方法会被执行
	 * 
	 * @see
	 * org.apache.shiro.realm.AuthorizingRealm#doGetAuthorizationInfo(org.apache
	 * .shiro.subject.PrincipalCollection)
	 */
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		System.out.println("获得权限信息");
		if (principals == null) {
			throw new AuthorizationException("Principal对象不能为空");
		}
		// 获得用户名
		String username = (String) principals.getPrimaryPrincipal();
		SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
		// 设置角色信息
		info.setRoles(userService.findRoles(username, User.Type.staff));
		// 设置权限信息
		info.setStringPermissions(userService.findPermissions(username, User.Type.staff));
		return info;
	}

	/*
	 * 在用户登录时该方法会被执行，用于登录的认证
	 * 
	 * @see
	 * org.apache.shiro.realm.AuthenticatingRealm#doGetAuthenticationInfo(org
	 * .apache.shiro.authc.AuthenticationToken)
	 */
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		System.out.println("登录:" + getAuthenticationTokenClass().getName());

		// 通过用户名和密码登录
		UsernamePasswordToken usernamePasswordToken = (UsernamePasswordToken) token;
		// 获得登录的用户名
		String username = usernamePasswordToken.getUsername();
		log.debug("需要认证的用户{}", username);
		// 用户名密码验证
		if (StringUtils.isNotBlank(username)) {
			User staff = userService.findUser(username, User.Type.staff);
			if (staff != null) {
				log.debug("找到用户，返回认证信息@{}", getName());
				AuthenticationInfo authcInfo = new SimpleAuthenticationInfo(staff.getLoginName(), staff.getPassword()
						.toCharArray(), ByteSource.Util.bytes(staff.getSalt()), getName());
				// 将当前用户写入session
				Subject currentUser = SecurityUtils.getSubject();
				Session session = currentUser.getSession();
				session.setAttribute(SystemConstant.SESSION_USER, staff);
				return authcInfo;
			} else {
				log.debug("没有找到相应的用户@{}", getName());
				throw new UnknownAccountException();// 没找到帐号
			}
		} else {
			log.debug("用户名为空@{}", getName());
			throw new UnknownAccountException();
		}
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

}
