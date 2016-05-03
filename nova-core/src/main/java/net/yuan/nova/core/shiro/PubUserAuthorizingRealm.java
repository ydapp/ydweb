package net.yuan.nova.core.shiro;

import java.util.HashSet;
import java.util.Set;

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
 * 公共用户登录的认证和授权，使用<code>{@link PubUserAuthenticationToken}</code>
 * 进行认证，判断网站、app、微信等用户的等
 * 
 * @author Administrator
 *
 */
public class PubUserAuthorizingRealm extends AuthorizingRealm {

	private final Logger log = LoggerFactory.getLogger(PubUserAuthorizingRealm.class);

	protected UserService userService;

	public PubUserAuthorizingRealm() {
		super();
		setAuthenticationTokenClass(PubUserAuthenticationToken.class);
	}

	@Override
	public String getName() {
		return "pubUserRealm";
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
		if (principals == null) {
			throw new AuthorizationException("Principal对象不能为空");
		}
		// 获得用户名
		String username = (String) principals.getPrimaryPrincipal();
		log.debug("获得用户{}的角色", username);
		SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
		// 设置角色信息
		Set<String> roles = new HashSet<String>();
		roles.add("pubUser");
		info.setRoles(roles);
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
		// 通过用户名和密码登录
		PubUserAuthenticationToken usernamePasswordToken = (PubUserAuthenticationToken) token;
		String imei = usernamePasswordToken.getImei();
		log.debug("当前登录的设备标识{}", imei);
		if (StringUtils.isBlank(imei)) {
			log.warn("imei为空，退出登录过程");
			throw new AuthenticationException("imei为空，不能登录");
		}
		// 获得登录的用户名
		String username = usernamePasswordToken.getUsername();
		log.debug("需要认证的用户{}", username);
		// 用户名密码验证
		if (StringUtils.isNotBlank(username)) {
			User user = userService.findUser(username, User.Type.pubUser);
			if (user != null) {
				log.debug("用户不为空，返回认证信息@{}", getName());
				AuthenticationInfo authcInfo = new SimpleAuthenticationInfo(user.getLoginName(), user.getPassword()
						.toCharArray(), ByteSource.Util.bytes(user.getSalt()), getName());
				// 将当前用户写入session
				Subject currentUser = SecurityUtils.getSubject();
				Session session = currentUser.getSession();
				session.setAttribute(SystemConstant.SESSION_USER, user);
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
