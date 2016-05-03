package net.yuan.nova.core.service;

import net.yuan.nova.core.dao.AttachmentBlobDao;
import net.yuan.nova.core.entity.AttachmentBlob;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class AttachmentBlobService {

	protected final Logger log = LoggerFactory.getLogger(AttachmentBlobService.class);

	@Autowired
	private AttachmentBlobDao attachmentBlobDao;

	/**
	 * 上传附件
	 * 
	 * @param atchMd5
	 * @param acthSize
	 * @param fileBytes
	 */
	@Async
	public void addUploadFile(String atchMd5, long acthSize, byte[] fileBytes) {
		log.debug("开始保存附件");
		AttachmentBlob attachmentBlob = new AttachmentBlob();
		attachmentBlob.setAtchMd5(atchMd5);
		attachmentBlob.setActhSize(acthSize);
		attachmentBlob.setAtchCont(fileBytes);
		addUploadFile(attachmentBlob);
		log.debug("保存附件结束");
	}

	/**
	 * 上传附件
	 * 
	 * @param attachmentBlob
	 */
	@Async
	public void addUploadFile(AttachmentBlob attachmentBlob) {
		int number = attachmentBlobDao.addAttachmentBlob(attachmentBlob);
		if (log.isDebugEnabled())
			log.debug("保存完成，共保存数据：" + number + "条");
	}

	/**
	 * 根据MD5值获得附件
	 * 
	 * @param atchMd5
	 * @return
	 */
	public AttachmentBlob findAttachmentBlob(String atchMd5) {
		return attachmentBlobDao.findAttachmentBlob(atchMd5);
	}

	/**
	 * 判断附件文件在数据库中是否存在
	 * 
	 * @param atchMd5
	 * @return
	 */
	public boolean exists(String atchMd5) {
		int count = attachmentBlobDao.findAttachmentBlobCount(atchMd5);
		return count > 0;
	}

	/**
	 * 清除在主表中不存在的数据
	 */
	public void cleanAttachmentBlob() {
		attachmentBlobDao.cleanAttachmentBlob();
	}

}
