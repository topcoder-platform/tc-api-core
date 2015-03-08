/**
 * 
 */
package com.appirio.tech.core.api.v3.resource.old;

import javax.servlet.http.HttpServletRequest;

import com.appirio.tech.core.api.v3.TCID;
import com.appirio.tech.core.api.v3.response.ApiResponse;

/**
 * Interface to handle "action" calls to the resouces.
 * Action comes in the form of /{resource}/{action} or /{resource}/{resourceId}/{action}
 * 
 * @see CMC API V2 specification doc. for details.
 * @author sudo
 *
 */
public interface RESTActionService extends RESTService {
	ApiResponse handleAction(String action, HttpServletRequest request) throws Exception;
	
	ApiResponse handleAction(TCID recordId, String action, HttpServletRequest request) throws Exception;
}
