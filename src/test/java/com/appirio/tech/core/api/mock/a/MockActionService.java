/**
 * 
 */
package com.appirio.tech.core.api.mock.a;

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

	public String getResourcePath() {
		return MockModelA.RESOURCE_PATH;
	}

	public ApiResponse handleAction(String action, HttpServletRequest request) throws Exception {
		return null;
	}

	public ApiResponse handleAction(CMCID recordId, String action, HttpServletRequest request) throws Exception {
		return null;
	}

}
