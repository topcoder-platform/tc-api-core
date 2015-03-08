package com.appirio.tech.core.api.v3.resource.old;

import javax.servlet.http.HttpServletRequest;

import com.appirio.tech.core.api.v3.TCID;
import com.appirio.tech.core.api.v3.dao.DaoBase;
import com.appirio.tech.core.api.v3.model.AbstractIdResource;

/**
 * Interface to handle REST calls for persistent resources.
 * 
 * @author sudo
 *
 */
public interface RESTPersistentService<T extends AbstractIdResource> extends RESTQueryService<T> {

	TCID handlePost(HttpServletRequest request, T object) throws Exception;

	TCID handlePut(HttpServletRequest request, T object) throws Exception;
	
	void handleDelete(HttpServletRequest request, TCID id) throws Exception;

	DaoBase<T> getResourceDao();
}
