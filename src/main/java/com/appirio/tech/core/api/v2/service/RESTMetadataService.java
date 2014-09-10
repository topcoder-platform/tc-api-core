/**
 * 
 */
package com.appirio.tech.core.api.v2.service;

import javax.servlet.http.HttpServletRequest;

import com.appirio.tech.core.api.v2.metadata.Metadata;
import com.appirio.tech.core.api.v2.request.QueryParameter;

/**
 * @author sudo
 *
 */
public interface RESTMetadataService extends RESTService {

	Metadata getMetadata(HttpServletRequest request, QueryParameter query) throws Exception;
}
