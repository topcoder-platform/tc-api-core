/**
 * 
 */
package com.appirio.tech.core.api.v3.controller;

import com.appirio.tech.core.api.v3.exception.APIRuntimeException;

/**
 * @author sudo
 *
 */
@SuppressWarnings("serial")
public class ResourceInitializationException extends APIRuntimeException {

	public ResourceInitializationException() {
		super();
	}

	public ResourceInitializationException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public ResourceInitializationException(String arg0) {
		super(arg0);
	}

	public ResourceInitializationException(Throwable arg0) {
		super(arg0);
	}

}
