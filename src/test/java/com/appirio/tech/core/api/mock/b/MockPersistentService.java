/**
 * 
 */
package com.appirio.tech.core.api.mock.b;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.appirio.tech.core.api.v2.CMCID;
import com.appirio.tech.core.api.v2.dao.DaoBase;
import com.appirio.tech.core.api.v2.request.FieldSelector;
import com.appirio.tech.core.api.v2.request.QueryParameter;
import com.appirio.tech.core.api.v2.response.ApiResponse;
import com.appirio.tech.core.api.v2.service.RESTActionService;
import com.appirio.tech.core.api.v2.service.RESTPersistentService;

/**
 * Mock Service class that handles all the REST calls.
 * 
 * @author sudo
 *
 */
@Component
public class MockPersistentService implements RESTActionService, RESTPersistentService<MockModelB> {

	public String getResourcePath() {
		return MockModelB.RESOURCE_PATH;
	}

	public MockModelB handleGet(FieldSelector selector, CMCID recordId) throws Exception {
		return null;
	}

	public List<MockModelB> handleGet(HttpServletRequest request, QueryParameter query) throws Exception {
		return null;
	}

	public CMCID handlePost(HttpServletRequest request, MockModelB object) throws Exception {
		return null;
	}

	public CMCID handlePut(HttpServletRequest request, MockModelB object) throws Exception {
		return null;
	}

	public void handleDelete(HttpServletRequest request, CMCID id) throws Exception {
	}

	public DaoBase<MockModelB> getResourceDao() {
		return null;
	}

	public ApiResponse handleAction(String action, HttpServletRequest request) throws Exception {
		return null;
	}

	public ApiResponse handleAction(CMCID recordId, String action, HttpServletRequest request) throws Exception {
		return null;
	}

}
