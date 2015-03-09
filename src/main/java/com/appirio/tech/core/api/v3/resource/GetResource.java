package com.appirio.tech.core.api.v3.resource;

import io.dropwizard.auth.Auth;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;

import com.appirio.tech.core.api.v3.TCID;
import com.appirio.tech.core.api.v3.request.FieldSelector;
import com.appirio.tech.core.api.v3.request.QueryParameter;
import com.appirio.tech.core.api.v3.response.ApiResponse;
import com.appirio.tech.core.auth.AuthUser;
import com.codahale.metrics.annotation.Timed;

public interface GetResource<T> {

	/**
	 * A method to catch all requests to /{resource}/{id}.
	 * The method returns list of resources per V3 API spec.
	 * 
	 * @param recordId
	 * @param fieldsIn
	 * @param request
	 *            the HttpServletRequest
	 * @return api response
	 * @throws Exception
	 *             if any error occurs
	 */
	@GET
	@Path("/{resourceId}")
	@Timed
	public abstract ApiResponse getObject(
			@Auth AuthUser authUser,
			@PathParam("resourceId") TCID recordId,
			/*@APIFieldParam(repClass =T.class)*/ FieldSelector selector,
			@Context HttpServletRequest request) throws Exception;

	/**
	 * A method to catch all requests to /{resource}.
	 * The method returns list of resources per V3 API spec.
	 * 
	 * @param query
	 * @param includeIn
	 * @param request
	 *            the HttpServletRequest
	 * @return api response
	 * @throws Exception
	 *             if any error occurs
	 */
	@GET
	@Timed
	public abstract ApiResponse getObjects(
			@Auth AuthUser authUser,
			/*@APIQueryParam(repClass = User.class)*/ QueryParameter query,
			@Context HttpServletRequest request) throws Exception;

}