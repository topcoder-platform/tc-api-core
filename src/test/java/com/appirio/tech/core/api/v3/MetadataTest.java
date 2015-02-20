/**
 * 
 */
package com.appirio.tech.core.api.v3;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.springframework.http.MediaType;

import com.appirio.tech.core.api.mock.a.MockModelA;
import com.appirio.tech.core.api.mock.a.MockQueryService;
import com.appirio.tech.core.api.mock.b.MockModelB;
import com.appirio.tech.core.api.mock.b.MockPersistentService;
import com.appirio.tech.core.api.v3.TCID;

/**
 * Tests metadata options.
 * 
 * @author sudo
 *
 */
public class MetadataTest extends ControllerTestBase {
	
	@Test
	public void testMetadataDoesNotCotain() throws Exception {
		setupData();
		
		mockMvc.perform(get("/api/v3/" + MockModelA.RESOURCE_PATH)
				.contentType(MediaType.APPLICATION_JSON)
				.param("metadata", "false"))
					.andExpect(status().isOk())
					.andExpect(jsonPath("result.metadata").doesNotExist())
					.andDo(print());
	}

	@Test
	public void testMetadataDoCotain() throws Exception {
		setupData();
		
		mockMvc.perform(get("/api/v3/" + MockModelA.RESOURCE_PATH)
				.contentType(MediaType.APPLICATION_JSON)
				.param("include", "metadata"))
					.andExpect(status().isOk())
					.andExpect(jsonPath("result.metadata").value("metadata not supported"));
					//.andDo(print());

		MockPersistentService mockSevice = webApplicationContext.getBean(MockPersistentService.class);
		mockMvc.perform(get("/api/v3/" + MockModelB.RESOURCE_PATH)
				.contentType(MediaType.APPLICATION_JSON)
				.param("include", "metadata"))
					.andExpect(status().isOk())
					.andExpect(jsonPath("result.metadata.fields").exists())
					.andExpect(jsonPath("result.metadata.totalCount").value(mockSevice.getStorage().size()))
					.andDo(print());
	}

	private void setupData() {
		//Setup model into MockStorage
		TCID id = new TCID("100");
		String strTest = "String Test Value";
		Integer intTest = 9999;
		MockModelA modelA = new MockModelA(id, strTest, intTest);
		webApplicationContext.getBean(MockQueryService.class).insertModel(modelA);
		
		MockModelB modelB = new MockModelB(id, strTest, intTest);
		webApplicationContext.getBean(MockPersistentService.class).insertModel(modelB);

	}
}
