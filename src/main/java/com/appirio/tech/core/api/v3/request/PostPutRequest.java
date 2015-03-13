/**
 * 
 */
package com.appirio.tech.core.api.v3.request;

import com.appirio.tech.core.api.v3.dropwizard.APIApplication;
import com.appirio.tech.core.api.v3.exception.ResourceInitializationException;
import com.appirio.tech.core.api.v3.model.AbstractIdResource;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Represents the format of a post / put request to the API.
 */
public class PostPutRequest {
	/**
	 * The param field in the json request to represent a resource.
	 */
	private JsonNode param;

	/**
	 * The method.
	 */
	private String method;

	/**
	 * The expected return fields.
	 */
	@JsonProperty("return")
	private String _return;

	/**
	 * Whether it's originalRequest or not.
	 */
	private boolean originalRequest;

	/**
	 * Whether needs debug information in the API response if error.
	 */
	private boolean debug;

	/**
	 * Gets the param
	 * 
	 * @return the param.
	 */
	public JsonNode getParam() {
		return param;
	}

	/**
	 * Sets the param
	 * 
	 * @param param
	 *            the param.
	 */
	public void setParam(JsonNode param) {
		this.param = param;
	}

	/**
	 * Gets the method.
	 * 
	 * @return the method
	 */
	public String getMethod() {
		return method;
	}

	/**
	 * Sets the method
	 * 
	 * @param method
	 *            the method
	 */
	public void setMethod(String method) {
		this.method = method;
	}

	/**
	 * Gets the expected return fields.
	 * 
	 * @return the expected return fields.
	 */
	public String getReturn() {
		return _return;
	}

	/**
	 * Sets the expected return fields.
	 * 
	 * @param _return
	 *            the expected return fields
	 */
	public void setReturn(String _return) {
		this._return = _return;
	}

	/**
	 * Gets whether it's original request.
	 * 
	 * @return whether it's original request.
	 */
	public boolean isOriginalRequest() {
		return originalRequest;
	}

	/**
	 * Sets whether it's original request.
	 * 
	 * @param originalRequest
	 *            whether it's original request.
	 */
	public void setOriginalRequest(boolean originalRequest) {
		this.originalRequest = originalRequest;
	}

	/**
	 * Gets whether need debug information if error happens.
	 * 
	 * @return whether need debug information.
	 */
	public boolean isDebug() {
		return debug;
	}

	/**
	 * Sets whether need debug information if error happens.
	 * 
	 * @param debug
	 *            whether need debug information.
	 */
	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	/**
	 * Utility method to get Content Reresentation Class
	 * TODO: use generics to return exact type and @Validate
	 * 
	 * @param resource
	 * @param putRequest
	 * @param resourceModel
	 * @param resourceData
	 * @return
	 */
	@JsonIgnore
	public AbstractIdResource getParamObject(Class<? extends AbstractIdResource> resourceModel) {
		AbstractIdResource resourceData;
		if (getParam() == null) {
			// the resource model data is not specified, throw
			// ResourceInitializationException
			throw new ResourceInitializationException(String.format(
					"There is no data for [%s] resource in post request", resourceModel.toString()));
		} else {
			try {
				// deserialize param data in post request (json data) to the
				// resource object.
				// Note: current version of ObjectMapper doesn't directly support #readValue(JsonNode, object) so putting into String
				resourceData = APIApplication.JACKSON_OBJECT_MAPPER.readValue(
						APIApplication.JACKSON_OBJECT_MAPPER.writeValueAsString(getParam()), resourceModel);
			} catch (Exception ex) {
				// error when deserialize from json data
				throw new ResourceInitializationException(String.format(
						"Fail to initialize [%s] resource from post request", resourceModel.toString()), ex);
			}
		}
		return resourceData;
	}
}
