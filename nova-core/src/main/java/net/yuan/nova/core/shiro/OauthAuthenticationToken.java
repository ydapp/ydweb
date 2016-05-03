package net.yuan.nova.core.shiro;

import org.apache.shiro.authc.RememberMeAuthenticationToken;

/**
 * 使用第三方oauth登录时使用的Token
 * 
 * @author Administrator
 *
 */
public class OauthAuthenticationToken implements ImeiAuthenticationToken, RememberMeAuthenticationToken {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The uid, in char[] format
	 */
	private String uid;

	/**
	 * The uid, in char[] format
	 */
	private String platType;

	/**
	 * Whether or not 'rememberMe' should be enabled for the corresponding login
	 * attempt; default is <code>false</code>
	 */
	private boolean rememberMe = false;

	/**
	 * The imei
	 */
	private String imei;

	public OauthAuthenticationToken(final String uid, final String platType, final String imei) {
		this(uid, platType, false, imei);
	}

	public OauthAuthenticationToken(final String uid, final String platType, final boolean rememberMe, final String imei) {
		this.uid = uid;
		this.platType = platType;
		this.rememberMe = rememberMe;
		this.imei = imei;
	}

	@Override
	public Object getPrincipal() {
		return getUid();
	}

	@Override
	public Object getCredentials() {
		return getUid();
	}

	@Override
	public boolean isRememberMe() {
		return rememberMe;
	}

	@Override
	public String getImei() {
		return imei;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public void setRememberMe(boolean rememberMe) {
		this.rememberMe = rememberMe;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public String getPlatType() {
		return platType;
	}

	public void setPlatType(String platType) {
		this.platType = platType;
	}

}
