package com.ks3.exception;

import com.ks3.exception.ExceptionConstants.ErrorCode;

public class RedisErrorException extends RuntimeException {
	
private static final long serialVersionUID = -1428157373082246084L;
	
	private ErrorCode errorCode;

	private String errorMessage;
	
	public RedisErrorException(String msg) {
		super(msg);
	}
	
	public RedisErrorException(String msg, Throwable e) {
		super(msg, e);
	}
	
	public RedisErrorException(ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
	}

	public RedisErrorException(ErrorCode errorCode,String errorMessage) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}
	
	public ErrorCode getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(ErrorCode errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
}
