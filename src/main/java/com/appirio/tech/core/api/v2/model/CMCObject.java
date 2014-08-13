/**
 * 
 */
package com.appirio.tech.core.api.v2.model;

import com.appirio.tech.core.api.v2.CMCID;


/**
 * Base class for CMC domain object that has ID, which means that the object is
 * stored as a record in CMC storage.
 * 
 * @author sudo
 *
 */
public abstract class CMCObject extends AbstractResource {
	private CMCID id;
	
	public void setId(CMCID id) {
		this.id = id;
	}
	
	public CMCID getId() {
		return id;
	}
}
