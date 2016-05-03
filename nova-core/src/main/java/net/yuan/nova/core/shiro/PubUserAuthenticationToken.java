package net.yuan.nova.core.shiro;

import org.apache.shiro.authc.RememberMeAuthenticationToken;

public class PubUserAuthenticationToken implements ImeiAuthenticationToken, RememberMeAuthenticationToken {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * The username
	 */
	private String username;

	/**
	 * The password, in char[] format
	 */
	private char[] password;

	/**
	 * Whether or not 'rememberMe' should be enabled for the corresponding login
	 * attempt; default is <code>false</code>
	 */
	private boolean rememberMe = false;

	/**
	 * The imei
	 */
	private String imei;

	/*--------------------------------------------
	|         C O N S T R U C T O R S           |
	============================================*/

	/**
	 * JavaBeans compatible no-arg constructor.
	 */

	public PubUserAuthenticationToken(final String username, final char[] password, final String imei) {
		this(username, password, false, imei);
	}

	public PubUserAuthenticationToken(final String username, final String password, final String imei) {
		this(username, password != null ? password.toCharArray() : null, false, imei);
	}

	public PubUserAuthenticationToken(final String username, final char[] password, final boolean rememberMe,
			final String imei) {

		this.username = username;
		this.password = password;
		this.rememberMe = rememberMe;
		this.imei = imei;
	}

	public PubUserAuthenticationToken(final String username, final String password, final boolean rememberMe,
			final String imei) {
		this(username, password != null ? password.toCharArray() : null, rememberMe, imei);
	}

	/*--------------------------------------------
	|  A C C E S S O R S / M O D I F I E R S    |
	============================================*/

	@Override
	public Object getPrincipal() {
		return getUsername();
	}

	@Override
	public Object getCredentials() {
		return getPassword();
	}

	public void setRememberMe(boolean rememberMe) {
		this.rememberMe = rememberMe;
	}

	@Override
	public boolean isRememberMe() {
		return rememberMe;
	}

	@Override
	public String getImei() {
		return this.imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public char[] getPassword() {
		return password;
	}

	public void setPassword(char[] password) {
		this.password = password;
	}

}
