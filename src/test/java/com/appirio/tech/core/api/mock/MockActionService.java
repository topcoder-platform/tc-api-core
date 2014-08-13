/**
 * 
 */
package com.appirio.tech.core.api.mock;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import com.appirio.tech.core.api.v2.CMCID;
import com.appirio.tech.core.api.v2.response.ApiResponse;
import com.appirio.tech.core.api.v2.service.RESTActionService;

/**
 * @author sudo
 *
 */
@Service
public class MockActionService implements RESTActionService {

	/* (non-Javadoc)
	 * @see com.appirio.tech.core.api.v2.service.RESTService#getResourcePath()
	 */
	public String getResourcePath() {
		return MockModel.RESOURCE_PATH;
	}

	/* (non-Javadoc)
	 * @see com.appirio.tech.core.api.v2.service.RESTActionService#handleAction(com.appirio.tech.core.api.v2.CMCID, java.lang.String, javax.servlet.http.HttpServletRequest)
	 */
	public ApiResponse handleAction(CMCID recordId, String action, HttpServletRequest request) throws Exception {
		return null;
	}

}
