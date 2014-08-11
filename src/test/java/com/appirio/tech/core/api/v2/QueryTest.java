/**
 * 
 */
package com.appirio.tech.core.api.v2;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.Test;

/**
 * Tests basic queries
 * 
 * @author sudo
 *
 */
public class QueryTest extends ControllerTest {

	@Test
	public void testQueryNotFound() throws Exception {
		mockMvc.perform(get("/api/v2"))
			.andExpect(status().isNotFound());

		mockMvc.perform(get("/api/v2/dummy_resource"))
			.andExpect(status().isNotFound());
	}
}
