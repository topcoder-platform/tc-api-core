package com.appirio.tech.core.api.v3.request;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RequestBase {

	/**
	 * The options
	 */
	private Map<String, Object> options;
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

	public RequestBase() {
		super();
	}

	/**
	 * Gets the options
	 * @return options
	 */
	public Map<String, Object> getOptions() {
		return options;
	}

	/**
	 * Returns the value to which the specified key is mapped in the options.
	 * @param key
	 * @return option value
	 */
	public Object getOption(String key) {
		if (key==null || options==null || !options.containsKey(key))
			return null;
		return options.get(key);
	}

	/**
	 * Returns the value converted as String to which the specified key is mapped in the options.
	 * @param key
	 * @return option value
	 */
	public String getOptionString(String key) {
		Object val = getOption(key);
		if(val==null)
			return null;
		return (val instanceof String) ? (String) val : String.valueOf(val);
	}

	/**
	 * Sets the options
	 * 
	 * @param options
	 *            the options.
	 */
	public void setOptions(Map<String, Object> options) {
		this.options = options;
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