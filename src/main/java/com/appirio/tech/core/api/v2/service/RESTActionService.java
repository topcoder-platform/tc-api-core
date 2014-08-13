/**
 * 
 */
package com.appirio.tech.core.api.v2.service;

import javax.servlet.http.HttpServletRequest;

import com.appirio.tech.core.api.v2.CMCID;
import com.appirio.tech.core.api.v2.response.ApiResponse;

/**
 * Interface to handle "action" calls to the resouces.
 * Action comes in the form of /{resource}/{action} or /{resource}/{resourceId}/{action}
 * 
 * @see CMC API V2 specification doc. for details.
 * @author sudo
 *
 */
public interface RESTActionService extends RESTService {
	ApiResponse handleAction(CMCID recordId, String action, HttpServletRequest request) throws Exception;
}
