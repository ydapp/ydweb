package net.yuan.nova.pis.service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import net.yuan.nova.core.entity.Attachment;
import net.yuan.nova.core.service.AttachmentService;
import net.yuan.nova.pis.dao.PisArticleMapper;
import net.yuan.nova.pis.entity.PisArticle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.github.pagehelper.PageHelper;

@Service
public class PisArticleService {

	protected final Logger log = LoggerFactory.getLogger(PisArticleService.class);

	@Autowired
	private PisArticleMapper pisArticleMapper;

	@Autowired
	private AttachmentService attachmentService;

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

	public PisArticle selectByPrimaryKey(String id) {
		return pisArticleMapper.selectByPrimaryKey(id);
	}
	public List<PisArticle> getCustomers(int page, int pageSize) {
		PageHelper.startPage(page, pageSize);
		return pisArticleMapper.selectArticleList();
	} 
}
