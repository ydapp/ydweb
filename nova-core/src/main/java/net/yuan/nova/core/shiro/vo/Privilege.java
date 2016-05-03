package net.yuan.nova.core.shiro.vo;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Privilege {
	/**
	 * 系统权限实体类
	 */
	private String privId;
	private String privType;
	private String privName;
	private String comments;

	/**
	 * @return the privId
	 */
	public String getPrivId() {
		return privId;
	}

	/**
	 * @param privId
	 *            the privId to set
	 */
	public void setPrivId(String privId) {
		this.privId = privId;
	}

	/**
	 * @return the privType
	 */
	public String getPrivType() {
		return privType;
	}

	/**
	 * @param privType
	 *            the privType to set
	 */
	public void setPrivType(String privType) {
		this.privType = privType;
	}

	/**
	 * @return the privName
	 */
	public String getPrivName() {
		return privName;
	}

	/**
	 * @param privName
	 *            the privName to set
	 */
	public void setPrivName(String privName) {
		this.privName = privName;
	}

	/**
	 * @return the comments
	 */
	public String getComments() {
		return comments;
	}

	/**
	 * @param comments
	 *            the comments to set
	 */
	public void setComments(String comments) {
		this.comments = comments;
	}

}
