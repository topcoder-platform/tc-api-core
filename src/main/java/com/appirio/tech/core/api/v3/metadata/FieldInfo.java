/**
 * 
 */
package com.appirio.tech.core.api.v3.metadata;


/**
 * Class to hold each field information for API
 * 
 * @author sudo
 *
 */
public class FieldInfo {
	String label;
	String type;
	Boolean isDefault;
//	Boolean canFilter;
	
	public String getLabel() {
		return label;
	}
	public void setName(String name) {
		this.label = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Boolean getIsDefault() {
		return isDefault;
	}
	public void setDefault(Boolean isDefault) {
		this.isDefault = isDefault;
	}
//	public Boolean getCanFilter() {
//		return canFilter;
//	}
//	public void setCanFilter(Boolean canFilter) {
//		this.canFilter = canFilter;
//	}
}
