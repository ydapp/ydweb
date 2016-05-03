package net.yuan.nova.core.exception;

/**
 * 
 * 基础异常类，根据传入的异常编码，确定具体异常
 * 
 * @author
 * 
 */
public class BaseException extends Exception {

	private ExceptionCode code;

	private static final long serialVersionUID = 1L;

	protected BaseException() {
		super(ExceptionCode.base.getReasonPhrase());
		this.code = ExceptionCode.base;
	}

	protected BaseException(String message) {
		super(message);
		this.code = ExceptionCode.base;
	}

	protected BaseException(String message, Throwable cause) {
		super(message, cause);
		this.code = ExceptionCode.base;
	}

	public BaseException(ExceptionCode code) {
		super(code.getReasonPhrase());
		this.code = code;
	}

	public BaseException(ExceptionCode code, String message) {
		super(message);
		this.code = code;
	}

	public BaseException(ExceptionCode code, Throwable cause) {
		super(code.getReasonPhrase(), cause);
		this.code = code;
	}

	public BaseException(ExceptionCode code, String message, Throwable cause) {
		super(message, cause);
		this.code = code;
	}

	public ExceptionCode getCode() {
		return code;
	}

}
