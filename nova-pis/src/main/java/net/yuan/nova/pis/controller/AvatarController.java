package net.yuan.nova.pis.controller;

import java.io.File;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.yuan.nova.commons.SystemConstant;
import net.yuan.nova.core.entity.Attachment;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * 用户头像接口
 * 
 * @author zhangshuai
 *
 */
@Controller
public class AvatarController extends ApiImageViewController {
	
	@RequestMapping("/api/avatar")
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

	@RequestMapping("/api/avatar/{fileId}")
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

	private void noPic(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String fileName = "user.png";
		// 读取默认的图片
		String defaultPicPath = new StringBuffer(System.getProperty(SystemConstant.WEBAPP_ROOT))
				.append(SystemConstant.UPLOAD_FOLDER).append("/default/").append(fileName).toString();
		File defaultFaceFile = new File(defaultPicPath);
		if (!defaultFaceFile.exists()) {
			File dir = defaultFaceFile.getParentFile();
			if (!dir.exists()) {
				// 文件不存在，创建所在的文件夹
				dir.mkdirs();
			}
			ClassPathResource res = new ClassPathResource(fileName, this.getClass());
			InputStream is = res.getInputStream();
			FileUtils.copyInputStreamToFile(is, defaultFaceFile);
			IOUtils.closeQuietly(is);
		}
		outFile(defaultFaceFile, fileName, request, response);
	}
}
