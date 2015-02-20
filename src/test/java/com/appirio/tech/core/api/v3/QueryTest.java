/**
 * 
 */
package com.appirio.tech.core.api.v3;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.springframework.http.MediaType;

import com.appirio.tech.core.api.mock.a.MockModelA;
import com.appirio.tech.core.api.mock.a.MockQueryService;
import com.appirio.tech.core.api.v3.TCID;

/**
 * Tests basic queries
 * 
 * @author sudo
 *
 */
public class QueryTest extends ControllerTestBase {

	@Test
	public void testQueryNotFound() throws Exception {
		mockMvc.perform(get("/api/v3"))
			.andExpect(status().isNotFound());

		mockMvc.perform(get("/api/v3/dummy_resource"))
			.andExpect(status().isNotFound());
	}
	
	@Test
	public void testException() throws Exception {
		mockMvc.perform(get("/api/v3/exception"))
			.andDo(print())
			.andExpect(status().isInternalServerError());
	}
	
	@Test
	public void testQueryFound() throws Exception {
		mockMvc.perform(get("/api/v3/" + MockModelA.RESOURCE_PATH))
			.andDo(print())
			.andExpect(status().isOk());
	}

	@Test
	public void testQueryModel() throws Exception {
		
		//Setup model into MockStorage
		TCID id = new TCID("100");
		String strTest = "String Test Value";
		Integer intTest = 9999;
		MockModelA model = new MockModelA(id, strTest, intTest);
		webApplicationContext.getBean(MockQueryService.class).insertModel(model);
		
		//Now do test
		mockMvc.perform(get("/api/v3/" + MockModelA.RESOURCE_PATH).contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("id").exists())
			.andExpect(jsonPath("result").exists())
			.andExpect(jsonPath("version").value("v3"))
			.andExpect(jsonPath("result.success").value(true))
			.andExpect(jsonPath("result.status").value(HttpStatus.SC_OK))
			.andExpect(jsonPath("result.content").isArray())
			.andExpect(jsonPath("result.content", Matchers.hasSize(1)))
			.andExpect(jsonPath("result.content[0].id").value(id.toString()))
			.andExpect(jsonPath("result.content[0].strTest").value(strTest))
			.andExpect(jsonPath("result.content[0].intTest").value(intTest))
			.andExpect(jsonPath("result.content[0].dummyField").doesNotExist())
			.andDo(print());
	}

	@Test
	public void testQueryFieldParameter() throws Exception {
		
		//Setup model into MockStorage
		TCID id = new TCID("100");
		String strTest = "String Test Value";
		Integer intTest = 9999;
		MockModelA model = new MockModelA(id, strTest, intTest);
		webApplicationContext.getBean(MockQueryService.class).insertModel(model);
		
		//Now do test
		String fieldParam = "id,strTest";
		mockMvc.perform(get("/api/v3/" + MockModelA.RESOURCE_PATH)
				.contentType(MediaType.APPLICATION_JSON)
				.param("fields", fieldParam))
					.andExpect(status().isOk())
					.andExpect(jsonPath("result.content", Matchers.hasSize(1)))
					.andExpect(jsonPath("result.content[0].id").value(id.toString()))
					.andExpect(jsonPath("result.content[0].strTest").value(strTest))
					.andExpect(jsonPath("result.content[0].intTest").doesNotExist())
					.andDo(print());
	}
}
