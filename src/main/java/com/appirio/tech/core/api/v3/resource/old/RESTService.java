/**
 * 
 */
package com.appirio.tech.core.api.v3.resource.old;

import com.appirio.tech.core.api.v3.model.annotation.ApiMapping;
import com.fasterxml.jackson.annotation.JsonIgnore;


/**
 * Interface to define classes that handles RESTful API requests.
 * 
 * @author sudo
 *
 */
public interface RESTService {
	/**
	 * Return resource POJO class that this service handles.
	 * 
	 * @return
	 */
	@ApiMapping(visible=false)
	@JsonIgnore
	public Class<? extends RESTResource> getResourceClass();
}
