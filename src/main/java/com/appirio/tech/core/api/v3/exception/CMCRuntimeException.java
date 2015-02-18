/**
 * 
 */
package com.appirio.tech.core.api.v3.exception;

/**
 * @author sudo
 *
 */
@SuppressWarnings("serial")
public class CMCRuntimeException extends RuntimeException {

	/**
	 * 
	 */
	public CMCRuntimeException() {
	}

	/**
	 * @param arg0
	 */
	public CMCRuntimeException(String arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 */
	public CMCRuntimeException(Throwable arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public CMCRuntimeException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

}
