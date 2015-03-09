/**
 * 
 */
package com.appirio.tech.core.api.v3.mock.a;

import javax.servlet.http.HttpServletRequest;

import com.appirio.tech.core.api.v3.TCID;
import com.appirio.tech.core.api.v3.model.annotation.ApiMapping;
import com.appirio.tech.core.api.v3.resource.old.RESTActionService;
import com.appirio.tech.core.api.v3.resource.old.RESTResource;
import com.appirio.tech.core.api.v3.response.ApiResponse;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author sudo
 *
 */
//@Path("mock_a_models")
public class MockActionResource implements RESTActionService {

	@Override
	@ApiMapping(visible = false)
	@JsonIgnore
	public Class<? extends RESTResource> getResourceClass() {
		return MockModelA.class;
	}

	public ApiResponse handleAction(String action, HttpServletRequest request) throws Exception {
		return null;
	}

	public ApiResponse handleAction(TCID recordId, String action, HttpServletRequest request) throws Exception {
		return null;
	}

}
