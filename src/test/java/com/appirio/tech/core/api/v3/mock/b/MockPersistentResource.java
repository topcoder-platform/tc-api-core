/**
 * 
 */
package com.appirio.tech.core.api.v3.mock.b;

import io.dropwizard.auth.Auth;
import io.dropwizard.jersey.PATCH;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.joda.time.DateTime;

import com.appirio.tech.core.api.v3.TCID;
import com.appirio.tech.core.api.v3.metadata.CountableMetadata;
import com.appirio.tech.core.api.v3.metadata.Metadata;
import com.appirio.tech.core.api.v3.request.FieldSelector;
import com.appirio.tech.core.api.v3.request.FilterParameter;
import com.appirio.tech.core.api.v3.request.PatchRequest;
import com.appirio.tech.core.api.v3.request.PostPutRequest;
import com.appirio.tech.core.api.v3.request.QueryParameter;
import com.appirio.tech.core.api.v3.request.annotation.APIFieldParam;
import com.appirio.tech.core.api.v3.request.annotation.APIQueryParam;
import com.appirio.tech.core.api.v3.resource.DDLResource;
import com.appirio.tech.core.api.v3.resource.GetResource;
import com.appirio.tech.core.api.v3.resource.PatchResource;
import com.appirio.tech.core.api.v3.response.ApiResponse;
import com.appirio.tech.core.api.v3.response.ApiResponseFactory;
import com.appirio.tech.core.auth.AuthUser;
import com.codahale.metrics.annotation.Timed;

/**
 * Mock Service class that handles all the REST calls.
 * 
 * @author sudo
 *
 */
@Path("mock_b_models")
@Produces(MediaType.APPLICATION_JSON)
public class MockPersistentResource implements GetResource<MockModelB>, DDLResource<MockModelB>, PatchResource {

	private AtomicInteger integer = new AtomicInteger(100);
	private static Map<TCID, MockModelB> mockStorage = new HashMap<TCID, MockModelB>();

	@Override
	@GET
	@Path("/{resourceId}")
	@Timed
	public ApiResponse getObject(
			@Auth AuthUser authUser,
			@PathParam("resourceId") TCID recordId,
			@APIFieldParam(repClass = MockModelB.class) FieldSelector selector,
			@Context HttpServletRequest request) throws Exception {
		return ApiResponseFactory.createFieldSelectorResponse(mockStorage.get(recordId), selector);
	}

	@GET
	@Path("/responseNull")
	@Timed
	public ApiResponse getApiResponseWithNullContent(
			@Auth AuthUser authUser,
			@APIFieldParam(repClass = MockModelB.class) FieldSelector selector,
			@Context HttpServletRequest request) throws Exception {
		return ApiResponseFactory.createResponse(null);
	}

	@GET
	@Path("/selectorNull")
	@Timed
	public ApiResponse getApiFieldSelectorResponseWithNullContent(
			@Auth AuthUser authUser,
			@APIFieldParam(repClass = MockModelB.class) FieldSelector selector,
			@Context HttpServletRequest request) throws Exception {
		return ApiResponseFactory.createFieldSelectorResponse((MockModelB)null, selector);
	}


	@Override
	@GET
	@Timed
	public ApiResponse getObjects(
			@Auth AuthUser authUser,
			@APIQueryParam(repClass = MockModelB.class) QueryParameter query,
			@Context HttpServletRequest request) throws Exception {
		List<MockModelB> result = new ArrayList<MockModelB>();
		Collection<MockModelB> values = mockStorage.values();
		
		FilterParameter filter = query.getFilter();
		Map<String, Object> params = filter.getParamMap();
		String strParam = (String)params.get("strTest");
		String intParam = (String)params.get("intTest");
		for(MockModelB m : values) {
			boolean hit = (strParam==null && intParam==null);
			if(!hit)
				hit = strParam!=null && strParam.equals(m.getStrTest());
			if(!hit)
				hit = intParam!=null && Integer.parseInt(intParam) == m.getIntTest();
			if(hit)
				result.add(m);
		}
		return ApiResponseFactory.createFieldSelectorResponse(
				result,
				getMetadata(request, query),
				query.getSelector());
	}

	@Override
	@POST
	@Timed
	public ApiResponse createObject(
			@Auth AuthUser authUser,
			@Valid PostPutRequest<MockModelB> postRequest,
			@Context HttpServletRequest request) throws Exception {

		MockModelB resourceData = postRequest.getParam();

		// call the RESTPersistentService.handlePost
		TCID id = new TCID(integer.getAndIncrement());
		resourceData.setId(id);
		resourceData.setModifiedAt(new DateTime());
		resourceData.setCreatedAt(new DateTime());
		mockStorage.put(id, resourceData);
		TCID newID = resourceData.getId();

		resourceData.setId(newID);

		String selector = (postRequest.getReturn()==null||postRequest.getReturn().isEmpty()) ? ApiResponseFactory.DEFAULT_DDL_RETURN_FIELDS : postRequest.getReturn();
		return ApiResponseFactory.createFieldSelectorResponse(resourceData, FieldSelector.instanceFromV2String(selector));
	}

	@Override
	@PUT
	@Path("/{resourceId}")
	@Timed
	public ApiResponse updateObject(
			@Auth AuthUser authUser,
			@PathParam("resourceId") String resourceId,
			@Valid PostPutRequest<MockModelB> putRequest,
			@Context HttpServletRequest request) throws Exception {

		MockModelB resourceData = putRequest.getParam();
		resourceData.setId(new TCID(resourceId));

		MockModelB modelB = mockStorage.get(resourceData.getId());
		modelB.setIntTest(resourceData.getIntTest());
		modelB.setStrTest(resourceData.getStrTest());
		resourceData.setModifiedAt(new DateTime());
		TCID newID = resourceData.getId();

		resourceData.setId(newID);

		String selector = (putRequest.getReturn()==null||putRequest.getReturn().isEmpty()) ? ApiResponseFactory.DEFAULT_DDL_RETURN_FIELDS : putRequest.getReturn();
		return ApiResponseFactory.createFieldSelectorResponse(resourceData, FieldSelector.instanceFromV2String(selector));
	}

	@Override
	@DELETE
	@Path("/{resourceId}")
	@Timed
	public ApiResponse deleteObject(
			@Auth AuthUser authUser,
			@PathParam("resourceId") String resourceId,
			@Context HttpServletRequest request) throws Exception {
		mockStorage.remove(new TCID(resourceId));
		return ApiResponseFactory.createResponse(new TCID(resourceId));
	}

	@Override
	@PATCH
	@Path("/{resourceId}")
	@Timed
	public ApiResponse updateObject(
			@Auth AuthUser authUser,
			@PathParam("resourceId") String resourceId,
			@Valid PatchRequest patchRequest,
			@Context HttpServletRequest request) throws Exception {
		
		Map<String, String> param = patchRequest.getParam();

		MockModelB modelB = mockStorage.get(new TCID(resourceId));
		
		if(param.containsKey("intTest")) {
			modelB.setIntTest(Integer.valueOf(param.get("intTest")));
		}
		if(param.containsKey("strTest")) {
			modelB.setStrTest(String.valueOf(param.get("strTest")));
		}
		modelB.setModifiedAt(new DateTime());

		String selector = (patchRequest.getReturn()==null||patchRequest.getReturn().isEmpty()) ? ApiResponseFactory.DEFAULT_DDL_RETURN_FIELDS : patchRequest.getReturn();
		return ApiResponseFactory.createFieldSelectorResponse(modelB, FieldSelector.instanceFromV2String(selector));
	}

	public Metadata getMetadata(HttpServletRequest request, QueryParameter query) throws Exception {
		CountableMetadata metadata = new CountableMetadata();
		metadata.setTotalCount(mockStorage.size());
		//populateFieldInfo(metadata);
		return metadata;
	}

	public void insertModel(MockModelB model) {
		mockStorage.put(model.getId(), model);
	}

	public static Map<TCID, MockModelB> getStorage() {
		return mockStorage;
	}

	public static void clearData() {
		mockStorage = new HashMap<TCID, MockModelB>();
	}

}
