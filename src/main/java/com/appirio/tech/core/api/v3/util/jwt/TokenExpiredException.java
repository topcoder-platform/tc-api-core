package com.appirio.tech.core.api.v3.util.jwt;

@SuppressWarnings("serial")
public class TokenExpiredException extends JWTException {

	public TokenExpiredException(String token, String message, Throwable cause) {
		super(token, message, cause);
	}

	public TokenExpiredException(String token, String message) {
		super(token, message);
	}

	public TokenExpiredException(String token, Throwable cause) {
		super(token, cause);
	}

	public TokenExpiredException(String token) {
		super(token);
	}
}
