/**
 * 
 */
package com.appirio.tech.core.api.v3.model;

import com.appirio.tech.core.api.v3.TCID;


/**
 * Base class for CMC domain object that has ID, which means that the object is
 * stored as a record in CMC storage.
 * 
 * @author sudo
 *
 */
public abstract class AbstractIdResource extends AbstractResource {
	private TCID id;
	
	public void setId(TCID id) {
		this.id = id;
	}
	
	public TCID getId() {
		return id;
	}
}
