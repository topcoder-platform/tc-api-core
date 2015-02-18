/**
 * 
 */
package com.appirio.tech.core.api.v3.exception.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;

import com.appirio.tech.core.api.v3.exception.ExceptionContent;

/**
 * Default ExceptionCallbackHandler that handles base exception upon API error.
 * 
 * @author sudo
 *
 */
public class RootExceptionCallbackHandler implements ExceptionCallbackHandler{

	protected final Logger logger = Logger.getLogger(getClass());

	public boolean isHandle(Throwable th) {
		//returns true for every Throwable type
		return true;
	}

	public ExceptionContent getExceptionContent(Throwable th, HttpServletRequest request, HttpServletResponse res) {
		logger.warn("Unknown exception caught in Api controller.", th);
		
		ExceptionContent content = new ExceptionContent(th);
		content.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
		return content;
	}
}
