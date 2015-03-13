/**
 * 
 */
package com.appirio.tech.core.api.v3.model;

import org.joda.time.DateTime;

import com.appirio.tech.core.api.v3.TCID;
import com.appirio.tech.core.api.v3.resource.old.RESTResource;


/**
 * Base class for standard domain object with id and audit fields.
 * The object is most likely stored in persistent storage.
 * 
 * @author sudo
 *
 */
public abstract class AbstractIdResource implements RESTResource {
	private TCID id;
	private TCID modifiedBy;
	private DateTime modifiedAt;
	private TCID createdBy;
	private DateTime createdAt;
	
	public void setId(TCID id) {
		this.id = id;
	}
	
	public TCID getId() {
		return id;
	}

	public TCID getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(TCID modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public DateTime getModifiedAt() {
		return modifiedAt;
	}

	public void setModifiedAt(DateTime modifiedAt) {
		this.modifiedAt = modifiedAt;
	}

	public TCID getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(TCID createdBy) {
		this.createdBy = createdBy;
	}

	public DateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(DateTime createdAt) {
		this.createdAt = createdAt;
	}
}
