package net.yuan.nova.core.exception;

/**
 * 
 * 数据验证异常
 * 
 * @author
 * 
 */
public class ValidationException extends BaseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ValidationException() {
		super(ExceptionCode.validation);
	}
}
