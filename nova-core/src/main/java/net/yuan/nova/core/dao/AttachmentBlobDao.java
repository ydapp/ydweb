package net.yuan.nova.core.dao;

import net.yuan.nova.core.entity.AttachmentBlob;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AttachmentBlobDao {

	// ///////////////////////////////
	// ///// 增加 ////////
	// ///////////////////////////////

	/**
	 * @param att
	 * @return
	 */
	public int addAttachmentBlob(AttachmentBlob att);

	// ///////////////////////////////
	// ///// 删除 ////////
	// ///////////////////////////////

	/**
	 * @param appAtchId
	 * @return
	 */
	public int deleteAttachmentBlob(@Param("atchMd5") String atchMd5);

	// ///////////////////////////////
	// ///// 查询////////
	// ///////////////////////////////

	/**
	 * 根据md5值获得附件
	 * 
	 * @param atchMd5
	 *            文件的md5值
	 * @param acthSize
	 *            文件大小
	 * @return
	 */
	public AttachmentBlob findAttachmentBlob(@Param("atchMd5") String atchMd5);

	/**
	 * 根据md5值获得附件数量
	 * 
	 * @param atchMd5
	 *            文件的md5值
	 * @param acthSize
	 *            文件大小
	 * @return
	 */
	public int findAttachmentBlobCount(@Param("atchMd5") String atchMd5);

	/**
	 * 清除在主表中不存在的数据
	 */
	public void cleanAttachmentBlob();
}
