/**
 * 
 */
package com.appirio.tech.core.api.v3.dropwizard;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

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

	@Override
	public Response toResponse(RuntimeException exception) {
		ApiResponse apiResponse = new ApiResponse();
		int status = HttpStatus.INTERNAL_SERVER_ERROR_500;
		
		//All application exceptions should extend APIRuntimeException
		if(exception.getCause()!=null &&
				exception.getCause() instanceof APIRuntimeException) {
			String message = exception.getCause().getLocalizedMessage();
			status = ((APIRuntimeException)exception.getCause()).getHttpStatus();
			apiResponse.setResult(true, status, message);
		} else {
			//all others returning Internal Server Error (500)
			String message = exception.getLocalizedMessage();
			apiResponse.setResult(true, status, message);
		}
		
		return Response.serverError()
				.status(status)
				.entity(apiResponse)
				.build();
	}

}
