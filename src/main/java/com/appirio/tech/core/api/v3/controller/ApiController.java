package com.appirio.tech.core.api.v3.controller;


import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

import com.appirio.tech.core.api.v3.ApiVersion;
import com.appirio.tech.core.api.v3.TCID;
import com.appirio.tech.core.api.v3.exception.ExceptionContent;
import com.appirio.tech.core.api.v3.exception.ResourceNotMappedException;
import com.appirio.tech.core.api.v3.exception.handler.ExceptionCallbackHandler;
import com.appirio.tech.core.api.v3.model.AbstractResource;
import com.appirio.tech.core.api.v3.model.ResourceHelper;
import com.appirio.tech.core.api.v3.request.FieldSelector;
import com.appirio.tech.core.api.v3.request.FilterParameter;
import com.appirio.tech.core.api.v3.request.LimitQuery;
import com.appirio.tech.core.api.v3.request.OrderByQuery;
import com.appirio.tech.core.api.v3.request.QueryParameter;
import com.appirio.tech.core.api.v3.response.ApiFieldSelectorResponse;
import com.appirio.tech.core.api.v3.response.ApiResponse;
import com.appirio.tech.core.api.v3.service.RESTActionService;
import com.appirio.tech.core.api.v3.service.RESTMetadataService;
import com.appirio.tech.core.api.v3.service.RESTQueryService;

/**
 * An Entrypoint Controller class for all /api/v3/* endpoints
 * 
 * For request/response protocol, see doc in CMC folder.
 * @see <a href="https://docs.google.com/a/appirio.com/presentation/d/1BLt2Mq_iEu6Az5CAX-cF-Ebg0xlj6rde--1tp0r2iaQ/edit">API Specification</a>
 *
 * @author sudo
 * @param <T>
 *
 */
@RequestMapping("/api/v3")
@Controller
public class ApiController {
	protected final Logger logger = Logger.getLogger(getClass());
	
	private List<ExceptionCallbackHandler> exceptionHandlers;
	private ResourceFactory resourceFactory;
	
	public ApiController() {
		resourceFactory = new ResourceFactory();
	}
	
	/**
	 * Get current {@link ExceptionCallbackHandler} that are registered within this Controller.
	 * 
	 */
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
	
	public void setResourceFactory(ResourceFactory resourceFactory) {
		this.resourceFactory = resourceFactory;
	}
	
	/**
	 * Get current {@link ResourceFactory} that is registered within this Controller
	 * 
	 * @return
	 */
	public ResourceFactory getResourceFactory() {
		return resourceFactory;
	}
	
	@RequestMapping(value={"/"}, method={RequestMethod.GET})
	@ResponseBody
	public ApiResponse version() {
		return createResponse("API Version: " + ApiVersion.v3);
	}
	
	@RequestMapping(value={"/exception"}, method={RequestMethod.GET})
	@ResponseBody
	public ApiResponse exception() {
		throw new RuntimeException("Exception test endpoint");
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
			@RequestParam(value="include", required=false) String include,
			HttpServletRequest request) throws Exception {

		FieldSelector selector;
		if(fields==null || fields.trim().length()==0) {
			selector = ResourceHelper.getDefaultFieldSelector(resourceFactory.getResourceModel(resource));
		} else {
			selector = FieldSelector.instanceFromV2String(fields);
		}
		QueryParameter query = new QueryParameter(selector,
													FilterParameter.fromEncodedString(filter),
													LimitQuery.instanceFromRaw(limit, offset, offsetId),
													OrderByQuery.instanceFromRaw(orderBy));

		RESTQueryService<?> service = resourceFactory.getQueryService(resource);
		List<? extends AbstractResource> models = service.handleGet(request, query);
		
		//attach metadata if requested and MetadataService exists for this resource
		Object metadataObject = null;
		if(include!=null && include.startsWith("metadata")) {
			try {
				RESTMetadataService metaService = resourceFactory.getMetadataService(resource);
				metadataObject = metaService.getMetadata(request, query);
			} catch (ResourceNotMappedException e) {
				metadataObject = "metadata not supported";
			}
		}
		
		return createFieldSelectorResponse(models, metadataObject, query.getSelector());
	}

	@RequestMapping(value="/{resource}/{recordId}", method=GET)
	@ResponseBody
	public ApiResponse getObject(@PathVariable String resource,
			@PathVariable TCID recordId,
			@RequestParam(value="fields", required=false) String fields,
			HttpServletRequest request) throws Exception {
		//add default fields if selector is empty.
		if(fields==null || fields.isEmpty()){
			fields = "";
			for(String field : ResourceHelper.getDefaultFields(resourceFactory.getResourceModel(resource))) {
				fields = field + ",";
			}
			//remove the last ","
			fields = fields.replaceAll(",$", "").trim();
		}

		QueryParameter query = new QueryParameter(FieldSelector.instanceFromV2String(fields));
		RESTQueryService<?> service = resourceFactory.getQueryService(resource);

		AbstractResource model = service.handleGet(query.getSelector(), recordId);
		return createFieldSelectorResponse(model, query.getSelector());
	}

	@RequestMapping(value="/{resource}/{recordId}/{action}", method=RequestMethod.POST, params="action=true")
	@ResponseBody
	public ApiResponse performAction(@PathVariable String resource,
			@PathVariable TCID recordId,
			@PathVariable String action,
			HttpServletRequest request) throws Exception {
		RESTActionService service = resourceFactory.getActionService(resource);
		return service.handleAction(recordId, action, request);
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
				response.setResult(false, exceptionContent.getHttpStatus().value(), exceptionContent);
				res.setStatus(exceptionContent.getHttpStatus().value());
				return response;
			}
		}
		
		return response;
	}

	private ApiResponse createResponse(final Object object) {
		ApiResponse response = new ApiResponse();
		response.setResult(true, HttpStatus.OK.value(), object);
		response.setVersion(ApiVersion.v3);
		return response;
	}

	private ApiFieldSelectorResponse createFieldSelectorResponse(List<? extends AbstractResource> object, Object metadata, FieldSelector selector) {
		ApiFieldSelectorResponse response = new ApiFieldSelectorResponse();
		Map<Integer, Set<String>> fieldSelectionMap = new HashMap<Integer, Set<String>>();
		for(AbstractResource resource : object) {
			ResourceHelper.setSerializeFields(resource, selector, fieldSelectionMap);
		}
		response.setResult(true, HttpStatus.OK.value(), metadata, object);
		response.setVersion(ApiVersion.v3);
		response.setFieldSelectionMap(fieldSelectionMap);
		return response;
	}

	private ApiFieldSelectorResponse createFieldSelectorResponse(final AbstractResource object, FieldSelector selector) {
		ApiFieldSelectorResponse response = new ApiFieldSelectorResponse();
		response.setResult(true, HttpStatus.OK.value(), object);
		response.setVersion(ApiVersion.v3);
		Map<Integer, Set<String>> fieldSelectionMap = new HashMap<Integer, Set<String>>();
		ResourceHelper.setSerializeFields(object, selector, fieldSelectionMap);
		response.setFieldSelectionMap(fieldSelectionMap);
		return response;
	}
}
