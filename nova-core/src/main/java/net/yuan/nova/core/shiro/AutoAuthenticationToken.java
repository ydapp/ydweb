package net.yuan.nova.core.shiro;

import org.apache.shiro.authc.RememberMeAuthenticationToken;

/**
 * 用户自动登录时使用的Token
 * 
 * @author Administrator
 *
 */
public class AutoAuthenticationToken implements ImeiAuthenticationToken, RememberMeAuthenticationToken {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The username
	 */
	private String username;

	/**
	 * Whether or not 'rememberMe' should be enabled for the corresponding login
	 * attempt; default is <code>false</code>
	 */
	private boolean rememberMe = false;

	/**
	 * The imei
	 */
	private String imei;

	public AutoAuthenticationToken(final String username) {
		this(username, false, null);
	}

	public AutoAuthenticationToken(final String username, final String imei) {
		this(username, false, imei);
	}

	public AutoAuthenticationToken(final String username, final boolean rememberMe, final String imei) {
		this.username = username;
		this.rememberMe = rememberMe;
		this.imei = imei;
	}

	@Override
	public Object getPrincipal() {
		return getUsername();
	}

	@Override
	public Object getCredentials() {
		return getUsername();
	}

	@Override
	public boolean isRememberMe() {
		return rememberMe;
	}

	@Override
	public String getImei() {
		return imei;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setRememberMe(boolean rememberMe) {
		this.rememberMe = rememberMe;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

}
