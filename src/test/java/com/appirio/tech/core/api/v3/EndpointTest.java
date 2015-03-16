/**
 * 
 */
package com.appirio.tech.core.api.v3;

import io.dropwizard.testing.junit.DropwizardAppRule;

import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.eclipse.jetty.http.HttpStatus;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;

import com.appirio.tech.core.api.v3.mock.a.MockModelA;
import com.appirio.tech.core.api.v3.mock.b.MockModelB;
import com.appirio.tech.core.api.v3.mock.b.MockPersistentResource;
import com.appirio.tech.core.api.v3.request.PostPutRequest;
import com.appirio.tech.core.api.v3.response.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;

/**
 * V3 API test point.
 * 
 * @author sudo
 * 
 */
public class EndpointTest {
	
	/**
	 * The jackson object mapper.
	 */
	private static final ObjectMapper JACKSON_OBJECT_MAPPER = new ObjectMapper();

	@ClassRule
	public static final DropwizardAppRule<TestConfiguration> RULE = new DropwizardAppRule<TestConfiguration>(
			TestApplication.class, "src/test/resources/initializer_test.yml");
	/*
	@SuppressWarnings("unchecked")
	@ClassRule
	public static final DropwizardAppRule<APIBaseConfiguration> RULE = new DropwizardAppRule<APIBaseConfiguration>(
				(Class<? extends Application<APIBaseConfiguration>>)APIApplication.class,
				"src/test/resources/initializer_test.yml");
	*/

	@Test
	public void testRoot() throws Exception {
		
		Client client = new Client();
		ClientResponse response = client.resource(String.format("http://localhost:%d/v3/mock_a_models", RULE.getLocalPort()))
									.get(ClientResponse.class);
		
		Assert.assertEquals(HttpStatus.OK_200, response.getStatus());
		Assert.assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getType());
	}

	@Test
	public void testV3Protocol() throws Exception {
		Client client = new Client();
		ClientResponse response = client.resource(String.format("http://localhost:%d/v3/mock_a_models", RULE.getLocalPort()))
									.get(ClientResponse.class);
		
		ApiResponse apiResponse = response.getEntity(ApiResponse.class);
		Assert.assertEquals(ApiVersion.v3, apiResponse.getVersion());
		Assert.assertNotNull(apiResponse.getId());
		Assert.assertEquals(HttpStatus.OK_200, (int)apiResponse.getResult().getStatus());
		@SuppressWarnings("unchecked")
		List<MockModelA> content = (List<MockModelA>)apiResponse.getResult().getContent();
		Assert.assertTrue(content.size()==0);
	}

	@Test
	public void testPost() throws Exception {
		MockPersistentResource.clearData();
		
		//Prepare 2 objects
		MockModelB modelA = new MockModelB();
		modelA.setIntTest(100);
		modelA.setStrTest("Test String A");
		
		MockModelB modelB = new MockModelB();
		modelB.setIntTest(200);
		modelB.setStrTest("Test String B");
		
		//Insert first object
		PostPutRequest requestA = new PostPutRequest();
		requestA.setParam(JACKSON_OBJECT_MAPPER.valueToTree(modelA));
		
		Client client = new Client();
		ClientResponse response = client.resource(String.format("http://localhost:%d/v3/mock_b_models", RULE.getLocalPort()))
				.accept("application/json").type("application/json").post(ClientResponse.class, requestA);
		
		Assert.assertEquals(HttpStatus.OK_200, response.getStatus());
		Assert.assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getType());
		
		//Insert second object
		PostPutRequest requestB = new PostPutRequest();
		requestB.setParam(JACKSON_OBJECT_MAPPER.valueToTree(modelB));
		
		response = client.resource(String.format("http://localhost:%d/v3/mock_b_models", RULE.getLocalPort()))
				.accept("application/json").type("application/json").post(ClientResponse.class, requestB);

		ApiResponse apiResponse = response.getEntity(ApiResponse.class);
		Assert.assertEquals(ApiVersion.v3, apiResponse.getVersion());
		Assert.assertNotNull(apiResponse.getId());
		Assert.assertEquals(HttpStatus.OK_200, (int)apiResponse.getResult().getStatus());
		
		//Assert by getting everything
		response = client.resource(
				String.format("http://localhost:%d/v3/mock_b_models", RULE.getLocalPort())).get(ClientResponse.class);

		ApiResponse getResponse = response.getEntity(ApiResponse.class);
		System.out.println("Result is" + getResponse.getResult());
		MockModelA[] content = getResponse.getContentResource(MockModelA[].class);
		System.out.println(getResponse.getResult());
		Assert.assertEquals(2, content.length);
	}

	@Test
	public void testPut() throws Exception {
		MockPersistentResource.clearData();

		//Insert new object
		MockModelB modelB = new MockModelB();
		modelB.setIntTest(200);
		modelB.setStrTest("Test String B");
		
		PostPutRequest request = new PostPutRequest();
		request.setParam(JACKSON_OBJECT_MAPPER.valueToTree(modelB));
		Client client = new Client();
		ClientResponse response = client.resource(String.format("http://localhost:%d/v3/mock_b_models", RULE.getLocalPort()))
				.accept("application/json").type("application/json").post(ClientResponse.class, request);
		
		//Do update to the resource
		ApiResponse apiResponse = response.getEntity(ApiResponse.class);
		TCID id = apiResponse.getContentResource(MockModelB.class).getId();
		modelB.setId(id);
		modelB.setIntTest(500); //New value that should get updated
		modelB.setStrTest("Test String Updated"); //New value that should get updated
		request.setParam(JACKSON_OBJECT_MAPPER.valueToTree(modelB));
		client.resource(String.format("http://localhost:%d/v3/mock_b_models/%s", RULE.getLocalPort(), id))
				.accept("application/json").type("application/json").put(ClientResponse.class, request);
		
		//Get the resource again, and see that it has been updated correctly
		response = client.resource(
				String.format("http://localhost:%d/v3/mock_b_models/%s", RULE.getLocalPort(), id)).get(ClientResponse.class);
		
		ApiResponse getResponse = response.getEntity(ApiResponse.class);
		MockModelB content = getResponse.getContentResource(MockModelB.class);
		Assert.assertEquals(500, (int)content.getIntTest());
		Assert.assertEquals("Test String Updated", content.getStrTest());
	}

	@Test
	public void testDelete() throws Exception {
		MockPersistentResource.clearData();
		Map<TCID, MockModelB> storageMap = MockPersistentResource.getStorage();
		
		//Insert 1 object
		MockModelB modelA = new MockModelB();
		modelA.setIntTest(100);
		modelA.setStrTest("Test String A");
		
		PostPutRequest requestA = new PostPutRequest();
		requestA.setParam(JACKSON_OBJECT_MAPPER.valueToTree(modelA));
		Client client = new Client();
		ClientResponse response = client.resource(String.format("http://localhost:%d/v3/mock_b_models", RULE.getLocalPort()))
				.accept("application/json").type("application/json").post(ClientResponse.class, requestA);
		Assert.assertEquals(1, storageMap.size());

		response = client.resource(
				String.format("http://localhost:%d/v3/mock_b_models/%s", RULE.getLocalPort(), 
						response.getEntity(ApiResponse.class).getContentResource(MockModelB.class).getId())).delete(ClientResponse.class);
		Assert.assertEquals(0, storageMap.size());
		
		//Assert by getting everything
		response = client.resource(
				String.format("http://localhost:%d/v3/mock_b_models", RULE.getLocalPort())).get(ClientResponse.class);

		ApiResponse getResponse = response.getEntity(ApiResponse.class);
		MockModelA[] content = getResponse.getContentResource(MockModelA[].class);
		Assert.assertEquals(0, content.length);
	}
}
