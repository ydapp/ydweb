package net.yuan.nova.core.shiro;

import org.apache.shiro.authc.AuthenticationToken;

public interface ImeiAuthenticationToken extends AuthenticationToken {

	/**
	 * IMEI（International Mobile Equipment Identity）是移动设备国际身份码的缩写
	 * 
	 * @return
	 */
	String getImei();
}
