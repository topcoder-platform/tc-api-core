package com.appirio.tech.core.api.v3.response;

import org.eclipse.jetty.http.HttpStatus;


public class ExceptionContent {
	private String name;
	private String message;
	private int httpStatus = HttpStatus.INTERNAL_SERVER_ERROR_500;
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

	public void setHttpStatus(int status) {
		this.httpStatus = status;
	}
	
	public int getHttpStatus() {
		return httpStatus;
	}
}