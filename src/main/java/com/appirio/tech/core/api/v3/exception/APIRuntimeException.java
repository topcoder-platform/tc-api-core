/**
 * 
 */
package com.appirio.tech.core.api.v3.exception;

import org.eclipse.jetty.http.HttpStatus;

/**
 * @author sudo
 *
 */
@SuppressWarnings("serial")
public class APIRuntimeException extends RuntimeException {

	private int httpStatus = HttpStatus.INTERNAL_SERVER_ERROR_500;
	
	/**
	 * 
	 */
	public APIRuntimeException() {
	}

	/**
	 * @param arg0
	 */
	public APIRuntimeException(String arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 */
	public APIRuntimeException(Throwable arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public APIRuntimeException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	/**
	 * 
	 */
	public APIRuntimeException(int httpStatus) {
		this.httpStatus = httpStatus;
	}

	/**
	 * @param arg0
	 */
	public APIRuntimeException(int httpStatus, String arg0) {
		super(arg0);
		this.httpStatus = httpStatus;
	}

	/**
	 * @param arg0
	 */
	public APIRuntimeException(int httpStatus, Throwable arg0) {
		super(arg0);
		this.httpStatus = httpStatus;
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public APIRuntimeException(int httpStatus, String arg0, Throwable arg1) {
		super(arg0, arg1);
		this.httpStatus = httpStatus;
	}

	public int getHttpStatus() {
		return httpStatus;
	}

	public void setHttpStatus(int httpStatus) {
		this.httpStatus = httpStatus;
	}
}
