/**
 * 
 */
package com.appirio.tech.core.api.v2;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonValue;

/**
 * @author sudo
 *
 */
@SuppressWarnings("serial")
public class CMCID implements Serializable {

	private String id;

	public CMCID(){}
	
	public CMCID(String id) {
		if(id!=null) {
			setId(id);
		}
	}
	
	public CMCID(Integer id) {
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
		if(obj instanceof CMCID) {
			//18 digit id is case-insensitive
			return id.equalsIgnoreCase(((CMCID)obj).getId());
		}
		return super.equals(obj);
	}
	public String toJSONString() {
		return id;
	}
}
