package net.yuan.nova.pis.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.coobird.thumbnailator.Thumbnails;
import net.yuan.nova.commons.HttpUtils;
import net.yuan.nova.commons.SystemConstant;
import net.yuan.nova.core.entity.Attachment;
import net.yuan.nova.core.entity.AttachmentBlob;
import net.yuan.nova.core.service.AttachmentBlobService;
import net.yuan.nova.core.service.AttachmentService;
import net.yuan.nova.core.vo.DataGridData;
import net.yuan.nova.core.vo.JsonVo;
import net.yuan.nova.pis.entity.PisArticle;
import net.yuan.nova.pis.entity.vo.PisArticleVo;
import net.yuan.nova.pis.pagination.DataGridHepler;
import net.yuan.nova.pis.pagination.PageParam;
import net.yuan.nova.pis.service.PisArticleService;
import net.yuan.nova.pis.service.TemplateService;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.github.pagehelper.PageInfo;

@Controller
public class ArticleController {

	protected final Logger log = LoggerFactory.getLogger(ArticleController.class);

	@Autowired
	private PisArticleService pisArticleService;
	@Autowired
	private AttachmentService attachmentService;
	@Autowired
	private TemplateService templateService;
	@Autowired
	private AttachmentBlobService attachmentBlobService;
	
	
	
	
	

	/**
	 * 插入数据
	 * 
	 * @param record
	 * @return
	 */
	@RequestMapping("/admin/article/add")
	public Object insertSelective(HttpServletRequest request, ModelMap modelMap) {
		String tile = request.getParameter("tile");
		String content = request.getParameter("editorValue");
		JsonVo<Object> json = new JsonVo<Object>();
		MultipartFile file = null;
		MultipartHttpServletRequest multipartRequest = null;
		// 转型为MultipartHttpRequest，如果没有上传图片是会报出异常：ClassCastException
		try {
			multipartRequest = (MultipartHttpServletRequest) request;
			file = multipartRequest.getFile("cover");
		} catch (ClassCastException cce) {
			log.error("没有上传图片", cce);
		}
		if (file == null) {
			System.out.println("没有附件上传");
		}
		if (StringUtils.isBlank(tile)) {
			json.putError("tile", "资讯标题不能为空");
		}
		if (StringUtils.isBlank(content)) {
			json.putError("content", "资讯内容不能为空");
		}
		if (json.validate()) {
			pisArticleService.addArticle(tile, content, file);
		}
		return json;
	}
	/**
	 * 修改数据
	 * @return
	 */
	@RequestMapping("/admin/article/update")
	public Object updateSelective(HttpServletRequest request, ModelMap modelMap){
		String id = request.getParameter("id");
		String tile = request.getParameter("tile");
		String  cover = request.getParameter("cover");
		String content = request.getParameter("editorValue");
		String path = "http://"+request.getLocalAddr()+":"+request.getLocalPort();
		if(!"".equals(content)){
			content = content.replaceAll(path, "");
		}
		JsonVo<Object> json = new JsonVo<Object>();
		MultipartFile file = null;
		MultipartHttpServletRequest multipartRequest = null;
		// 转型为MultipartHttpRequest，如果没有上传图片是会报出异常：ClassCastException
		try {
			multipartRequest = (MultipartHttpServletRequest) request;
			file = multipartRequest.getFile("cover");
		} catch (ClassCastException cce) {
			log.error("没有上传图片", cce);
		}
		if (null!=file&&file.getSize()==0) {
			System.out.println("没有附件上传");
		}
		if (StringUtils.isBlank(tile)) {
			json.putError("tile", "资讯标题不能为空");
		}
		if (StringUtils.isBlank(content)) {
			json.putError("content", "资讯内容不能为空");
		}
		if (json.validate()) {
			pisArticleService.updateAricle(id,tile, content,cover, file);
			json.setSuccess(true);
			json.setMessage("修改成功");
		}
		return json;
	}

	@RequestMapping("/admin/articles")
	public Object selectArticleList(HttpServletRequest request, ModelMap modelMap, HttpServletResponse response) {
		PageParam param = DataGridHepler.parseRequest(request);
		List<PisArticle> list = this.pisArticleService.getCustomers(param.getPage(), param.getPageSize());
		return DataGridHepler.addDataGrid(list, modelMap); 
	}

	@RequestMapping("/api/articles")
	public Object articleList(HttpServletRequest request, ModelMap modelMap) {
		int page = NumberUtils.toInt(request.getParameter("page"),1);
		int pageSize = NumberUtils.toInt(request.getParameter("pageSize"), 10);
		DataGridData<PisArticleVo> dgd = new DataGridData<PisArticleVo>();
		List<PisArticle> selectArticleList = pisArticleService.selectArticleList(page, pageSize);
		long total = new PageInfo(selectArticleList).getTotal();
		List<PisArticleVo> pisArticleVoList = new ArrayList<PisArticleVo>();
		for (PisArticle pisArticle : selectArticleList) {
			
			PisArticleVo pisArticleVo = new PisArticleVo();
			try {
				BeanUtils.copyProperties(pisArticleVo, pisArticle);
			} catch (Exception e) {
				log.error("拷贝数据出错", e);
			}
			//这里暂时不需要在列表中显示图片，因此暂时屏蔽图片关联部分
//			String cover = pisArticle.getCover();
//			Attachment attachment = attachmentService.getAttachmentById(cover);
//			String filePath = null;
//			if (attachment != null) {
//				try {
//					filePath = attachmentService.thumbnailator(attachment, 42);
//				} catch (IOException e) {
//					log.error("生成缩略图失败", e);
//					filePath = attachment.getSavePath();
//				}
//			}
//			pisArticleVo.setFilePath(filePath);
			pisArticleVoList.add(pisArticleVo);
		}
		dgd.setRows(pisArticleVoList);
		dgd.setTotal(total);
		return dgd;
	}

	/**
	 * 资讯详情
	 * 
	 * @param id
	 * @param request
	 * @param modelMap
	 * @return
	 */
	@RequestMapping("/api/article/{id}")
	public ModelAndView articleDetail(@PathVariable String id, HttpServletRequest request, ModelAndView modelAndView) {
		PisArticle pisArticle = pisArticleService.selectByPrimaryKey(id);
		if (pisArticle == null) {
			modelAndView.setViewName(templateService.getErrorTemplate(404));
			return modelAndView;
		}
		String [] array = pisArticle.getContent().split("</p>");
		int start = 0;
		int end =0;
		String src="";
		String src_1="";
		String content="";
		StringBuffer sb = new StringBuffer();
		for(int i =0;i<array.length;i++){
			if(array[i].indexOf("src")!=-1){
				 start =array[i].indexOf("src");
				 end = array[i].lastIndexOf("/");
				 src = array[i].substring(start, end);
				 src_1 =src.replace("src=","").replaceAll("\"","");
				 content = "src="+"\""+"http://"+request.getLocalAddr()+":"+request.getLocalPort()+src_1+"\"";
				 array[i] = array[i].replaceAll(src, content);
				 array[i] = array[i]+"</p>";
			}
			 sb.append(array[i]);
		}
		pisArticle.setContent(sb.toString());
		PisArticleVo pisArticleVo = new PisArticleVo();
		try {
			BeanUtils.copyProperties(pisArticleVo, pisArticle);
		} catch (Exception e) {
			log.error("拷贝数据出错", e);
		}
		String viewWidthStr = request.getParameter("viewWidth");
		int width = NumberUtils.toInt(viewWidthStr, 0);
		// 对图片进行相应的压缩
		String cover = pisArticle.getCover();
		if(null != cover){
			Attachment attachment = attachmentService.getAttachmentById(cover);
			if (attachment != null) {
				String filePath = attachment.getSavePath();
				if (width > 10) {
					try {
						filePath = attachmentService.thumbnailator(attachment, width - 10);
					} catch (IOException e) {
						log.error("生成缩略图失败", e);
					}
				}
				// 获得请求图片的完整地址
				String basePath = HttpUtils.getBasePath(request);
				pisArticleVo.setFilePath(basePath + "/" + filePath);
			}
		}
		modelAndView.addObject("pisArticleVo", pisArticleVo);
		modelAndView.setViewName("ydapp/infoDetail");
		return modelAndView;
	}

	/**
	 * 资讯详情
	 * 
	 * @param id
	 * @param request
	 * @param modelMap
	 * @return
	 */
	@RequestMapping("/api/article")
	public ModelAndView articleDetail(HttpServletRequest request, ModelAndView modelAndView) {
		String id = request.getParameter("id");
		return articleDetail(id, request, modelAndView);
	}
	/**
	 * 执行删除操作
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping("/admin/article/remove")
	public JsonVo removeArticle(HttpServletRequest request, HttpServletResponse response){
		JsonVo json = new JsonVo();
		//获取主键ID
		String id=request.getParameter("id");
		//获取与附件照片表关联主键
		String cover = request.getParameter("cover");
		//执行删除操作
		boolean flag = this.pisArticleService.deleteByPrimaryKey(id,cover);
		//设置返回值
		json.setSuccess(flag);
		return json;
	}
	
	/**
	 * 获取资讯横幅
	 * @param request
	 * @param modelAndView
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping("/admin/article/getInfoBanner")
	public JsonVo getInfoBanner(HttpServletRequest request, HttpServletResponse response){
		List<Attachment> list = attachmentService.getAttachmentsByKindId("articleBanner", Attachment.TableName.PIS_ARTICLE, Attachment.State.A);
		Attachment attachment = null;
		JsonVo json = new JsonVo();
		if(null != list && list.size()>0){
			attachment = list.get(0);
			File newFile = new File(System.getProperty(SystemConstant.WEBAPP_ROOT) + attachment.getSavePath());
			if (!newFile.exists()) {
				String savePath = attachment.getSavePath();
				String path = System.getProperty(SystemConstant.WEBAPP_ROOT) + savePath;
				File file = new File(path);
				if (!file.exists()) {
					file.getParentFile().mkdirs();
					file = attachmentService.transferToFile(attachment);
				}
				if(null != file){
					String filePath = System.getProperty(SystemConstant.WEBAPP_ROOT) + attachment.getSavePath();
					try {
						Thumbnails.of(file).scale(9f).toFile(new File(filePath));
						//Thumbnails.of(file).size(386, 161).toFile(new File(filePath));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		json.setResult(attachment);;
		return json;
	} 
	/**
	 * 修改资讯横幅（app）
	 * @param request
	 * @param modelMap
	 * @return
	 */
	@RequestMapping("/admin/article/updateBanner")
	public Object updateBanner(HttpServletRequest request, ModelMap modelMap){
		MultipartFile file = null;
		MultipartHttpServletRequest multipartRequest = null;
		JsonVo<Object> json = new JsonVo<Object>();
		try {
			multipartRequest = (MultipartHttpServletRequest) request;
			file = multipartRequest.getFile("banner");
		} catch (ClassCastException cce) {
			log.error("没有上传图片", cce);
		}
		if (null == file) {
			json.putError("photo","没有图片上传");
		}
		List<Attachment> list = attachmentService.getAttachmentsByKindId("articleBanner", Attachment.TableName.PIS_ARTICLE, Attachment.State.A);
		if(null != list && list.size()>0){
			for(Attachment attachment:list){
				 attachmentService.deleteAttachment(attachment.getAppAtchId());
			}
		}
		Attachment attachment = attachmentService.addUploadFile(file,file.getOriginalFilename(),"articleBanner", Attachment.TableName.PIS_ARTICLE);
		if(null !=attachment ){
			json.setSuccess(true);
			json.setMessage("修改成功");
		}
		return json;
	}

}
