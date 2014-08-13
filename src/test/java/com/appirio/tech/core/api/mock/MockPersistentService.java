/**
 * 
 */
package com.appirio.tech.core.api.mock;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import com.appirio.tech.core.api.v2.CMCID;
import com.appirio.tech.core.api.v2.dao.DaoBase;
import com.appirio.tech.core.api.v2.model.CMCObject;
import com.appirio.tech.core.api.v2.service.RESTPersistentService;

/**
 * @author sudo
 *
 */
@Service
public class MockPersistentService implements RESTPersistentService {

	/* (non-Javadoc)
	 * @see com.appirio.tech.core.api.v2.service.RESTService#getResourcePath()
	 */
	public String getResourcePath() {
		return MockModel.RESOURCE_PATH;
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

}
