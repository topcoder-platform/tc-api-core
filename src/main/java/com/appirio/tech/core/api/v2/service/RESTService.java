/**
 * 
 */
package com.appirio.tech.core.api.v2.service;

import org.codehaus.jackson.annotate.JsonIgnore;


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
	@JsonIgnore
	public String getResourcePath();
}
