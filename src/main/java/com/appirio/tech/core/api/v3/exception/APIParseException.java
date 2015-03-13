/**
 * 
 */
package com.appirio.tech.core.api.v3.exception;


/**
 * @author sudo
 *
 */
@SuppressWarnings("serial")
public class APIParseException extends APIRuntimeException {

	/**
	 * 
	 */
	public APIParseException() {
		super();
	}

	/**
	 * @param arg0
	 */
	public APIParseException(String arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 */
	public APIParseException(Throwable arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public APIParseException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

}
