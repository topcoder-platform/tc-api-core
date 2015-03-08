/**
 * 
 */
package com.appirio.tech.core.api.v3.exception;

/**
 * Expected resource isn't mapped within the application.
 * 
 * @author sudo
 *
 */
@SuppressWarnings("serial")
public class ResourceNotMappedException extends APIRuntimeException {

	public ResourceNotMappedException() {
	}

	public ResourceNotMappedException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public ResourceNotMappedException(String arg0) {
		super(arg0);
	}

	public ResourceNotMappedException(Throwable arg0) {
		super(arg0);
	}

}
