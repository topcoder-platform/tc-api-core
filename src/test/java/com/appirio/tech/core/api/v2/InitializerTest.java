/**
 * 
 */
package com.appirio.tech.core.api.v2;

import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;

import com.appirio.tech.core.api.v2.controller.ApiController;
import com.appirio.tech.core.api.v2.exception.handler.ExceptionCallbackHandler;

/**
 * Tests initializing properties
 * 
 * @author sudo
 *
 */
public class InitializerTest extends ControllerTest {

	@Test
	public void testExceptionHandlers() {
		//first get all ExceptionHandlers
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
}
