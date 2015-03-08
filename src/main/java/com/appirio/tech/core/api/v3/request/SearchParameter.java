/**
 * 
 */
package com.appirio.tech.core.api.v3.request;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import com.appirio.tech.core.api.v3.exception.APIParseException;

/**
 * Represents a query parameter specified in our API.
 * 
 * ex.)
 * ..../objects/{objectId}?<object field A>=<value A>&<object field B>=<value B>&.....
 * in this case, this class holds map of ({<object field A>, <value A>}, {<object field B>, <value B>})
 * 
 * TODO: include facet, range queries in future.
 * 
 * @author sudo
 *
 */
public class SearchParameter {
	private Map<String, Object> paramMap = new HashMap<String, Object>();
	
	public SearchParameter() {
	    
	}
	
	public SearchParameter(String paramString) {
	    String[] params = paramString.split("&");
        for(String param : params) {
            String[] data = param.split("=");
            if(data.length != 2) {
                throw new APIParseException("Unable to parse filter parameter:" + param);
            }
            // support multibyte character.
            try {
                paramMap.put(data[0], URLDecoder.decode(data[1],"UTF-8"));
            } catch (UnsupportedEncodingException e) {
                paramMap.put(data[0],data[1]);
            }
        }
	}

	public Map<String, Object> getParamMap() {
		return paramMap;
	}

	public void put(String key, Object value) {
		paramMap.put(key, value);
	}

	public boolean contains(String key) {
		return paramMap.containsKey(key);
	}

	public Object get(String key) {
		return paramMap.get(key);
	}
}
