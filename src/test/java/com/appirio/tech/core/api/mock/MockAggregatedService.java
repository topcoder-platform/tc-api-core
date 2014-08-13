/**
 * 
 */
package com.appirio.tech.core.api.mock;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.appirio.tech.core.api.v2.CMCID;
import com.appirio.tech.core.api.v2.dao.DaoBase;
import com.appirio.tech.core.api.v2.model.CMCObject;
import com.appirio.tech.core.api.v2.model.CMCResource;
import com.appirio.tech.core.api.v2.request.FieldSelector;
import com.appirio.tech.core.api.v2.request.QueryParameter;
import com.appirio.tech.core.api.v2.response.ApiResponse;
import com.appirio.tech.core.api.v2.service.RESTActionService;
import com.appirio.tech.core.api.v2.service.RESTPersistentService;
import com.appirio.tech.core.api.v2.service.RESTQueryService;

/**
 * Mock Service class that handles all the REST calls.
 * 
 * @author sudo
 *
 */
@Component
public class MockAggregatedService implements RESTActionService, RESTPersistentService, RESTQueryService {

	/* (non-Javadoc)
	 * @see com.appirio.tech.core.api.v2.service.RESTService#getResourcePath()
	 */
	public String getResourcePath() {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.appirio.tech.core.api.v2.service.RESTQueryService#handleGet(com.appirio.tech.core.api.v2.request.FieldSelector, com.appirio.tech.core.api.v2.CMCID)
	 */
	public CMCObject handleGet(FieldSelector selector, CMCID recordId) throws Exception {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.appirio.tech.core.api.v2.service.RESTQueryService#handleGet(javax.servlet.http.HttpServletRequest, com.appirio.tech.core.api.v2.request.QueryParameter)
	 */
	public List<? extends CMCResource> handleGet(HttpServletRequest request, QueryParameter query) throws Exception {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.appirio.tech.core.api.v2.service.RESTPersistentService#handlePost(javax.servlet.http.HttpServletRequest, com.appirio.tech.core.api.v2.model.CMCObject)
	 */
	public <T extends CMCObject> CMCID handlePost(HttpServletRequest request, T object) throws Exception {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.appirio.tech.core.api.v2.service.RESTPersistentService#handlePut(javax.servlet.http.HttpServletRequest, com.appirio.tech.core.api.v2.model.CMCObject)
	 */
	public <T extends CMCObject> CMCID handlePut(HttpServletRequest request, T object) throws Exception {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.appirio.tech.core.api.v2.service.RESTPersistentService#handleDelete(javax.servlet.http.HttpServletRequest, com.appirio.tech.core.api.v2.CMCID)
	 */
	public void handleDelete(HttpServletRequest request, CMCID id) throws Exception {
	}

	/* (non-Javadoc)
	 * @see com.appirio.tech.core.api.v2.service.RESTPersistentService#getResourceDao()
	 */
	public <T extends CMCObject> DaoBase<T> getResourceDao() {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.appirio.tech.core.api.v2.service.RESTActionService#handleAction(com.appirio.tech.core.api.v2.CMCID, java.lang.String, javax.servlet.http.HttpServletRequest)
	 */
	public ApiResponse handleAction(CMCID recordId, String action, HttpServletRequest request) throws Exception {
		return null;
	}

}
