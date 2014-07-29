/**
 * 
 */
package com.appirio.tech.core.api.v2.controller;

import java.util.HashMap;
import java.util.Map;

import com.appirio.tech.core.api.v2.exception.CMCParseException;
import com.appirio.tech.core.api.v2.model.CMCResource;
import com.appirio.tech.core.api.v2.service.RESTActionService;
import com.appirio.tech.core.api.v2.service.RESTQueryService;

/**
 * @author sudo
 *
 */
public class ResourceFactory<T extends CMCResource> {

	private Map<String, RESTQueryService> queryServiceMap;
	private Map<String, RESTActionService> actionServiceMap;
	private Map<String, Class<T>> modelMap;
	
	public ResourceFactory() {
		queryServiceMap = new HashMap<String, RESTQueryService>();
		actionServiceMap = new HashMap<String, RESTActionService>();
		modelMap = new HashMap<String, Class<T>>();
	}

	public RESTQueryService getQueryService(String resource) throws Exception {
		if(queryServiceMap.containsKey(resource)) {
			return queryServiceMap.get(resource);
		} else {
			throw new CMCParseException("unknown resource:" + resource);
		}
	}
	
	public RESTActionService getActionService(String resource) {
		if(actionServiceMap.containsKey(resource)) {
			return actionServiceMap.get(resource);
		} else {
			throw new CMCParseException("unknown resource:" + resource);
		}
	}
	
	public Class<T> getResourceModel(String resource) throws Exception {
		if(modelMap.containsKey(resource)) {
			return modelMap.get(resource);
		} else {
			throw new CMCParseException("unknown resource");
		}
	}
}
