/**
 * 
 */
package com.appirio.tech.core.api.v3.controller;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.appirio.tech.core.api.v3.exception.handler.ResourceNotMappedHandler;
import com.appirio.tech.core.api.v3.exception.handler.RootExceptionCallbackHandler;

/**
 * ApiConfiguration class to handle Bean declaration for API.
 * 
 * @author sudo
 *
 */
@Configuration
public class ApiConfig {

	@Bean
	public ResourceNotMappedHandler resourceNotMappedCallbackHandler() {
		return new ResourceNotMappedHandler();
	}
	
	@Bean
	public RootExceptionCallbackHandler rootExceptionCallbackHandler() {
		return new RootExceptionCallbackHandler();
	}
}
