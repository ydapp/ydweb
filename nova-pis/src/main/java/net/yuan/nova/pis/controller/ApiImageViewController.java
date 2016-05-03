package net.yuan.nova.pis.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.yuan.nova.commons.SystemConstant;
import net.yuan.nova.core.entity.Attachment;
import net.yuan.nova.core.service.AttachmentService;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ApiImageViewController {

	protected final Logger log = LoggerFactory.getLogger(ApiImageViewController.class);

	@Autowired
	protected AttachmentService attachmentService;

	@RequestMapping("/api/images")
	public ModelAndView download(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String fileId = StringUtils.trimToEmpty(request.getParameter("fileId"));
		if (StringUtils.isNotBlank(fileId)) {
			// fileId不为空，通过fileId显示图片
			return this.download(fileId, request, response);
		} else {
			log.error("附件ID为空，请确认");
			noPic(request, response);
			return null;
		}
	}

	@RequestMapping("/api/images/{fileId}")
	public ModelAndView download(@PathVariable("fileId") String fileId, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// 获得文件路径
		Attachment attachment = attachmentService.getAttachmentById(fileId);
		if (attachment == null || !Attachment.Type.P.equals(attachment.getActhType())) {
			log.info("没有附件或者附件的类型不是图片");
			noPic(request, response);
			return null;
		}
		try {
			File file = getFile(attachment);
			if (file == null || !file.exists()) {
				log.info("没有附件内容");
				noPic(request, response);
			} else {
				outFile(file, attachment.getAtchName(), request, response);
			}
		} catch (Exception e) {
			log.error("下载图片时出错", e);
			noPic(request, response);
		}
		return null;
	}

	protected File getFile(Attachment attachment) {
		String path = attachment.getSavePath();
		// 获得文件路径
		String filePath = System.getProperty(SystemConstant.WEBAPP_ROOT) + path;
		log.debug(filePath);
		File file = new File(filePath);
		if (!file.exists()) {// 判断文件目录是否存在
			file.getParentFile().mkdirs();
			file = attachmentService.transferToFile(attachment);
		}
		return file;
	}

	private void noPic(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String fileName = "no_pic.jpg";
		// 读取默认的图片
		String defaultPicPath = new StringBuffer(System.getProperty(SystemConstant.WEBAPP_ROOT))
				.append(SystemConstant.UPLOAD_FOLDER).append("/default/no_pic.jpg").toString();
		File defaultFaceFile = new File(defaultPicPath);
		if (!defaultFaceFile.exists()) {
			File dir = defaultFaceFile.getParentFile();
			if (!dir.exists()) {
				// 文件不存在，创建所在的文件夹
				dir.mkdirs();
			}
			ClassPathResource res = new ClassPathResource("no_pic.jpg", this.getClass());
			InputStream is = res.getInputStream();
			FileUtils.copyInputStreamToFile(is, defaultFaceFile);
			IOUtils.closeQuietly(is);
		}
		outFile(defaultFaceFile, fileName, request, response);
	}

	/**
	 * 该方法内会关闭传入的流对象
	 * 
	 * @param bis
	 * @param fileName
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	protected void outFile(File imageFile, String fileName, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		response.setHeader("Accept-Ranges", "bytes");
		InputStream bis = new BufferedInputStream(new FileInputStream(imageFile));
		java.io.BufferedOutputStream bos = null;
		try {
			// 设置相应类型
			response.setContentType(this.getContentType(fileName));
			// 设置文件大小
			long fileLength = imageFile.length();
			response.setHeader("Content-Length", String.valueOf(fileLength));
			bos = new BufferedOutputStream(response.getOutputStream());
			IOUtils.copyLarge(bis, bos);

		} catch (Exception e) {
			log.error("下载文件时出错：{}", e.getMessage());
			throw e;
		} finally {
			IOUtils.closeQuietly(bis);
			IOUtils.closeQuietly(bos);
		}
	}

	public String getContentType(String fileName) {
		if (StringUtils.isBlank(fileName)) {
			return "image/jpeg";
		}
		String fileExt = fileName.substring(fileName.lastIndexOf("."));
		fileExt = StringUtils.upperCase(fileExt);
		if (StringUtils.equals(fileExt, ".PNG")) {
			return "image/png";
		} else if (StringUtils.equals(fileExt, ".GIF")) {
			return "image/gif";
		} else if (StringUtils.equals(fileExt, ".BMP")) {
			return "image/bmp";
		} else {
			return "image/jpeg";
		}
	}

}
