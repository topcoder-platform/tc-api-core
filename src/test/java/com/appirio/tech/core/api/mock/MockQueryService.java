/**
 * 
 */
package com.appirio.tech.core.api.mock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import com.appirio.tech.core.api.v2.CMCID;
import com.appirio.tech.core.api.v2.model.CMCObject;
import com.appirio.tech.core.api.v2.model.AbstractResource;
import com.appirio.tech.core.api.v2.request.FieldSelector;
import com.appirio.tech.core.api.v2.request.QueryParameter;
import com.appirio.tech.core.api.v2.service.RESTQueryService;

/**
 * @author sudo
 *
 */
@Service
public class MockQueryService implements RESTQueryService {

	private Map<CMCID, MockModel> mockStorage = new HashMap<CMCID, MockModel>();
	
	public String getResourcePath() {
		return MockModel.RESOURCE_PATH;
	}

	public CMCObject handleGet(FieldSelector selector, CMCID recordId) throws Exception {
		return null;
	}

	public List<? extends AbstractResource> handleGet(HttpServletRequest request, QueryParameter query) throws Exception {
		List<MockModel> result = new ArrayList<MockModel>(mockStorage.values());
		return result;
	}

	public void insertModel(MockModel model) {
		mockStorage.put(model.getId(), model);
	}
}
