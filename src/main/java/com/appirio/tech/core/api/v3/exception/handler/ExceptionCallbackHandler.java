/**
 * 
 */
package com.appirio.tech.core.api.v3.exception.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.appirio.tech.core.api.v3.exception.ExceptionContent;

/**
 * The class get's callback when Exception is caught on API response time.
 * 
 * @author sudo
 *
 */
public interface ExceptionCallbackHandler {

	/**
	 * Returns if this ExceptionHandler handles the defined throwable caught at the root of API call.
	 * @param th
	 * @return
	 */
	boolean isHandle(Throwable th);

	/**
	 * The method will get callback from Controller upon the specified Exception is thrown in API layer.
	 * 
	 * @param th
	 * @param request
	 * @param res
	 * @return
	 */
	ExceptionContent getExceptionContent(Throwable th, HttpServletRequest request, HttpServletResponse res);
}
