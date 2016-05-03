package net.yuan.nova.pis.controller;

import javax.servlet.http.HttpServletRequest;

import net.yuan.nova.pis.entity.vo.VersionVo;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class VersionController {

	protected final Logger log = LoggerFactory.getLogger(VersionController.class);

	/**
	 * 检测软件更新
	 */
	@RequestMapping(value = "/api/check/update")
	public Object checkUpdate(HttpServletRequest request, ModelMap modelMap) {
		String appid = StringUtils.trimToEmpty(request.getParameter("appid"));
		System.out.println("appid:" + appid);
		String version = StringUtils.trimToEmpty(request.getParameter("version"));
		System.out.println("version:" + version);
		String imei = StringUtils.trimToEmpty(request.getParameter("imei"));
		System.out.println("imei:" + imei);
		VersionVo versionVo = new VersionVo();
		versionVo.setVersion("1.0.0");
		versionVo.setStatus(0);
		versionVo.setTitle("版本更新");
		versionVo.setNote("有一个新版本发布了");
		versionVo.setUrl("http://www.dcloud.io/hellomui/HelloMUI.apk");
		return versionVo;
	}
}
