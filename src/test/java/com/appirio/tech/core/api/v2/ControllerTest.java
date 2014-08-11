package com.appirio.tech.core.api.v2;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * Base class for testing Controller methods with Spring application context loaded.
 * 
 * @author sudo
 *
 */

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = { "file:src/test/webapp/WEB-INF/dispatcher-servlet.xml" })
public class ControllerTest {

	@Autowired
	protected WebApplicationContext webApplicationContext;
	protected MockMvc mockMvc;

	public ControllerTest() {
		super();
	}

	@Before
	public void setUp() throws Exception {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
				.build();
	}

}