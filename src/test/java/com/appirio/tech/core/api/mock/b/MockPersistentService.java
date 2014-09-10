/**
 * 
 */
package com.appirio.tech.core.api.mock.b;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.appirio.tech.core.api.v2.CMCID;
import com.appirio.tech.core.api.v2.dao.DaoBase;
import com.appirio.tech.core.api.v2.metadata.CountableMetadata;
import com.appirio.tech.core.api.v2.metadata.Metadata;
import com.appirio.tech.core.api.v2.request.FieldSelector;
import com.appirio.tech.core.api.v2.request.QueryParameter;
import com.appirio.tech.core.api.v2.response.ApiResponse;
import com.appirio.tech.core.api.v2.service.AbstractMetadataService;
import com.appirio.tech.core.api.v2.service.RESTActionService;
import com.appirio.tech.core.api.v2.service.RESTPersistentService;

/**
 * Mock Service class that handles all the REST calls.
 * 
 * @author sudo
 *
 */
@Component
public class MockPersistentService extends AbstractMetadataService implements RESTActionService, RESTPersistentService<MockModelB> {

	private Map<CMCID, MockModelB> mockStorage = new HashMap<CMCID, MockModelB>();

	public String getResourcePath() {
		return MockModelB.RESOURCE_PATH;
	}

	public MockModelB handleGet(FieldSelector selector, CMCID recordId) throws Exception {
		return mockStorage.get(recordId);
	}

	public List<MockModelB> handleGet(HttpServletRequest request, QueryParameter query) throws Exception {
		List<MockModelB> result = new ArrayList<MockModelB>(mockStorage.values());
		return result;
	}

	public CMCID handlePost(HttpServletRequest request, MockModelB object) throws Exception {
		/* Not Implemented for mock yet */
		return null;
	}

	public CMCID handlePut(HttpServletRequest request, MockModelB object) throws Exception {
		/* Not Implemented for mock yet */
		return null;
	}

	public void handleDelete(HttpServletRequest request, CMCID id) throws Exception {
		/* Not Implemented for mock yet */
	}

	public DaoBase<MockModelB> getResourceDao() {
		/* Not Implemented for mock yet */
		return null;
	}

	public ApiResponse handleAction(String action, HttpServletRequest request) throws Exception {
		/* Not Implemented for mock yet */
		return null;
	}

	public ApiResponse handleAction(CMCID recordId, String action, HttpServletRequest request) throws Exception {
		/* Not Implemented for mock yet */
		return null;
	}
	
	@Override
	public Metadata getMetadata(HttpServletRequest request, QueryParameter query) throws Exception {
		CountableMetadata metadata = new CountableMetadata();
		metadata.setTotalCount(mockStorage.size());
		populateFieldInfo(metadata);
		return metadata;
	}

	public void insertModel(MockModelB model) {
		mockStorage.put(model.getId(), model);
	}

	public Map<CMCID, MockModelB> getStorage() {
		return mockStorage;
	}
}
