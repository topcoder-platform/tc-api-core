/**
 * 
 */
package com.appirio.tech.core.api.v3.response;

import java.io.IOException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.PropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;

/**
 * ResponseFilter to return only the fields specified from client (partial
 * response) in V3 API.
 * 
 * @author sudo
 * 
 */
public class ApiResponseFilter implements ContainerResponseFilter {

	private static final Logger log = LoggerFactory.getLogger(ApiResponseFilter.class);
	public static final String FILTER_NAME = "ApiRepresentationFilter";
	private ObjectMapper mapper;

	public ApiResponseFilter(ObjectMapper mapper) {
		this.mapper = mapper;
	}

	@Override
//	public ContainerResponse filter(ContainerRequest request,
//			ContainerResponse response) {
	public void filter(ContainerRequestContext request, ContainerResponseContext response) throws IOException {
	
		if (!"GET".equals(request.getMethod())) {
			return;
		}
		if (response.getStatusInfo() != Response.Status.OK) {
			return;
		}
		if (!MediaType.APPLICATION_JSON_TYPE.isCompatible(response.getMediaType())) {
			return;
		}
		Object entity = response.getEntity();
		if(!(entity instanceof ApiResponse)) {
			return;
		}
		ApiResponse apiResponse = (ApiResponse)response.getEntity();

		try {
			PropertyFilter filter = new ApiPartialResponsePropertyFilter(apiResponse, request);
			FilterProvider fp = new SimpleFilterProvider().addFilter(FILTER_NAME, filter);
			String resp = mapper.writer(fp).writeValueAsString(entity);
			response.setEntity(resp);
		} catch (JsonProcessingException ex) {
			log.warn("Error during serialization to JSON", ex);
		}
	}

}
