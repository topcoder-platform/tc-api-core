/**
 * 
 */
package com.appirio.tech.core.api.v3.mock.a;

import io.dropwizard.auth.Auth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.appirio.tech.core.api.v3.TCID;
import com.appirio.tech.core.api.v3.request.FieldSelector;
import com.appirio.tech.core.api.v3.request.QueryParameter;
import com.appirio.tech.core.api.v3.request.annotation.APIFieldParam;
import com.appirio.tech.core.api.v3.request.annotation.APIQueryParam;
import com.appirio.tech.core.api.v3.resource.GetResource;
import com.appirio.tech.core.api.v3.response.ApiResponse;
import com.appirio.tech.core.api.v3.response.ApiResponseFactory;
import com.appirio.tech.core.auth.AuthUser;
import com.codahale.metrics.annotation.Timed;

/**
 * Test resource
 * 
 * @author sudo
 *
 */
@Path("mock_a_models")
@Produces(MediaType.APPLICATION_JSON)
public class MockQueryResource implements GetResource<MockModelA> {

	private Map<TCID, MockModelA> mockStorage = new HashMap<TCID, MockModelA>();
	
	@Override
	@GET
	@Path("/{resourceId}")
	@Timed
	public ApiResponse getObject(
			@Auth(required=false) AuthUser authUser,
			@PathParam("resourceId") TCID recordId,
			@APIFieldParam(repClass = MockModelA.class) FieldSelector selector, @Context HttpServletRequest request)
			throws Exception {
		return null;
	}

	@Override
	@GET
	@Timed
	public ApiResponse getObjects(
			@Auth(required=false) AuthUser authUser,
			@APIQueryParam(repClass = MockModelA.class) QueryParameter query,
			@Context HttpServletRequest request) throws Exception {
		List<MockModelA> result = new ArrayList<MockModelA>(mockStorage.values());
		return ApiResponseFactory.createFieldSelectorResponse(result, query.getSelector());
	}
}
