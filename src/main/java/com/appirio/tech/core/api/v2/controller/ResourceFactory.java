/**
 * 
 */
package com.appirio.tech.core.api.v2.controller;

import java.util.Map;

import com.appirio.tech.core.api.v2.exception.ResourceNotMappedException;
import com.appirio.tech.core.api.v2.model.CMCResource;
import com.appirio.tech.core.api.v2.service.RESTActionService;
import com.appirio.tech.core.api.v2.service.RESTQueryService;

/**
 * @author sudo
 *
 */
public class ResourceFactory {

	private Map<String, RESTQueryService> queryServiceMap;
	private Map<String, RESTActionService> actionServiceMap;
	private Map<String, Class<? extends CMCResource>> modelMap;
	
	public void setup(Map<String, RESTQueryService> queryServiceMap,
			Map<String, RESTActionService> actionServiceMap,
			Map<String, Class<? extends CMCResource>> modelMap) {
		this.queryServiceMap = queryServiceMap;
		this.actionServiceMap = actionServiceMap;
		this.modelMap = modelMap;
	}

	public RESTQueryService getQueryService(String resource) throws Exception {
		if(queryServiceMap.containsKey(resource)) {
			return queryServiceMap.get(resource);
		} else {
			throw new ResourceNotMappedException("unknown resource:" + resource);
		}
	}
	
	public RESTActionService getActionService(String resource) {
		if(actionServiceMap.containsKey(resource)) {
			return actionServiceMap.get(resource);
		} else {
			throw new ResourceNotMappedException("unknown resource:" + resource);
		}
	}
	
	public Class<? extends CMCResource> getResourceModel(String resource) throws Exception {
		if(modelMap.containsKey(resource)) {
			return modelMap.get(resource);
		} else {
			throw new ResourceNotMappedException("unknown resource");
		}
	}
}
