/**
 * 
 */
package com.appirio.tech.core.api.mock.a;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import com.appirio.tech.core.api.v2.CMCID;
import com.appirio.tech.core.api.v2.request.FieldSelector;
import com.appirio.tech.core.api.v2.request.QueryParameter;
import com.appirio.tech.core.api.v2.service.RESTQueryService;

/**
 * @author sudo
 *
 */
@Service
public class MockQueryService implements RESTQueryService<MockModelA> {

	private Map<CMCID, MockModelA> mockStorage = new HashMap<CMCID, MockModelA>();
	
	public String getResourcePath() {
		return MockModelA.RESOURCE_PATH;
	}

	public MockModelA handleGet(FieldSelector selector, CMCID recordId) throws Exception {
		return null;
	}

	public List<MockModelA> handleGet(HttpServletRequest request, QueryParameter query) throws Exception {
		List<MockModelA> result = new ArrayList<MockModelA>(mockStorage.values());
		return result;
	}

	public void insertModel(MockModelA model) {
		mockStorage.put(model.getId(), model);
	}
}
