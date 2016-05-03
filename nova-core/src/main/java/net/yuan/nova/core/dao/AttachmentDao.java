package net.yuan.nova.core.dao;

import java.util.List;

import net.yuan.nova.core.entity.Attachment;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AttachmentDao {

	// ///////////////////////////////
	// ///// 增加 ////////
	// ///////////////////////////////

	/**
	 * @param att
	 * @return
	 */
	public int addAttachment(Attachment att);

	// ///////////////////////////////
	// ///// 删除 ////////
	// ///////////////////////////////

	/**
	 * @param appAtchId
	 * @return
	 */
	public int deleteAttachment(@Param("appAtchId") String appAtchId);

	/**
	 * @param kindId
	 * @param tableName
	 */
	public void deleteAttachmentsByKindId(@Param("kindId") String kindId,
			@Param("tableName") Attachment.TableName tableName);

	// ///////////////////////////////
	// ///// 修改////////
	// ///////////////////////////////

	/**
	 * @param appAtchId
	 * @param state
	 */
	public void updateStatusByAttachmentId(@Param("appAtchId") String appAtchId, @Param("state") Attachment.State state);

	public int updateDescriptionByAttachmentId(@Param("appAtchId") String appAtchId, @Param("comments") String comments,
			@Param("atchName") String atchName);

	public int updateAttachment(Attachment attachment);

	// ///////////////////////////////
	// ///// 查询////////
	// ///////////////////////////////

	/**
	 * @param appAtchId
	 * @return
	 */
	public Attachment getAttachmentById(@Param("appAtchId") String appAtchId);

	/**
	 * @param kindId
	 * @param tableName
	 * @param state
	 * @return
	 */
	public List<Attachment> getAttachmentsByKindId(@Param("kindId") String kindId,
			@Param("tableName") Attachment.TableName tableName, @Param("state") Attachment.State state);

	/**
	 * @param kindId
	 * @param tableName
	 * @param state
	 * @return
	 */
	public int getAttachmentCountByKindId(@Param("kindId") String kindId,
			@Param("tableName") Attachment.TableName tableName, @Param("state") Attachment.State state);

	public int getAttachmentCountByKind(@Param("tableName") Attachment.TableName tableName,
			@Param("state") Attachment.State state);

	/**
	 * 获得附件列表，附件id在appAtchId之后
	 * 
	 * @param offset
	 * @param endRowNum
	 * @param appAtchId
	 * @return
	 */
	public List<Attachment> getAttachmentList(@Param("offset") int offset, @Param("endRowNum") int endRowNum,
			@Param("appAtchId") String appAtchId);

	/**
	 * 清理无效的附件
	 */
	public void cleanAttachment(@Param("tableName") Attachment.TableName tableName);

}
