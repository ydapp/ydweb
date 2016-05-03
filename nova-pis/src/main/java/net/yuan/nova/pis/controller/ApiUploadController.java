package net.yuan.nova.pis.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.yuan.nova.core.entity.Attachment;
import net.yuan.nova.core.service.AttachmentService;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class ApiUploadController {

	protected final Logger log = LoggerFactory.getLogger(ApiUploadController.class);

	@Autowired
	private AttachmentService attachmentService;

	/**
	 * 文件上传，允许一个或多个文件上传，当上传多个时返回数组
	 * 
	 * @param request
	 * @param response
	 * @param modelMap
	 * @param upfiles
	 * @return
	 */
	@RequestMapping(value = "/api/upload")
	public String fileUpload(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap,
			@RequestParam("file") MultipartFile[] upfiles) {
		if (upfiles != null) {
			if (upfiles.length > 1) {
				List<Object> results = new ArrayList<Object>();
				for (MultipartFile upfile : upfiles) {
					if (upfile != null && !upfile.isEmpty()) {
						String fileName = upfile.getOriginalFilename();
						this.fileUpload(request, response, modelMap, fileName, upfile);
						Object result = modelMap.get("result");
						results.add(result);
					}
				}
				modelMap.addAttribute("result", results);
			} else {
				MultipartFile upfile = upfiles[0];
				String fileName = upfile.getOriginalFilename();
				this.fileUpload(request, response, modelMap, fileName, upfile);
			}
		}
		return "fileUpload";
	}

	/**
	 * 文件上传，一次上传一个并可以指定文件名称
	 * 
	 * @param request
	 * @param response
	 * @param modelMap
	 * @param fileName
	 * @param upfile
	 * @return
	 */
	@RequestMapping(value = "/api/upload", params = "fileName")
	public String fileUpload(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap,
			@RequestParam("fileName") String fileName, @RequestParam("file") MultipartFile upfile) {

		String kindId = StringUtils.trimToEmpty(request.getParameter("kindId"));
		if (StringUtils.isBlank(kindId)) {
			kindId = "0";
		}
		Attachment.TableName kind = Attachment.TableName.NULL_TALBE;
		String kindStr = StringUtils.trimToEmpty(request.getParameter("kind"));
		if (StringUtils.isBlank(kindStr)) {
			// 如果没有kind字段则取tableName
			kindStr = StringUtils.trimToEmpty(request.getParameter("tableName"));
		}
		if (StringUtils.isNotBlank(kindStr)) {
			try {
				kind = Enum.valueOf(Attachment.TableName.class, kindStr);
			} catch (Exception e) {
				log.error("转换业务类型失败");
			}
		}
		Attachment attachment = attachmentService.addUploadFile(upfile, fileName, kindId, kind, Attachment.State.A);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("success", true);
		result.put("message", "文件“" + fileName + "”上传成功");
		result.put("id", attachment.getAppAtchId());
		result.put("filePath", attachment.getSavePath());
		result.put("src", request.getContextPath() + "/" + attachment.servletPath());
		modelMap.addAttribute("result", result);
		return "fileUpload";
	}

	@PostConstruct
	public void cleanAttachment() {
		log.debug("clean attachment");
		attachmentService.cleanAttachment(Attachment.TableName.NULL_TALBE);
	}

}
