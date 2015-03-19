/**
 * 
 */
package com.appirio.tech.core.api.v3.request;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents the format of a post / put request to the API.
 */
public class PostPutRequest<T> {
	/**
	 * The param field in the json request to represent a resource.
	 */
	private T param;

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
}
