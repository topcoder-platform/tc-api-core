/**
 * 
 */
package com.appirio.tech.core.api.v3.request;

import java.util.Map;

/**
 * Represents the format of a http PATCH request to the API.
 * 
 */
public class PatchRequest extends RequestBase {
	/**
	 * The param field in the json request to represent a resource.
	 */
	private Map<String, Object> param;
	
	/**
	 * Gets the param.
	 * Since this is PATCH, return value would be in Map
	 * 
	 * @return the param.
	 */
	public Map<String, Object> getParam() {
		return param;
	}

	/**
	 * Sets the param
	 * 
	 * @param param
	 *            the param.
	 */
	public void setParam(Map<String, Object> param) {
		this.param = param;
	}
}
