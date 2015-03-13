package com.appirio.tech.core.api.v3.exception;


/**
 * AppInitializationException indicates any exception thrown within the application initialization phase.
 * 
 * @author sudo
 *
 */
@SuppressWarnings("serial")
public class AppInitializationException extends APIRuntimeException {

	public AppInitializationException() {
		super();
	}

	public AppInitializationException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public AppInitializationException(String arg0) {
		super(arg0);
	}

	public AppInitializationException(Throwable arg0) {
		super(arg0);
	}

}
