/**
 * 
 */
package com.appirio.tech.core.api.v2.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.appirio.tech.core.api.v2.exception.ResourceInitializationException;
import com.appirio.tech.core.api.v2.exception.handler.ExceptionCallbackHandler;
import com.appirio.tech.core.api.v2.model.CMCResource;
import com.appirio.tech.core.api.v2.service.RESTActionService;
import com.appirio.tech.core.api.v2.service.RESTPersistentService;
import com.appirio.tech.core.api.v2.service.RESTQueryService;
import com.appirio.tech.core.api.v2.service.RESTService;

/**
 * Initialized ApiController after Spring context has been initialized (all bean registered)
 * 
 * @author sudo
 *
 */
@Component("apiInitializer")
public class ApiInitializer implements ApplicationListener<ContextRefreshedEvent>{
	
	protected final Logger logger = Logger.getLogger(getClass());
	
	@Autowired
	ApplicationContext context;

	public void onApplicationEvent(ContextRefreshedEvent event) {
		logger.debug("Initializing ApiController...");
		
		ApiController ctrl = context.getBean(ApiController.class);
		
		///////////////////////////////////////////////////////////////////////////
		//register ExceptionHandlers
		Map<String, ExceptionCallbackHandler> exceptionHandlersMap = context.getBeansOfType(ExceptionCallbackHandler.class);
		List<ExceptionCallbackHandler> handlerList = new ArrayList<ExceptionCallbackHandler>(exceptionHandlersMap.values());
		ctrl.setExceptionHandlers(handlerList);
		
		///////////////////////////////////////////////////////////////////////////
		//register ResourceFactory getting all REST handlers
		Map<String, RESTQueryService> queryServiceMap = new HashMap<String, RESTQueryService>();
		setupService(queryServiceMap, RESTQueryService.class);
		
		Map<String, RESTPersistentService> persistentServiceMap = new HashMap<String, RESTPersistentService>();
		setupService(persistentServiceMap, RESTPersistentService.class);
		
		Map<String, RESTActionService> actionServiceMap = new HashMap<String, RESTActionService>();
		setupService(actionServiceMap, RESTActionService.class);

		Map<String, Class<? extends CMCResource>> modelMap = new HashMap<String, Class<? extends CMCResource>>();
		Map<String, CMCResource> modelBeans = context.getBeansOfType(CMCResource.class);
		for(CMCResource model: modelBeans.values()) {
			if(modelBeans.containsKey(model.getResourcePath())) {
				throw new ResourceInitializationException("Duplicate Service detected during initialization. " + model.getResourcePath());
			}
			modelMap.put(model.getResourcePath(), model.getClass());
		}
		
		ResourceFactory factory = new ResourceFactory();
		factory.setup(queryServiceMap, persistentServiceMap, actionServiceMap, modelMap);
		ctrl.setResourceFactory(factory);

		logComplete(handlerList, queryServiceMap, persistentServiceMap, actionServiceMap, modelMap);
	}

	private <T extends RESTService> void setupService(Map<String, T> serviceMap, Class<T> clazz) {
		Map<String, T> serviceBeans = context.getBeansOfType(clazz);
		for(T queryService : serviceBeans.values()) {
			if(serviceMap.containsKey(queryService.getResourcePath())) {
				throw new ResourceInitializationException("Duplicate Service detected during initialization. " + queryService.getResourcePath());
			}
			serviceMap.put(queryService.getResourcePath(), (T)queryService);
		}
	}

	/**
	 * @param handlerList
	 * @param queryServiceMap
	 * @param actionServiceMap
	 * @param modelMap
	 */
	private void logComplete(List<ExceptionCallbackHandler> handlerList, Map<String, RESTQueryService> queryServiceMap,
			Map<String, RESTPersistentService> persistentServiceMap, Map<String, RESTActionService> actionServiceMap,
			Map<String, Class<? extends CMCResource>> modelMap) {
		if(logger.isDebugEnabled()) {
			StringBuilder builder = new StringBuilder();
			builder.append("ApiController Initialization complete:").append("\n");
			int i=0;

			builder.append("\tExceptionHandlers...").append("\n");
			for(ExceptionCallbackHandler handler : handlerList) {
				builder.append("\t\t:" + i++ + ":" + handler.getClass().getCanonicalName()).append("\n");
			}
			
			i=0;
			builder.append("\tRESTQueryService...").append("\n");
			for(RESTQueryService queryService : queryServiceMap.values()) {
				builder.append("\t\t:" + i++ + ":" + queryService.getClass().getCanonicalName()).append("\n");
			}
			
			i=0;
			builder.append("\tRESTPersistentService...").append("\n");
			for(RESTPersistentService persistentService : persistentServiceMap.values()) {
				builder.append("\t\t:" + i++ + ":" + persistentService.getClass().getCanonicalName()).append("\n");
			}
			
			i=0;
			builder.append("\tRESTActionService...").append("\n");
			for(RESTActionService actionService : actionServiceMap.values()) {
				builder.append("\t\t:" + i++ + ":" + actionService.getClass().getCanonicalName()).append("\n");
			}
			
			i=0;
			builder.append("\tModel Classes...").append("\n");
			for(Class<?> model : modelMap.values()) {
				builder.append("\t\t:" + i++ + ":" + model.getCanonicalName()).append("\n");
			}
			
			logger.debug(builder.toString());
		}
	}
}
