/**
 * 
 */
package com.appirio.tech.core.api.v2;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;

/**
 * Default ExceptionCallbackHandler that handles base exception upon API error.
 * 
 * @author sudo
 *
 */
public class RootExceptionCallbackHandler implements ExceptionCallbackHandler{

	public boolean isHandle(Throwable th) {
		//returns true for every Throwable type
		return true;
	}

	public ExceptionContent getExceptionContent(Throwable th, HttpServletRequest request, HttpServletResponse res) {
		ExceptionContent content = new ExceptionContent(th);
		content.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
		return content;
	}
}
