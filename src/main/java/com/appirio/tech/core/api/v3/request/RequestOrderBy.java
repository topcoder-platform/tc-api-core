/**
 * 
 */
package com.appirio.tech.core.api.v3.request;

import com.appirio.tech.core.api.v3.exception.CMCParseException;

/**
 * Request query order by definition for /api/va1 endpoints
 * Query order_by comes in as:<br/>
 *  /api/va1/stories?order_by=URLENCODE(param1 desc nulls_last)
 *  the (asc/desc) and (nulls_first/nulls_last) are optional
 * 
 * see API spec doc for details.
 * 
 * @author sudo
 * @since va1
 */
public class RequestOrderBy {
	
	private String rawString;
	private String field;
	
	// http://www.salesforce.com/us/developer/docs/soql_sosl/Content/sforce_api_calls_soql_select_orderby.htm
	private boolean isDesc = false; //default for Salesforce is ASC
	private boolean isNullsLast = false; //default for Salesforce is NULLS FIRST
	
	public RequestOrderBy(String orderByValue) {
		this.rawString = orderByValue;
		String[] orderBy = orderByValue.split(" ");
		if(orderBy.length > 3) {
			throw new CMCParseException("Unable to parse order_by parameter:" + orderByValue);
		}
		
		field = orderBy[0];
		if(orderBy.length>1) {
			isDesc = orderBy[1].equalsIgnoreCase("desc");
		}
		if(orderBy.length>2) {
			isNullsLast = orderBy[2].equalsIgnoreCase("nulls_last");
		}
	}

	public String getRawString() {
		return rawString;
	}

	public String getField() {
		return field;
	}

	public boolean isDesc() {
		return isDesc;
	}

	public boolean isNullsLast() {
		return isNullsLast;
	}
}
