/**
 * 
 */
package com.appirio.tech.core.api.v3.response;

import java.util.Map;
import java.util.Set;

import com.appirio.tech.core.api.v3.model.annotation.ApiMapping;
import com.fasterxml.jackson.annotation.JsonIgnore;


/**
 * Adds fields selection on query results.
 * 
 * @author sudo
 *
 */
public class ApiFieldSelectorResponse extends ApiResponse {

	private Map<Integer, Set<String>> fieldSelectionMap; //Key: Object id (System.identyHashCode()), Value: Set of field names that needs to get serialized
	
	public ApiFieldSelectorResponse() {
		super();
	}
	
	@JsonIgnore
	@ApiMapping(visible=false)
	public Map<Integer, Set<String>> getFieldSelectionMap() {
		return fieldSelectionMap;
	}

	public void setFieldSelectionMap(Map<Integer, Set<String>> fieldSelectionMap) {
		this.fieldSelectionMap = fieldSelectionMap;
	}
	
}
