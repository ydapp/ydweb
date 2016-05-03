package net.yuan.nova.core.exception;

/**
 * 这里定义异常编码
 * 
 * @author zhangshuai
 *
 */
public enum ExceptionCode {

	base("baseException", "Exception occurred"),
	/**
	 * 验证异常编码
	 */
	validation("validationException", "Did not pass the verification"),
	/**
	 * 用户名已存在
	 */
	userNameExists("userNameExists", "Username already exists"),
	/**
	 * 验证异常编码
	 */
	weather("weatherException", "The weather data are not available");

	private final String value;
	/**
	 * 异常的描述信息，当value值对应的国际化不存在是，使用这个描述信息
	 */
	private final String reasonPhrase;

	private ExceptionCode(String value, String reasonPhrase) {
		this.value = value;
		this.reasonPhrase = reasonPhrase;
	}

	/**
	 * Return the integer value of this exception code.
	 */
	public String value() {
		return this.value;
	}

	/**
	 * Return the reason phrase of this exception code.
	 */
	public String getReasonPhrase() {
		return reasonPhrase;
	}

	/**
	 * Return a string representation of this status code.
	 */
	@Override
	public String toString() {
		return this.value;
	}

}
