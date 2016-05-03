package net.yuan.nova.core.shiro;

import java.util.Iterator;
import java.util.Set;

import net.yuan.nova.commons.SystemConstant;
import net.yuan.nova.core.shiro.service.UserService;
import net.yuan.nova.core.shiro.vo.User;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CurrentUserUtil {

	private static final Logger log = LoggerFactory.getLogger(CurrentUserUtil.class);

	private static UserService userService;

	/**
	 * 去除当前登录用户
	 * 
	 * @return
	 */
	public static User getShiroUser() {
		Subject currentUser = SecurityUtils.getSubject();
		if (currentUser.isAuthenticated()) {
			// 用户已登录
			Session session = currentUser.getSession();
			Object user = session.getAttribute(SystemConstant.SESSION_USER);
			if (user == null) {
				log.warn("不该走到这里，尝试恢复数据");
				String realmName = null;
				Set<String> set = currentUser.getPrincipals().getRealmNames();
				Iterator<String> iterator = set.iterator();
				realmName = iterator.next();
				// 获得当前登录用户的数据
				String username = (String) currentUser.getPrincipals().getPrimaryPrincipal();
				if (StringUtils.equals(realmName, "adminRealm")) {
					// 后台用户
					user = userService.findUser(username, User.Type.staff);
				} else {
					// app用户
					user = userService.findUser(username, User.Type.pubUser);
				}
				session.setAttribute(SystemConstant.SESSION_USER, user);
			}
			return (User) user;
		}
		return null;
	}

	public static boolean isAdmin() {
		User user = getShiroUser();
		return StringUtils.equals(SystemConstant.ADMIN_USER, user.getLoginName());
	}

	public static void setUserService(UserService userService) {
		CurrentUserUtil.userService = userService;
	}

}
