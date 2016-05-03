package net.yuan.nova.pis.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.yuan.nova.commons.SystemConstant;
import net.yuan.nova.core.entity.Attachment;
import net.yuan.nova.core.service.AttachmentService;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * 附件下载
 * 
 * @author Administrator
 *
 */
@Controller
public class ApiDownloadController {

	protected final Logger log = LoggerFactory.getLogger(ApiDownloadController.class);

	@Autowired
	private AttachmentService attachmentService;

	@RequestMapping("/api/download")
	public ModelAndView download(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String fileId = StringUtils.trimToEmpty(request.getParameter("fileId"));
		if (StringUtils.isNotBlank(fileId)) {
			return this.download(fileId, request, response);
		}
		return errorModelAndView();
	}

	@RequestMapping("/api/download/{fileId}")
	public ModelAndView download(@PathVariable("fileId") String fileId, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		java.io.BufferedInputStream bis = null;
		java.io.BufferedOutputStream bos = null;
		// 获得文件路径
		Attachment attachment = attachmentService.getAttachmentById(fileId);
		if (attachment == null) {
			return errorModelAndView();
		}
		String path = attachment.getSavePath();
		// 获得文件路径
		String downLoadPath = System.getProperty(SystemConstant.WEBAPP_ROOT) + path;
		log.debug(downLoadPath);
		String fileName = attachment.getAtchName();
		try {
			File file = new File(downLoadPath);
			if (!file.exists()) {// 判断文件目录是否存在
				file.getParentFile().mkdirs();
				file = attachmentService.transferToFile(attachment);
			}
			if (file == null || !file.exists()) {
				return errorModelAndView();
			} else {
				// 文件大小
				long fileLength = file.length();
				// 设置相应类型
				response.setContentType("application/octet-stream");
				// 设置文件名
				String agent = request.getHeader("User-Agent");
				if (agent != null && (agent.indexOf("MSIE") != -1 || agent.indexOf("rv:11") != -1)) {
					log.debug("IE浏览器，IE11需要用“rv:11”来判断");
					fileName = URLEncoder.encode(fileName, "UTF-8");
				} else {
					fileName = new String(fileName.getBytes("UTF-8"), "ISO-8859-1");
				}
				response.setHeader("Content-disposition", "attachment; filename=" + fileName);
				// 设置文件大小
				response.setHeader("Content-Length", String.valueOf(fileLength));
				bis = new BufferedInputStream(new FileInputStream(file));
				bos = new BufferedOutputStream(response.getOutputStream());
				IOUtils.copyLarge(bis, bos);
			}
		} catch (Exception e) {
			log.error("下载文件时出错", e);
			ModelAndView mv = new ModelAndView("error/fileNotFound");
			mv.addObject("message", "下载文件时出错");
			mv.addObject("success", false);
			return mv;
		} finally {
			if (bis != null)
				bis.close();
			if (bos != null)
				bos.close();
		}
		return null;
	}

	private ModelAndView errorModelAndView() {
		ModelAndView mv = new ModelAndView("error/fileNotFound");
		mv.addObject("message", "您请求的文件不存在或已被删除");
		mv.addObject("success", false);
		return mv;
	}

}
