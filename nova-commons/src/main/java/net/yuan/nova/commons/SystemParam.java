package net.yuan.nova.commons;

import java.io.Serializable;

public class SystemParam implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long param;
	private String paramName;
	private String mask;
	private String currentValue;
	private String comments;

	public Long getParam() {
		return param;
	}

	public void setParam(Long param) {
		this.param = param;
	}

	public String getParamName() {
		return paramName;
	}

	public void setParamName(String paramName) {
		this.paramName = paramName;
	}

	public String getMask() {
		return mask;
	}

	public void setMask(String mask) {
		this.mask = mask;
	}

	public String getCurrentValue() {
		return currentValue;
	}

	public void setCurrentValue(String currentValue) {
		this.currentValue = currentValue;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

}
