/**
 * 
 */
package com.appirio.tech.core.api.v2.service;


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
	public String getResourcePath();
}
