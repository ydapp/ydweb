package net.yuan.nova.pis.service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import net.yuan.nova.core.entity.Attachment;
import net.yuan.nova.core.service.AttachmentBlobService;
import net.yuan.nova.core.service.AttachmentService;
import net.yuan.nova.pis.dao.PisArticleMapper;
import net.yuan.nova.pis.entity.PisArticle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;

import com.github.pagehelper.PageHelper;

@Service
public class PisArticleService {

	protected final Logger log = LoggerFactory.getLogger(PisArticleService.class);

	@Autowired
	private PisArticleMapper pisArticleMapper;

	@Autowired
	private AttachmentService attachmentService;
	
	@Autowired
	private AttachmentBlobService attachmentBlobService;

	/**
	 * 插入数据
	 * 
	 * @param record
	 * @return
	 */
	public int insertSelective(PisArticle record) {
		if (record != null) {
			return pisArticleMapper.insertSelective(record);
		}
		return 0;
	}

	public List<PisArticle> selectArticleList(int page, int pageSize) {
		PageHelper.startPage(page, pageSize);
		return pisArticleMapper.selectArticleList();
	}

	public int addArticle(String tile, String content, MultipartFile file) {
		PisArticle article = new PisArticle();
		article.setId(UUID.randomUUID().toString());
		article.setTile(tile);
		article.setContent(content);
		article.setPubTime(new Date());
		Attachment attachment = attachmentService
				.addUploadFile(file, article.getId(), Attachment.TableName.PIS_ARTICLE);
		article.setCover(attachment.getAppAtchId());
		return this.insertSelective(article);
	}
	
	public int updateAricle(String id,String tile,String content,String cover,MultipartFile file){
		PisArticle article = new PisArticle();
		article.setTile(tile);
		article.setContent(content);
		article.setId(id);
		if(null!=file&&file.getSize()>0){
			attachmentService.deleteAttachment(cover);
			attachmentBlobService.cleanAttachmentBlob();
			Attachment attachment = attachmentService
					.addUploadFile(file, article.getId(), Attachment.TableName.PIS_ARTICLE);
					article.setCover(attachment.getAppAtchId());
		}
		return this.updateByPrimaryKeySelective(article);
	}

	public PisArticle selectByPrimaryKey(String id) {
		return pisArticleMapper.selectByPrimaryKey(id);
	}
	public List<PisArticle> getCustomers(int page, int pageSize) {
		PageHelper.startPage(page, pageSize);
		return pisArticleMapper.selectArticleList();
	} 
	public int updateByPrimaryKeySelective(PisArticle record){
		return this.pisArticleMapper.updateByPrimaryKeySelective(record);
	}
	
	/**
	 * 执行删除资讯信息
	 * @return true:操作成功,false:操作失败
	 */
	@Transactional(rollbackFor = { Exception.class })  
	public boolean deleteByPrimaryKey(String id,String appAtchId ){
		//删除资讯信息
		int ret_01 = this.pisArticleMapper.deleteByPrimaryKey(id);
		if(ret_01<1){
			log.debug("删除资讯关联信息异常");
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return false;
		}
		//删除资讯信息关联信息
		int ret_02 = this.attachmentService.deleteAttachment(appAtchId);
		if(ret_02<0){
			log.debug("删除资讯关联信息异常");
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return false;
		}
		//删除资讯信息照片表
		attachmentBlobService.cleanAttachmentBlob();
		return true;
	}
}
