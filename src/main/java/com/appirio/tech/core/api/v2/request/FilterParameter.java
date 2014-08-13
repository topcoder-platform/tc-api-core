/**
 *
 */
package com.appirio.tech.core.api.v2.request;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Set;

import com.appirio.tech.core.api.v2.exception.CMCParseException;


/**
 * Request query filter for /api/va1 endpoints
 * Query filter comes in as:<br/>
 *  /api/va1/stories?filter=URLENCODE(param1=filter_a&param2=filter_b&....)
 *
 * see API spec doc for details.
 *
 * @author sudo
 * @since va1
 */
public class FilterParameter extends SearchParameter {

	/**
	 * orderBy, limit and offset is no longer used in FilterParameter,
	 * but is now specified from root param from v2
	 */
	private RequestOrderBy orderBy;
	private Integer limit;
	private Integer offset;
	private boolean like = false;

	public FilterParameter(String filterValue) {
		if(filterValue==null) return;
		
		String[] params = filterValue.split("&");
		for(String param : params) {
			String[] data = param.split("=");
			if(data.length != 2) {
				throw new CMCParseException("Unable to parse filter parameter:" + param);
			}
			if(data[0].equalsIgnoreCase("order_by")){
				orderBy = new RequestOrderBy(data[1]);
			} else if(data[0].equalsIgnoreCase("limit")){
				limit = Integer.valueOf(data[1]);
			} else if(data[0].equalsIgnoreCase("offset")){
				offset = Integer.valueOf(data[1]);
			} else if(data[0].equalsIgnoreCase("like") && data[1].equalsIgnoreCase("true")) {
				like = true;
			} else {
				// support multibyte character.
				try {
					put(data[0], URLDecoder.decode(data[1],"UTF-8"));
				} catch (UnsupportedEncodingException e) {
					put(data[0],data[1]);
				}
			}
		}
	}

	public RequestOrderBy getOrderBy() {
		return orderBy;
	}

	public Integer getLimit() {
		return limit;
	}

	public Integer getOffset() {
		return offset;
	}

	public boolean isLike() {
		return like;
	}

	/**
	 * Returns Set of all fields which is defined to filter
	 *
	 * @return
	 */
	public Set<String> getFields() {
		return getParamMap().keySet();
	}
}
