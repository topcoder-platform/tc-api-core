/**
 * 
 */
package com.appirio.tech.core.api.v2.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.appirio.tech.core.api.v2.CMCID;
import com.appirio.tech.core.api.v2.model.CMCObject;
import com.appirio.tech.core.api.v2.model.CMCResource;
import com.appirio.tech.core.api.v2.request.FieldSelector;
import com.appirio.tech.core.api.v2.request.QueryParameter;

/**
 * Interface to handle query (GET) calls.
 * 
 * @author sudo
 *
 */
public interface RESTQueryService {

	/**
	 * returns CMCObject of defined CMCID.
	 * 
	 * @param selector
	 * @param recordId
	 * @return
	 * @throws Exception
	 */
	CMCObject handleGet(FieldSelector selector, CMCID recordId) throws Exception;

	List<? extends CMCResource> handleGet(HttpServletRequest request, QueryParameter query) throws Exception;
	
}
