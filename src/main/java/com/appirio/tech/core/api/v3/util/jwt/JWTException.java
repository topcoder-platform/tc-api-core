package com.appirio.tech.core.api.v3.util.jwt;

@SuppressWarnings("serial")
public class JWTException extends RuntimeException {

	private String token;
	
	public JWTException(String token) {
		this.token = token;
	}

	public JWTException(String token, String message) {
		super(message);
		this.token = token;
	}

	public JWTException(String token, Throwable cause) {
		super(cause);
		this.token = token;
	}

	public JWTException(String token, String message, Throwable cause) {
		super(message, cause);
		this.token = token;
	}

	public String getToken() {
		return token;
	}

	protected void setToken(String token) {
		this.token = token;
	}
}
