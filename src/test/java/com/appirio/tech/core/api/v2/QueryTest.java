/**
 * 
 */
package com.appirio.tech.core.api.v2;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * Tests basic queries
 * 
 * @author sudo
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = { "file:src/test/webapp/WEB-INF/dispatcher-servlet.xml" })
public class QueryTest {

	@Autowired
	private WebApplicationContext webApplicationContext;
	protected MockMvc mock;

	@Before
	public void setUp() throws Exception {
		mock = MockMvcBuilders.webAppContextSetup(webApplicationContext)
				.build();
	}

	@Test
	public void testQueryNotFound() throws Exception {
		mock.perform(get("/api/v2"))
			.andExpect(status().isNotFound());

		mock.perform(get("/api/v2/dummy_resource"))
			.andExpect(status().isNotFound());
	}
}
