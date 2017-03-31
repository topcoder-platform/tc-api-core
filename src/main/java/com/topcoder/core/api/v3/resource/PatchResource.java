package com.topcoder.core.api.v3.resource;

import io.dropwizard.auth.Auth;
import io.dropwizard.jersey.PATCH;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;

import com.codahale.metrics.annotation.Timed;
import com.topcoder.core.api.v3.request.PatchRequest;
import com.topcoder.core.api.v3.response.ApiResponse;
import com.topcoder.core.auth.AuthUser;

public interface PatchResource {

	/**
	 * Handles the patch request.
	 * 
	 * @param patchRequest
	 *            the patch request data.
	 * @param request
	 *            the HttpServletRequest
	 * @return api response
	 * @throws Exception
	 *             if any error occurs
	 */
	@PATCH
	@Path("/{resourceId}")
	@Timed
	public abstract ApiResponse updateObject(
			@Auth AuthUser authUser,
			@PathParam("resourceId") String resourceId,
			@Valid PatchRequest patchRequest,
			@Context HttpServletRequest request) throws Exception;

}