package net.yuan.nova.core.shiro;

import net.yuan.nova.commons.SystemConstant;
import net.yuan.nova.core.shiro.vo.User;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 公共用户第三方oauth登录的认证和授权realm，使用<code>{@link OauthAuthorizingRealm}</code>
 * 
 * @author Administrator
 *
 */
public class OauthAuthorizingRealm extends PubUserAuthorizingRealm {

	private final Logger log = LoggerFactory.getLogger(OauthAuthorizingRealm.class);

	public OauthAuthorizingRealm() {
		super();
		setAuthenticationTokenClass(OauthAuthenticationToken.class);
	}

	@Override
	public String getName() {
		return "oauthRealm";
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
		OauthAuthenticationToken oauthToken = (OauthAuthenticationToken) token;
		// 获得登录的用户名
		String imei = oauthToken.getImei();
		log.debug("当前登录的设备标识{}", imei);
		if (StringUtils.isBlank(imei)) {
			log.warn("imei为空，退出登录过程");
			throw new AuthenticationException("imei为空，不能登录");
		}
		String uid = oauthToken.getUid();
		log.debug("第三方平台返回的UID：{}", uid);

		String platType = oauthToken.getPlatType();
		log.debug("当前认证使用的平台：{}", platType);
		// 用户名密码验证
		if (StringUtils.isNotBlank(uid)) {
			User user = userService.findUserByUid(uid, platType);
			if (user != null) {
				log.debug("用户不为空，返回认证信息@{}", getName());
				AuthenticationInfo authcInfo = new SimpleAuthenticationInfo(user.getLoginName(), user.getPassword()
						.toCharArray(), getName());
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

}
