/**
 * 
 */
package com.appirio.tech.core.api.v3.controller;

import java.util.Map;

import com.appirio.tech.core.api.v3.exception.ResourceNotMappedException;
import com.appirio.tech.core.api.v3.model.AbstractResource;
import com.appirio.tech.core.api.v3.service.RESTActionService;
import com.appirio.tech.core.api.v3.service.RESTMetadataService;
import com.appirio.tech.core.api.v3.service.RESTPersistentService;
import com.appirio.tech.core.api.v3.service.RESTQueryService;

/**
 * @author sudo
 *
 */
public class ResourceFactory {

	private Map<String, RESTQueryService<? extends AbstractResource>> queryServiceMap;
	private Map<String, RESTMetadataService> metadataMap;
	private Map<String, RESTPersistentService<? extends AbstractResource>> persistentServiceMap;
	private Map<String, RESTActionService> actionServiceMap;
	private Map<String, Class<? extends AbstractResource>> modelMap;
	
	public void setup(Map<String, RESTQueryService<? extends AbstractResource>> queryServiceMap,
			Map<String, RESTMetadataService> metadataMap,
			Map<String, RESTPersistentService<? extends AbstractResource>> persistentServiceMap,
			Map<String, RESTActionService> actionServiceMap,
			Map<String, Class<? extends AbstractResource>> modelMap) {
		this.queryServiceMap = queryServiceMap;
		this.metadataMap = metadataMap;
		this.persistentServiceMap = persistentServiceMap;
		this.actionServiceMap = actionServiceMap;
		this.modelMap = modelMap;
	}

	public RESTQueryService<? extends AbstractResource> getQueryService(String resource) throws ResourceNotMappedException {
		if(queryServiceMap.containsKey(resource)) {
			return queryServiceMap.get(resource);
		} else {
			throw new ResourceNotMappedException("unknown resource:" + resource);
		}
	}
	
	public RESTPersistentService<? extends AbstractResource> getPersistentService(String resource) throws ResourceNotMappedException {
		if(persistentServiceMap.containsKey(resource)) {
			return persistentServiceMap.get(resource);
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
	
	public Class<? extends AbstractResource> getResourceModel(String resource) throws Exception {
		if(modelMap.containsKey(resource)) {
			return modelMap.get(resource);
		} else {
			throw new ResourceNotMappedException("unknown resource");
		}
	}

	public RESTMetadataService getMetadataService(String resource) {
		System.out.println(metadataMap.keySet());
		if(metadataMap.containsKey(resource)) {
			return metadataMap.get(resource);
		} else {
			throw new ResourceNotMappedException("unknown resource");
		}
	}
}
