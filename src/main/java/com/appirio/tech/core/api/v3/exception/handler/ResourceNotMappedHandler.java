/**
 * 
 */
package com.appirio.tech.core.api.v3.exception.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;

import com.appirio.tech.core.api.v3.exception.ExceptionContent;
import com.appirio.tech.core.api.v3.exception.ResourceNotMappedException;

/**
 * Returns 404 (NotFound) 
 * 
 * @author sudo
 *
 */
public class ResourceNotMappedHandler implements ExceptionCallbackHandler {

	public boolean isHandle(Throwable th) {
		return (th instanceof ResourceNotMappedException)?true:false;
	}

	public ExceptionContent getExceptionContent(Throwable th, HttpServletRequest request, HttpServletResponse res) {
		ExceptionContent content = new ExceptionContent(th);
		content.setHttpStatus(HttpStatus.NOT_FOUND);
		return content;
	}

}
