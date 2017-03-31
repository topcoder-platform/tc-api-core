/**
 * 
 */
package com.topcoder.core.api.v3.resource.old;

import javax.servlet.http.HttpServletRequest;

import com.topcoder.core.api.v3.metadata.Metadata;
import com.topcoder.core.api.v3.request.QueryParameter;

/**
 * @author sudo
 *
 */
public interface RESTMetadataService extends RESTService {

	Metadata getMetadata(HttpServletRequest request, QueryParameter query) throws Exception;
}
