package net.yuan.nova.core.vo;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class JsonVo<T> {
	/**
	 * 结果
	 */
	private boolean success;
	/**
	 * 成功的消息
	 */
	private String message;

	/**
	 * 具体每个输入错误的消息
	 */
	private Map<String, String> errors;

	/**
	 * 返回的数据
	 */
	private T result;

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setErrors(Map<String, String> errors) {
		this.errors = errors;
	}

	public void putError(String key, String error) {
		if (errors == null) {
			errors = new HashMap<String, String>();
		}
		if (StringUtils.isBlank(this.message)) {
			this.message = error;
		}
		errors.put(key, error);
	}

	public T getResult() {
		return result;
	}

	public void setResult(T result) {
		this.result = result;
	}

	/**
	 * 
	 * 返回true表示通过验证
	 * 
	 * @return
	 */
	public boolean validate() {
		if (this.errors != null && this.errors.size() > 0) {
			this.setSuccess(false);
			if (StringUtils.isBlank(this.getMessage())) {
				Map<String, String> es = this.errors;
				Collection<String> vs = es.values();
				for (Iterator<String> iterator = vs.iterator(); iterator.hasNext();) {
					String msg = (String) iterator.next();
					if (StringUtils.isNotBlank(msg)) {
						this.setMessage(msg);
						break;
					}
				}
			}
			return false;
		} else {
			this.setSuccess(true);
			this.setMessage(null);
			return true;
		}
	}
}
