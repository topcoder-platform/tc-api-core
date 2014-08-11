package com.appirio.tech.core.api.v2.controller;


import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.rmi.server.UID;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.appirio.tech.core.api.v2.ApiVersion;
import com.appirio.tech.core.api.v2.CMCID;
import com.appirio.tech.core.api.v2.ExceptionContent;
import com.appirio.tech.core.api.v2.exception.handler.ExceptionCallbackHandler;
import com.appirio.tech.core.api.v2.exception.handler.ResourceNotMappedHandler;
import com.appirio.tech.core.api.v2.exception.handler.RootExceptionCallbackHandler;
import com.appirio.tech.core.api.v2.model.CMCResource;
import com.appirio.tech.core.api.v2.model.CMCResourceHelper;
import com.appirio.tech.core.api.v2.request.FieldSelector;
import com.appirio.tech.core.api.v2.request.FilterParameter;
import com.appirio.tech.core.api.v2.request.LimitQuery;
import com.appirio.tech.core.api.v2.request.OrderByQuery;
import com.appirio.tech.core.api.v2.request.QueryParameter;
import com.appirio.tech.core.api.v2.response.ApiResponse;
import com.appirio.tech.core.api.v2.service.RESTActionService;
import com.appirio.tech.core.api.v2.service.RESTQueryService;

/**
 * An Entrypoint Controller class for all /api/v2/* endpoints
 * 
 * For request/response protocol, see doc in CMC folder.
 * @see <a href="https://docs.google.com/a/appirio.com/presentation/d/1BLt2Mq_iEu6Az5CAX-cF-Ebg0xlj6rde--1tp0r2iaQ/edit">API Specification</a>
 *
 * @author sudo
 * @param <T>
 *
 */
@RequestMapping("/api/v2")
@Controller
public class ApiController<T extends CMCResource> {
	protected final Logger logger = Logger.getLogger(getClass());
	
	private List<ExceptionCallbackHandler> exceptionHandlers;
	private ResourceFactory<T> resourceFactory;
	
	public ApiController() {
		exceptionHandlers = new ArrayList<ExceptionCallbackHandler>();
		exceptionHandlers.add(new ResourceNotMappedHandler());
		exceptionHandlers.add(new RootExceptionCallbackHandler());
		
		resourceFactory = new ResourceFactory<T>();
	}
	
	public List<ExceptionCallbackHandler> getExceptionHandlers() {
		return exceptionHandlers;
	}
	
	/**
	 * Set ExceptionCallbackHander.
	 * ExceptionCallbackHandler will be called when Exceptions are propagated to response method.
	 * First handler in the list will be examined first, last the last.
	 * 
	 * @param exceptionHandlers
	 */
	public void setExceptionHandlers(List<ExceptionCallbackHandler> exceptionHandlers) {
		this.exceptionHandlers = exceptionHandlers;
	}
	
	public ResourceFactory<T> getResourceFactory() {
		return resourceFactory;
	}
	
	@RequestMapping(value={"/"}, method={RequestMethod.GET})
	@ResponseBody
	public ApiResponse version() {
		return createResponse("API Version: " + ApiVersion.v2);
	}
	
	@RequestMapping(value={"/exception"}, method={RequestMethod.GET})
	@ResponseBody
	public ApiResponse exception() {
		throw new RuntimeException("test");
	}
	


	@RequestMapping(value="/{resource}", method=GET)
	@ResponseBody
	public ApiResponse getObjects(@PathVariable String resource,
			@RequestParam(value="fields", required=false) String fields,
			@RequestParam(value="filter", required=false) String filter,
			@RequestParam(value="limit", required=false) String limit,
			@RequestParam(value="offset", required=false) String offset,
			@RequestParam(value="offsetId", required=false) String offsetId,
			@RequestParam(value="orderBy", required=false) String orderBy,
			HttpServletRequest request) throws Exception {

		FieldSelector selector;
		if(filter==null || filter.trim().length()==0) {
			selector = CMCResourceHelper.getDefaultFieldSelector(resourceFactory.getResourceModel(resource));
		} else {
			selector = FieldSelector.instanceFromV2String(fields);
		}
		QueryParameter query = new QueryParameter(selector,
													new FilterParameter(filter),
													LimitQuery.instanceFromRaw(limit, offset, offsetId),
													OrderByQuery.instanceFromRaw(orderBy));
		RESTQueryService service = resourceFactory.getQueryService(resource);

		List<? extends CMCResource> models = service.handleGet(request, query);
		return getResponse(models, query.getFieldSelector());
	}

	@RequestMapping(value="/{resource}/{recordId}", method=GET)
	@ResponseBody
	public ApiResponse getObject(@PathVariable String resource,
			@PathVariable CMCID recordId,
			@RequestParam(value="fields", required=false) String fields,
			HttpServletRequest request) throws Exception {
		//add default fields if selector is empty.
		if(fields==null || fields.isEmpty()){
			fields = "";
			for(String field : CMCResourceHelper.getDefaultFields(resourceFactory.getResourceModel(resource))) {
				fields = field + ",";
			}
			//remove the last ","
			fields = fields.replaceAll(",$", "").trim();
		}

		QueryParameter query = new QueryParameter(FieldSelector.instanceFromV2String(fields));
		RESTQueryService service = resourceFactory.getQueryService(resource);

		CMCResource model = service.handleGet(query.getFieldSelector(), recordId);
		return getResponse(model, query.getFieldSelector());
	}

	@RequestMapping(value="/{resource}/{recordId}/{action}", method=RequestMethod.POST)
	@ResponseBody
	public ApiResponse performAction(@PathVariable String resource,
			@PathVariable CMCID recordId,
			@PathVariable String action,
			HttpServletRequest request) throws Exception {
		RESTActionService service = resourceFactory.getActionService(resource);
		return service.handleAction(recordId, action, request);
	}

	private ApiResponse getResponse(CMCResource object, FieldSelector selector) {
		CMCResourceHelper.setSerializeFields(object, selector);
		return createResponse(object);
	}

	private ApiResponse getResponse(List<? extends CMCResource> objects, FieldSelector selector) {
		for (CMCResource object : objects) {
			CMCResourceHelper.setSerializeFields(object, selector);
		}
		return createResponse(objects);
	}

	/**
	 * final Handle-All Exception handler.
	 *
	 * @param ex
	 * @param request
	 * @return
	 */
	@ExceptionHandler(Throwable.class)
	@ResponseBody
	public ApiResponse handleException(Throwable ex, HttpServletRequest request, HttpServletResponse res) {
		ExceptionContent exceptionContent;
		ApiResponse response = new ApiResponse();
		
		//the order of instanceof should be the deepest order that we want to handle
		for(ExceptionCallbackHandler handler : exceptionHandlers) {
			if(handler.isHandle(ex)) {
				exceptionContent = handler.getExceptionContent(ex, request, res);
				UID uid = new UID();
				response.setId(uid.toString());
				response.setResult(false, exceptionContent.getHttpStatus().value(), exceptionContent);
				res.setStatus(exceptionContent.getHttpStatus().value());
				return response;
			}
		}
		
		return response;
	}

	protected ApiResponse createResponse(final Object object) {
		ApiResponse response = new ApiResponse();
		response.setId((new UID()).toString());
		response.setResult(true, HttpStatus.OK.value(), object);
		response.setVersion(ApiVersion.v2);
		return response;
	}
}
