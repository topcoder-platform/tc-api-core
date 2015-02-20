/**
 * 
 */
package com.appirio.tech.core.api.v3;

import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;

import com.appirio.tech.core.api.mock.a.MockModelA;
import com.appirio.tech.core.api.mock.b.MockModelB;
import com.appirio.tech.core.api.v3.controller.ApiController;
import com.appirio.tech.core.api.v3.controller.ResourceFactory;
import com.appirio.tech.core.api.v3.exception.handler.ExceptionCallbackHandler;
import com.appirio.tech.core.api.v3.service.RESTActionService;
import com.appirio.tech.core.api.v3.service.RESTPersistentService;
import com.appirio.tech.core.api.v3.service.RESTQueryService;

/**
 * Tests initializing properties after Spring context loads.
 * 
 * @author sudo
 *
 */
public class InitializerTest extends ControllerTestBase {

	@Test
	public void testExceptionHandlers() {
		//Get all ExceptionHandlers in the class file and assert them via Controller.
		Map<String, ExceptionCallbackHandler> handlers = webApplicationContext.getBeansOfType(ExceptionCallbackHandler.class);
		Assert.assertNotNull(handlers);
		int handlerCount = handlers.values().size();
		
		ApiController ctrl = webApplicationContext.getBean(ApiController.class);
		Assert.assertNotNull(ctrl);
		
		List<ExceptionCallbackHandler> registered = ctrl.getExceptionHandlers();
		Assert.assertTrue(registered.size() == handlerCount);
		
		for(ExceptionCallbackHandler handler : handlers.values()) {
			Assert.assertTrue(registered.contains(handler));
		}
	}
	
	@Test
	public void testRESTQueryServicesLoad() throws Exception {
		Map<String, RESTQueryService> services = webApplicationContext.getBeansOfType(RESTQueryService.class);
		Assert.assertNotNull(services);
		Assert.assertTrue(services.values().size()>0);
		
		ApiController ctrl = webApplicationContext.getBean(ApiController.class);
		ResourceFactory factory = ctrl.getResourceFactory();
		Assert.assertNotNull(factory);
		
		RESTQueryService service = factory.getQueryService(MockModelA.RESOURCE_PATH);
		Assert.assertNotNull(service);
	}

	@Test
	public void testRESTActionServicesLoad() throws Exception {
		Map<String, RESTActionService> services = webApplicationContext.getBeansOfType(RESTActionService.class);
		Assert.assertNotNull(services);
		Assert.assertTrue(services.values().size()>0);
		
		ApiController ctrl = webApplicationContext.getBean(ApiController.class);
		ResourceFactory factory = ctrl.getResourceFactory();
		Assert.assertNotNull(factory);
		
		RESTActionService service = factory.getActionService(MockModelA.RESOURCE_PATH);
		Assert.assertNotNull(service);
	}

	@Test
	public void testRESTPersistentServicesLoad() throws Exception {
		Map<String, RESTPersistentService> services = webApplicationContext.getBeansOfType(RESTPersistentService.class);
		Assert.assertNotNull(services);
		Assert.assertTrue(services.values().size()>0);
		
		ApiController ctrl = webApplicationContext.getBean(ApiController.class);
		ResourceFactory factory = ctrl.getResourceFactory();
		Assert.assertNotNull(factory);
		
		RESTPersistentService service = factory.getPersistentService(MockModelB.RESOURCE_PATH);
		Assert.assertNotNull(service);
	}

}
