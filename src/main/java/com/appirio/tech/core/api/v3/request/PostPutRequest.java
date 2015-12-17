/**
 * 
 */
package com.appirio.tech.core.api.v3.request;

import javax.validation.Valid;

/**
 * Represents the format of a post / put request to the API.
 */
public class PostPutRequest<T> extends RequestBase {
	/**
	 * The param field in the json request to represent a resource.
	 */
	@Valid
	private T param;
	
	/**
	 * Gets the param
	 * 
	 * @return the param.
	 */
	public T getParam() {
		return param;
	}

	/**
	 * Sets the param
	 * 
	 * @param param
	 *            the param.
	 */
	public void setParam(T param) {
		this.param = param;
	}
}
