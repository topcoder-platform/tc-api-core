/**
 * 
 */
package com.appirio.tech.core.api.v3.request;

import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.util.MultiValueMap;

import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;
import com.appirio.tech.core.api.v3.exception.CMCParseException;

/**
 * Request query filter for /api/va endpoints
 * 
 * @author sudo
 *
 */
public class RequestParser {

	protected final Logger logger = Logger.getLogger(getClass());
	public static final String POST_PARAM = "post-param";
	public static final String PUT_PARAM = "put-param";
	public static final String RETURN_KEY = "return";
	public static final String DEBUG_KEY = "debug";
	public static final String CHARACTER_ENCODING_UTF8 = "UTF-8";
	
	private String rawValue;
	
	public RequestParser(String value) {
		this.rawValue = value;
	}

	public RequestParser(MultiValueMap<String, String> body) {
		if(body.containsKey(POST_PARAM)) {
			rawValue = body.getFirst(POST_PARAM);
		} else {
			rawValue = body.getFirst(PUT_PARAM);
		}
	}

	public JSONObject getObjectJSON() {
		try {
			JSONObject obj = getParsedObjectJSON();
			return obj;
		} catch (JSONException e) {
			logger.warn("Unable to parse request:" + rawValue, e);
			throw new CMCParseException("Unable to parse request", e);
		}
	}

	/**
	 * return true if the request want's more than default (id) field.
	 * @return
	 */
	public boolean hasNonDefaultReturn() {
		try {
			JSONObject obj = new JSONObject(rawValue);
			if(obj.has(RETURN_KEY)) {
				FieldSelector selector = FieldSelector.instanceFromVaString(RETURN_KEY + ":" + obj.getString(RETURN_KEY));
				Set<String> fields = selector.getSelectedFields();
				fields.remove("id");
				return fields.size() > 0;
			}
			return false;
		} catch (JSONException e) {
			logger.warn("Unable to parse request:" + rawValue, e);
			throw new CMCParseException("Unable to parse request", e);
		}
	}
	
	/**
	 * returns FieldSelector for returning fields.
	 * @return
	 */
	public FieldSelector getReturnFieldSelector() {
		try {
			JSONObject obj = new JSONObject(rawValue);
			if(obj.has(RETURN_KEY)) {
				return FieldSelector.instanceFromVaString(RETURN_KEY + ":" + obj.getString(RETURN_KEY));
			} else {
				return FieldSelector.instanceFromVaString(RETURN_KEY).addField("id");
			}
		} catch (JSONException e) {
			logger.warn("Unable to parse request:" + rawValue, e);
			throw new CMCParseException("Unable to parse request", e);
		}
	}
	
	/**
	 * remove all Request specific values.
	 * @return
	 * @throws JSONException
	 */
	private JSONObject getParsedObjectJSON() throws JSONException {
		JSONObject obj = new JSONObject(rawValue);
		obj.remove(RETURN_KEY);
		obj.remove(DEBUG_KEY);
		return obj;
	}

}
