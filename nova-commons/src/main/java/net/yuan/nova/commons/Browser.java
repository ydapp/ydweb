package net.yuan.nova.commons;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 浏览器工具类
 * 
 * 智能机浏览器版本信息
 * 
 * @author Administrator
 *
 */
public class Browser {

	private final String userAgent;
	private static Pattern iosPattern = Pattern.compile("\\(i[^;]+;( U;)? CPU.+Mac OS X");
	private static Pattern mobilePattern = Pattern.compile("AppleWebKit.*Mobile.*");
	private static Pattern appleWebKit = Pattern.compile("AppleWebKit");

	private boolean trident = false;
	private boolean presto = false;
	private boolean webKit = false;
	private boolean gecko = false;
	private boolean mobile = false;
	private boolean ios = false;
	private boolean android = false;
	private boolean iPhone = false;
	private boolean iPad = false;
	private boolean webApp = false;

	Browser(String userAgent) {
		this.userAgent = userAgent;
		Matcher iosMatcher = iosPattern.matcher(userAgent);
		Matcher mobileMatcher = mobilePattern.matcher(userAgent);
		Matcher appleWebKitMatcher = appleWebKit.matcher(userAgent);

		trident = userAgent.indexOf("Trident") > -1; // IE内核
		presto = userAgent.indexOf("Presto") > -1; // opera内核
		webKit = userAgent.indexOf("AppleWebKit") > -1; // 苹果、谷歌内核
		gecko = userAgent.indexOf("Gecko") > -1 && userAgent.indexOf("KHTML") == -1; // 火狐内核
		mobile = mobileMatcher.matches() || appleWebKitMatcher.matches(); // 是否为移动终端
		ios = iosMatcher.matches(); // ios终端
		android = userAgent.indexOf("Android") > -1 || userAgent.indexOf("Linux") > -1; // android终端或者uc浏览器
		iPhone = userAgent.indexOf("iPhone") > -1 || userAgent.indexOf("Mac") > -1; // 是否为iPhone或者QQHD浏览器
		iPad = userAgent.indexOf("iPad") > -1; // 是否iPad
		webApp = userAgent.indexOf("Safari") == -1;// 是否web应该程序，没有头部与底部
	}

	public static Browser build(String userAgent) {
		return new Browser(userAgent);
	}

	public String getUserAgent() {
		return userAgent;
	}

	public boolean isTrident() {
		return trident;
	}

	public boolean isPresto() {
		return presto;
	}

	public boolean isWebKit() {
		return webKit;
	}

	public boolean isGecko() {
		return gecko;
	}

	public boolean isMobile() {
		return mobile;
	}

	public boolean isIos() {
		return ios;
	}

	public boolean isAndroid() {
		return android;
	}

	public boolean isIPhone() {
		return iPhone;
	}

	public boolean isIPad() {
		return iPad;
	}

	public boolean isWebApp() {
		return webApp;
	}

}
