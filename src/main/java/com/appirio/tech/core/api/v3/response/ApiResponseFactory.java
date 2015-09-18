/**
 * 
 */
package com.appirio.tech.core.api.v3.response;

import java.net.URI;
import java.rmi.server.UID;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jetty.http.HttpStatus;

import com.appirio.tech.core.api.v3.ApiVersion;
import com.appirio.tech.core.api.v3.model.ResourceHelper;
import com.appirio.tech.core.api.v3.request.FieldSelector;
import com.appirio.tech.core.api.v3.resource.old.RESTResource;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author sudo
 *
 */
public class ApiResponseFactory {

	@JsonIgnore
	public static ApiResponse createResponse(final Object object) {
		ApiResponse response = new ApiResponse();
		response.setId((new UID()).toString());
		response.setResult(true, HttpStatus.OK_200, object);
		response.setVersion(ApiVersion.v3);
		return response;
	}

	public static ApiResponse createCreatedResponse(final String id, final URI locationUri) {
		ApiResponse response = new ApiResponse();
		response.setId((new UID()).toString());
		response.setResult(true, HttpStatus.CREATED_201, id);
		response.setVersion(ApiVersion.v3);

		// TODO: Set Header with locationUri
		// Location: locationUri
		return response;
	}

	/*
	 * TODO: refine the metatada handling
	 */
	public static ApiFieldSelectorResponse createFieldSelectorResponse(List<? extends RESTResource> object, Object metadata, FieldSelector selector) {
		ApiFieldSelectorResponse response = new ApiFieldSelectorResponse();
		Map<Integer, Set<String>> fieldSelectionMap = new HashMap<Integer, Set<String>>();
		for(RESTResource resource : object) {
			ResourceHelper.setSerializeFields(resource, selector, fieldSelectionMap);
		}
		response.setResult(true, HttpStatus.OK_200, metadata, object);
		response.setVersion(ApiVersion.v3);
		response.setFieldSelectionMap(fieldSelectionMap);
		return response;
	}

	/*
	 * TODO: refine the metatada handling
	 */
	public static ApiFieldSelectorResponse createFieldSelectorResponse(List<? extends RESTResource> object, FieldSelector selector) {
		return createFieldSelectorResponse(object, null, selector);
	}

	public static ApiFieldSelectorResponse createFieldSelectorResponse(final RESTResource object, FieldSelector selector) {
		ApiFieldSelectorResponse response = new ApiFieldSelectorResponse();
		response.setResult(true, HttpStatus.OK_200, object);
		response.setVersion(ApiVersion.v3);
		if(object!=null) {
			Map<Integer, Set<String>> fieldSelectionMap = new HashMap<Integer, Set<String>>();
			ResourceHelper.setSerializeFields(object, selector, fieldSelectionMap);
			response.setFieldSelectionMap(fieldSelectionMap);
		}
		return response;
	}

	/**
	 * The default return fields for the post/put/delete request.
	 */
	public static final String DEFAULT_DDL_RETURN_FIELDS = "id";
}
