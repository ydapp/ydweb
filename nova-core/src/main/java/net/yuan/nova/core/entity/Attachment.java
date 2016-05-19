package net.yuan.nova.core.entity;

import java.io.Serializable;
import java.util.Date;

public class Attachment implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String appAtchId;// APP_ATCH_ID
	private TableName tableName;
	private String kindId;
	private String atchName;
	private String savePath;
	private String comments;
	private Type acthType;
	private String md5;
	private Long acthSize;
	private State state;
	private Date createDate;

	public String getAppAtchId() {
		return appAtchId;
	}

	public void setAppAtchId(String appAtchId) {
		this.appAtchId = appAtchId;
	}

	public TableName getTableName() {
		return tableName;
	}

	public void setTableName(TableName tableName) {
		this.tableName = tableName;
	}

	public String getKindId() {
		return kindId;
	}

	public void setKindId(String kindId) {
		this.kindId = kindId;
	}

	public String getAtchName() {
		return atchName;
	}

	public void setAtchName(String atchName) {
		this.atchName = atchName;
	}

	public String getSavePath() {
		return savePath;
	}

	public void setSavePath(String savePath) {
		this.savePath = savePath;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public Type getActhType() {
		return acthType;
	}

	public void setActhType(Type acthType) {
		this.acthType = acthType;
	}

	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}

	public Long getActhSize() {
		return acthSize;
	}

	public void setActhSize(Long acthSize) {
		this.acthSize = acthSize;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	/**
	 * 类型<br>
	 * photo：照片<br>
	 * file：文件<br>
	 * 
	 * @author
	 * 
	 */
	public static enum Type {
		/**
		 * 相册:photo
		 */
		P, /**
		 * 文件:file
		 */
		F,
		/**
		 * 音频:audio
		 */
		A,
		/**
		 * 视频:video
		 */
		V,
		/**
		 * 其他类型:other
		 */
		O
	}

	/**
	 * 种类
	 * 
	 * @author
	 * 
	 */
	public static enum TableName {
		NULL_TALBE,PIS_ARTICLE, PIS_PROPERTY,PIS_USER,PIS_USER_INFO
	}

	public static enum State {
		/**
		 * 可用状态
		 */
		A, /**
		 * 不可用状态
		 */
		X
	}

	/**
	 * 文件允许格式
	 */
	public static String[] FILE_TYPE = { ".rar", ".zip", ".doc", ".docx", ".xls", ".xlsx", ".ppt", ".pptx", ".pdf",
			".txt" };

	/**
	 * 图片允许格式
	 */
	public static String[] PHOTO_TYPE = { ".gif", ".png", ".jpg", ".jpeg", ".bmp" };

	/**
	 * 视频允许格式
	 */
	public static String[] VIDEO_TYPE = { ".flv", ".swf", ".wmv", ".avi", ".rm", ".rmvb", ".mpeg", ".mpg", ".ogg",
			".mov", ".mp4" };

	/**
	 * 音频允许格式
	 */
	public static String[] AUDIO_TYPE = { ".mp3", ".wav" };

	/**
	 * 流程定义格式
	 */
	public static String[] WORKFLOW_TYPE = { ".bpmn20.xml", ".bpmn" };

	public static boolean isFileType(String fileName, String[] typeArray) {
		for (String type : typeArray) {
			if (fileName.toLowerCase().endsWith(type)) {
				return true;
			}
		}
		return false;
	}

	public String servletPath() {
		if (Attachment.isFileType(this.atchName, Attachment.PHOTO_TYPE)) {
			return "api/images/" + getAppAtchId() + getFileExt();
		} else {
			return "api/download/" + getAppAtchId() + getFileExt();
		}
	}

	/**
	 * 获取文件扩展名
	 * 
	 * @return string
	 */
	private String getFileExt() {
		return atchName.substring(atchName.lastIndexOf("."));
	}
}
