/**
 * 
 */
package com.appirio.tech.core.api.v2.service;

import com.appirio.tech.core.api.v2.model.annotation.ApiMapping;


/**
 * Interface to define classes that handles API requests.
 * 
 * @author sudo
 *
 */
public interface RESTService {
	/**
	 * Return the root resource path that this service class handles.
	 * 
	 * @return
	 */
	@ApiMapping(visible=false)
	public String getResourcePath();
}
