package net.yuan.nova.pis.entity.vo;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 版本信息实体类
 * 
 * @author zhangshuai
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VersionVo {

	private int status;
	private String version;
	private String title;
	private String note;
	private String url;

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
