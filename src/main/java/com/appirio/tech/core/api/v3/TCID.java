/**
 * 
 */
package com.appirio.tech.core.api.v3;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Resource ID for all topcoder objects
 * 
 * @author sudo
 *
 */
@SuppressWarnings("serial")
public class TCID implements Serializable {

	private String id;

	public TCID(){}
	
	public TCID(String id) {
		if(id!=null) {
			setId(id);
		}
	}
	
	public TCID(Integer id) {
		if(id!=null) {
			setId(id.toString());
		}
	}
	
	public TCID(Long id) {
		if(id!=null) {
			setId(id.toString());
		}
	}


	@JsonValue
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return id;
	}
	
	@Override
	public int hashCode() {
		return id.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof TCID) {
			//18 digit id is case-insensitive
			return id.equalsIgnoreCase(((TCID)obj).getId());
		}
		return super.equals(obj);
	}
	public String toJSONString() {
		return id;
	}
}
