package net.yuan.nova.core.entity;

import java.io.Serializable;

import org.apache.commons.beanutils.BeanUtils;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AttachmentVo extends Attachment implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String src;
	private String thumbnailSrc;

	public AttachmentVo() {

	}

	public AttachmentVo(Attachment attachment) {
		try {
			BeanUtils.copyProperties(this, attachment);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getSrc() {
		return src;
	}

	public void setSrc(String src) {
		this.src = src;
	}

	public String getThumbnailSrc() {
		return thumbnailSrc;
	}

	public void setThumbnailSrc(String thumbnailSrc) {
		this.thumbnailSrc = thumbnailSrc;
	}

}
