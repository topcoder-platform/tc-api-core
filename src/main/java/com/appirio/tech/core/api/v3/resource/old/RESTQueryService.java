/**
 * 
 */
package com.appirio.tech.core.api.v3.resource.old;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.appirio.tech.core.api.v3.TCID;
import com.appirio.tech.core.api.v3.request.FieldSelector;
import com.appirio.tech.core.api.v3.request.QueryParameter;

/**
 * Interface to handle query (GET) calls.
 * 
 * @author sudo
 *
 */
public interface RESTQueryService<T extends RESTResource> extends RESTService {

	/**
	 * returns CMCObject of defined TCID.
	 * 
	 * @param selector
	 * @param recordId
	 * @return
	 * @throws Exception
	 */
	T handleGet(FieldSelector selector, TCID recordId) throws Exception;

	List<T> handleGet(HttpServletRequest request, QueryParameter query) throws Exception;
}
