/**
 * 
 */
package com.appirio.tech.core.api.v3.dropwizard;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.log4j.Logger;
import org.eclipse.jetty.http.HttpStatus;

import com.appirio.tech.core.api.v3.exception.APIRuntimeException;
import com.appirio.tech.core.api.v3.response.ApiResponse;

/**
 * Provider to hook general runtime exception to Jersey's response mapping.
 * 
 * @author sudo
 *
 */
@Provider
public class RuntimeExceptionMapper implements ExceptionMapper<RuntimeException> {

	private static Logger logger = Logger.getLogger(RuntimeExceptionMapper.class);
	
	@Override
	public Response toResponse(RuntimeException exception) {
		ApiResponse apiResponse = new ApiResponse();
		int status = HttpStatus.INTERNAL_SERVER_ERROR_500;
		
		// APIRuntimeException
		if(exception instanceof APIRuntimeException) {
			String message = exception.getLocalizedMessage();
			status = ((APIRuntimeException)exception).getHttpStatus();
			apiResponse.setResult(true, status, message);
		} else if(exception.getCause()!=null &&
				exception.getCause() instanceof APIRuntimeException) {
			String message = exception.getCause().getLocalizedMessage();
			status = ((APIRuntimeException)exception.getCause()).getHttpStatus();
			apiResponse.setResult(true, status, message);
		}
		// JAX-RS Exceptions
		else if(exception instanceof WebApplicationException) {
			WebApplicationException wae = (WebApplicationException)exception;
			if(wae.getResponse()!=null)
				status = wae.getResponse().getStatus();
			apiResponse.setResult(true, status, wae.getMessage());
		} 
		// all others returning Internal Server Error (500)
		else {
			String message = exception.getLocalizedMessage();
			apiResponse.setResult(true, status, message);
			
			logger.error("Internal Server Error occurred. cause: "+exception.getMessage(), exception);
		}
		
		return Response.serverError()
				.status(status)
				.entity(apiResponse)
				.build();
	}

}
