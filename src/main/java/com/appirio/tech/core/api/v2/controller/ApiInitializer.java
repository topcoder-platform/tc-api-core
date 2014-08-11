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

import com.appirio.tech.core.api.v2.exception.handler.ExceptionCallbackHandler;
import com.appirio.tech.core.api.v2.model.CMCResource;
import com.appirio.tech.core.api.v2.service.RESTActionService;
import com.appirio.tech.core.api.v2.service.RESTQueryService;

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
		Map<String, RESTActionService> actionServiceMap = new HashMap<String, RESTActionService>();
		Map<String, Class<? extends CMCResource>> modelMap = new HashMap<String, Class<? extends CMCResource>>();
		
		ResourceFactory factory = new ResourceFactory();
		factory.setup(queryServiceMap, actionServiceMap, modelMap);
		ctrl.setResourceFactory(factory);

		logComplete(handlerList, queryServiceMap, actionServiceMap, modelMap);
	}

	/**
	 * @param handlerList
	 * @param queryServiceMap
	 * @param actionServiceMap
	 * @param modelMap
	 */
	private void logComplete(List<ExceptionCallbackHandler> handlerList, Map<String, RESTQueryService> queryServiceMap,
			Map<String, RESTActionService> actionServiceMap, Map<String, Class<? extends CMCResource>> modelMap) {
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
			builder.append("\tRESTActionService...").append("\n");
			for(RESTActionService actionService : actionServiceMap.values()) {
				builder.append("\t\t:" + i++ + ":" + actionService.getClass().getCanonicalName()).append("\n");
			}
			
			i=0;
			builder.append("\tModel Classes...").append("\n");
			for(Class<?> model : modelMap.values()) {
				builder.append("\t\t:" + i++ + ":" + model.getClass().getCanonicalName()).append("\n");
			}
			
			logger.debug(builder.toString());
		}
	}
}
