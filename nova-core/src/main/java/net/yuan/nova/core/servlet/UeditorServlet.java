package net.yuan.nova.core.servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;
import net.yuan.nova.commons.SystemConstant;
import net.yuan.nova.core.entity.Attachment;
import net.yuan.nova.core.service.AttachmentService;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.baidu.ueditor.ActionEnter;
import com.baidu.ueditor.define.ActionMap;

public class UeditorServlet extends HttpServlet {

	protected final Logger log = LoggerFactory.getLogger(UeditorServlet.class);

	private static final long serialVersionUID = 1L;

	private AttachmentService attachmentService;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doPost(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		request.setCharacterEncoding("utf-8");
		response.setHeader("Content-Type", "text/html");
		String actionType = request.getParameter("action");
		String rootPath = request.getSession().getServletContext().getRealPath("/");
		PrintWriter out = response.getWriter();
		String msg = new ActionEnter(request, rootPath).exec();
		log.debug(msg);
		if (actionType != null) {
			int actionCode = ActionMap.getType(actionType);
			switch (actionCode) {
			case ActionMap.UPLOAD_IMAGE:
			case ActionMap.UPLOAD_SCRAWL:
			case ActionMap.UPLOAD_VIDEO:
			case ActionMap.UPLOAD_FILE:
				// 调用调用上传
				JSONObject jsonObject = JSONObject.fromObject(msg);
				String url = jsonObject.getString("url");
				String original = jsonObject.getString("original");
				// 将上传的附件转存如当前系统的附件管理中
				File tmpFile = new File(System.getProperty(SystemConstant.WEBAPP_ROOT) + url);
				AttachmentService as = getAttachmentService();
				// 获得归宿的业务主表，默认为文章
				Attachment.TableName tableName = Attachment.TableName.PIS_ARTICLE;
				String kindStr = StringUtils.trimToEmpty(request.getParameter("kind"));
				if (StringUtils.isBlank(kindStr)) {
					// 如果没有kind字段则取tableName
					kindStr = StringUtils.trimToEmpty(request.getParameter("tableName"));
				}
				if (StringUtils.isNotBlank(kindStr)) {
					try {
						tableName = Enum.valueOf(Attachment.TableName.class, kindStr);
					} catch (Exception e) {
						log.error("转换业务类型失败");
					}
				}
				String kindId = StringUtils.trimToEmpty(request.getParameter("kindId"));
				if (StringUtils.isBlank(kindId)) {
					kindId = "0";
				}
				Attachment attachment = as.addUploadFile(tmpFile, original, kindId, tableName, Attachment.State.A);
				jsonObject.put("url", request.getContextPath() + "/" + attachment.servletPath());
				jsonObject.put("id", attachment.getAppAtchId());
				msg = jsonObject.toString();
				break;
			}
		}
		out.write(msg);
		out.flush();
		out.close();
	}

	public AttachmentService getAttachmentService() {
		if (attachmentService == null) {
			WebApplicationContext webApplicationContext = WebApplicationContextUtils
					.getWebApplicationContext(getServletContext());
			attachmentService = (AttachmentService) webApplicationContext.getBean("attachmentService");
		}
		return attachmentService;
	}

}
