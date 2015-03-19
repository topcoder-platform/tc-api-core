package com.appirio.tech.core.api.v3.resource;

import io.dropwizard.auth.Auth;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;

import com.appirio.tech.core.api.v3.request.PostPutRequest;
import com.appirio.tech.core.api.v3.response.ApiResponse;
import com.appirio.tech.core.auth.AuthUser;
import com.codahale.metrics.annotation.Timed;

public interface DDLResource<T> {

	/**
	 * Handles the post request.
	 * 
	 * @param postRequest
	 *            the post request data.
	 * @param request
	 *            the HttpServletRequest
	 * @return api response
	 * @throws Exception
	 *             if any error occurs
	 */
	@POST
	@Timed
	public abstract ApiResponse createObject(
			@Auth AuthUser authUser,
			@Valid PostPutRequest<T> postRequest,
			@Context HttpServletRequest request) throws Exception;

	/**
	 * Handles the post request.
	 * 
	 * @param putRequest
	 *            the post request data.
	 * @param request
	 *            the HttpServletRequest
	 * @return api response
	 * @throws Exception
	 *             if any error occurs
	 */
	@PUT
	@Path("/{resourceId}")
	@Timed
	public abstract ApiResponse updateObject(
			@Auth AuthUser authUser,
			@PathParam("resourceId") String resourceId,
			@Valid PostPutRequest<T> putRequest,
			@Context HttpServletRequest request) throws Exception;

	/**
	 * Handles the delete request.
	 * 
	 * @param resourceId
	 *            the resource to delete.
	 * @param request
	 *            the HttpServletRequest
	 * @return
	 * @throws Exception
	 */
	@DELETE
	@Path("/{resourceId}")
	@Timed
	public abstract ApiResponse deleteObject(
			@Auth AuthUser authUser,
			@PathParam("resourceId") String resourceId,
			@Context HttpServletRequest request) throws Exception;

}