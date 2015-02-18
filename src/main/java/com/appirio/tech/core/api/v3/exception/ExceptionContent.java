package com.appirio.tech.core.api.v3.exception;

import org.springframework.http.HttpStatus;


public class ExceptionContent {
	private String name;
	private String message;
	private HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
	private String redirectUrl;
	
	public ExceptionContent(Throwable e) {
		this.name = e.getClass().getSimpleName();
		this.message = e.getMessage();
	}
	
	public ExceptionContent(Throwable e, String redirectUrl) {
		this.name = e.getClass().getSimpleName();
		this.message = e.getMessage();
		this.redirectUrl = redirectUrl;
	}
	
	public String getName() {
		return name;
	}
	public String getMessage() {
		return message;
	}
	
	public String getRedirectUrl() {
		return redirectUrl;
	}

	public void setHttpStatus(HttpStatus status) {
		this.httpStatus = status;
	}
	
	public HttpStatus getHttpStatus() {
		return httpStatus;
	}
}