/**
 * 
 */
package com.appirio.tech.core.api.v3;

import io.dropwizard.testing.junit.DropwizardAppRule;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.jetty.http.HttpStatus;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;

import com.appirio.tech.core.api.v3.dropwizard.APIApplication;
import com.appirio.tech.core.api.v3.mock.a.MockModelA;
import com.appirio.tech.core.api.v3.mock.b.MockModelB;
import com.appirio.tech.core.api.v3.mock.b.MockPersistentResource;
import com.appirio.tech.core.api.v3.request.PostPutRequest;
import com.appirio.tech.core.api.v3.response.ApiResponse;
import com.appirio.tech.core.api.v3.util.jwt.JWTToken;
import com.appirio.tech.core.auth.JWTAuthenticator;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;

/**
 * V3 API test point.
 * 
 * @author sudo
 * 
 */
public class EndpointTest {

	// Setting "TC_JWT_KEY"
	static final String JWT_SECRET = "SECRET-DUMMY";
	static {
		System.setProperty(APIApplication.PROP_KEY_JWT_SECRET, JWT_SECRET);
	}

	@ClassRule
	public static final DropwizardAppRule<TestConfiguration> RULE = new DropwizardAppRule<TestConfiguration>(
			TestApplication.class, "src/test/resources/initializer_test.yml");
	
	@Test
	public void testRoot() throws Exception {
		
		Client client = ClientBuilder.newClient();
		Response response = client.target(String.format("http://localhost:%d/v3/mock_a_models", RULE.getLocalPort()))
				.request().header("Authorization", createAuthHeader()).get();
		
		Assert.assertEquals(HttpStatus.OK_200, response.getStatus());
		Assert.assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType());
	}
	
	@Test
	public void testV3Protocol() throws Exception {
		
		Client client = ClientBuilder.newClient();
		Response response = client.target(String.format("http://localhost:%d/v3/mock_a_models", RULE.getLocalPort()))
				.request().header("Authorization", createAuthHeader()).get(Response.class);

		
		ApiResponse apiResponse = response.readEntity(ApiResponse.class);
		Assert.assertEquals(ApiVersion.v3, apiResponse.getVersion());
		Assert.assertNotNull(apiResponse.getId());
		Assert.assertEquals(HttpStatus.OK_200, (int)apiResponse.getResult().getStatus());
		List<MockModelA> content = apiResponse.getContentResourceList(MockModelA.class);
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
		PostPutRequest<MockModelB> requestA = new PostPutRequest<MockModelB>();
		requestA.setParam(modelA);
		
		Client client = ClientBuilder.newClient();
		Response response = client.target(String.format("http://localhost:%d/v3/mock_b_models", RULE.getLocalPort()))
				.request().header("Authorization", createAuthHeader())
				.accept("application/json")
				.post(Entity.json(requestA));

		Assert.assertEquals(HttpStatus.OK_200, response.getStatus());
		Assert.assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType());
		

		//Insert second object
		PostPutRequest<MockModelB> requestB = new PostPutRequest<MockModelB>();
		requestB.setParam(modelB);
		
		response = client.target(String.format("http://localhost:%d/v3/mock_b_models", RULE.getLocalPort()))
				.request().header("Authorization", createAuthHeader())
				.accept("application/json")
				.post(Entity.json(requestB));
				
		ApiResponse apiResponse = response.readEntity(ApiResponse.class);
		Assert.assertEquals(ApiVersion.v3, apiResponse.getVersion());
		Assert.assertNotNull(apiResponse.getId());
		Assert.assertEquals(HttpStatus.OK_200, (int)apiResponse.getResult().getStatus());
		
		//Assert by getting everything
		response = client.target(String.format("http://localhost:%d/v3/mock_b_models", RULE.getLocalPort()))
				.request().header("Authorization", createAuthHeader()).get(Response.class);

		ApiResponse getResponse = response.readEntity(ApiResponse.class);
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
		
		PostPutRequest<MockModelB> request = new PostPutRequest<MockModelB>();
		request.setParam(modelB);
		
		Client client = ClientBuilder.newClient();
		Response response = client.target(String.format("http://localhost:%d/v3/mock_b_models", RULE.getLocalPort()))
				.request().header("Authorization", createAuthHeader())
				.accept("application/json")
				.post(Entity.json(request));
		
		//Do update to the resource
		ApiResponse apiResponse = response.readEntity(ApiResponse.class);
		TCID id = apiResponse.getContentResource(MockModelB.class).getId();
		
		modelB.setId(id);
		modelB.setIntTest(500); //New value that should get updated
		modelB.setStrTest("Test String Updated"); //New value that should get updated
		request.setParam(modelB);
		
		client.target(String.format("http://localhost:%d/v3/mock_b_models/%s", RULE.getLocalPort(), id))
				.request().header("Authorization", createAuthHeader())
				.accept("application/json")
				.put(Entity.json(request));
		
		//Get the resource again, and see that it has been updated correctly
		response = client.target(String.format("http://localhost:%d/v3/mock_b_models/%s", RULE.getLocalPort(), id))
				.request().header("Authorization", createAuthHeader()).get(Response.class);
		
		ApiResponse getResponse = response.readEntity(ApiResponse.class);
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
		
		PostPutRequest<MockModelB> requestA = new PostPutRequest<MockModelB>();
		requestA.setParam(modelA);
		
		Client client = ClientBuilder.newClient();
		Response response = client.target(String.format("http://localhost:%d/v3/mock_b_models", RULE.getLocalPort()))
				.request().header("Authorization", createAuthHeader())
				.accept("application/json")
				.post(Entity.json(requestA));

		Assert.assertEquals(1, storageMap.size());

		response = client.target(
				String.format("http://localhost:%d/v3/mock_b_models/%s", RULE.getLocalPort(), 
						response.readEntity(ApiResponse.class).getContentResource(MockModelB.class).getId()))
						.request().header("Authorization", createAuthHeader())
						.delete(Response.class);
		Assert.assertEquals(0, storageMap.size());

		//Assert by getting everything
		response = client.target(
				String.format("http://localhost:%d/v3/mock_b_models", RULE.getLocalPort()))
				.request().header("Authorization", createAuthHeader()).get(Response.class);

		ApiResponse getResponse = response.readEntity(ApiResponse.class);
		MockModelA[] content = getResponse.getContentResource(MockModelA[].class);
		Assert.assertEquals(0, content.length);
	}
	

	@Test
	public void testGetResposeWithNull() throws Exception {
		Client client = ClientBuilder.newClient();
		Response response = client.target(String.format("http://localhost:%d/v3/mock_b_models/responseNull", RULE.getLocalPort()))
									.request().header("Authorization", createAuthHeader()).get(Response.class);
		
		ApiResponse apiResponse = response.readEntity(ApiResponse.class);
		Assert.assertEquals(ApiVersion.v3, apiResponse.getVersion());
		Assert.assertNotNull(apiResponse.getId());
		Assert.assertEquals(HttpStatus.OK_200, (int)apiResponse.getResult().getStatus());
		MockModelB content = apiResponse.getContentResource(MockModelB.class);
		Assert.assertNull(content);
	}
	
	@Test
	public void testGetFieldSelectorResposeWithNull() throws Exception {
		Client client = ClientBuilder.newClient();
		Response response = client.target(String.format("http://localhost:%d/v3/mock_b_models/selectoreNull", RULE.getLocalPort()))
				.request().header("Authorization", createAuthHeader()).get(Response.class);
		
		ApiResponse apiResponse = response.readEntity(ApiResponse.class);
		Assert.assertEquals(ApiVersion.v3, apiResponse.getVersion());
		Assert.assertNotNull(apiResponse.getId());
		Assert.assertEquals(HttpStatus.OK_200, (int)apiResponse.getResult().getStatus());
		MockModelB content = apiResponse.getContentResource(MockModelB.class);
		Assert.assertNull(content);
	}

	@Test
	public void testGetWithFilter() throws Exception {
		// setup
		MockPersistentResource.clearData();
		
		// create 2 objects
		MockModelB modelA = new MockModelB();
		modelA.setIntTest(100);
		modelA.setStrTest("Test String A");
		
		MockModelB modelB = new MockModelB();
		modelB.setIntTest(200);
		modelB.setStrTest("Test+String+B");
		
		Client client = ClientBuilder.newClient();
		
		//Insert first object
		PostPutRequest<MockModelB> requestA = new PostPutRequest<MockModelB>();
		requestA.setParam(modelA);
		Response response = client.target(String.format("http://localhost:%d/v3/mock_b_models", RULE.getLocalPort()))
				.request().header("Authorization", createAuthHeader())
				.accept("application/json")
				.post(Entity.json(requestA));
		Assert.assertEquals(HttpStatus.OK_200, response.getStatus());
		
		//Insert second object
		PostPutRequest<MockModelB> requestB = new PostPutRequest<MockModelB>();
		requestB.setParam(modelB);
		response = client.target(String.format("http://localhost:%d/v3/mock_b_models", RULE.getLocalPort()))
				.request().header("Authorization", createAuthHeader())
				.accept("application/json")
				.post(Entity.json(requestB));
		Assert.assertEquals(HttpStatus.OK_200, response.getStatus());

		// Get all
		response = client.target(
				String.format("http://localhost:%d/v3/mock_b_models?filter=", RULE.getLocalPort()))
				.request().header("Authorization", createAuthHeader()).get(Response.class);

		ApiResponse getResponse = response.readEntity(ApiResponse.class);
		MockModelB[] content = getResponse.getContentResource(MockModelB[].class);
		Assert.assertEquals(2, content.length);

		// Get with filter
		String filterValue = "Test+String+B";
		String filter = "strTest="+filterValue;
		getResponse = getObjectsWithFilter(client, filter);
		content = getResponse.getContentResource(MockModelB[].class);
		Assert.assertEquals(1, content.length);
		Assert.assertEquals(filterValue, content[0].getStrTest());
		Assert.assertEquals(modelB.getIntTest(), content[0].getIntTest());
		
		filterValue = "Test String A";
		filter = "strTest="+filterValue;
		getResponse = getObjectsWithFilter(client, filter);
		content = getResponse.getContentResource(MockModelB[].class);
		Assert.assertEquals(1, content.length);
		Assert.assertEquals(filterValue, content[0].getStrTest());
		Assert.assertEquals(modelA.getIntTest(), content[0].getIntTest());
		
		// n/a
		getResponse = getObjectsWithFilter(client, null);
		content = getResponse.getContentResource(MockModelB[].class);
		Assert.assertEquals(2, content.length);
	}
	
	@Test
	public void testAnonymousAccess() throws Exception {
		
		Client client = ClientBuilder.newClient();
		Response response = client.target(String.format("http://localhost:%d/v3/mock_a_models/anonymous", RULE.getLocalPort()))
				.request().get();
		
		Assert.assertEquals(HttpStatus.OK_200, response.getStatus());
		Assert.assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType());
		
		client = ClientBuilder.newClient();
		response = client.target(String.format("http://localhost:%d/v3/mock_a_models/anonymous", RULE.getLocalPort()))
				.request().header("Authorization", createAuthHeader()).get();
		
		Assert.assertEquals(HttpStatus.OK_200, response.getStatus());
		Assert.assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType());
	}
	
	@Test
	public void testRoleBasedPermission() throws Exception {
		
		Client client = ClientBuilder.newClient();
		Response response = client.target(String.format("http://localhost:%d/v3/mock_a_models/protected", RULE.getLocalPort()))
				.request().header("Authorization", createAuthHeader()).get();
		
		Assert.assertEquals(HttpStatus.FORBIDDEN_403, response.getStatus());
		
		client = ClientBuilder.newClient();
		response = client.target(String.format("http://localhost:%d/v3/mock_a_models/protected", RULE.getLocalPort()))
				.request().header("Authorization", createAuthHeaderWith("administrator")).get();
		
		Assert.assertEquals(HttpStatus.OK_200, response.getStatus());

		client = ClientBuilder.newClient();
		response = client.target(String.format("http://localhost:%d/v3/mock_a_models/protected", RULE.getLocalPort()))
				.request().header("Authorization", createAuthHeaderWith("employee")).get();
		
		Assert.assertEquals(HttpStatus.FORBIDDEN_403, response.getStatus());
	}
	
	private ApiResponse getObjectsWithFilter(Client client, String filter)
			throws UnsupportedEncodingException {
		Response response;
		ApiResponse getResponse;
		filter = filter!=null ? URLEncoder.encode(filter, "UTF-8") : "";
		System.out.println("Filter(encoded) is " + filter);
		response = client.target(
				String.format("http://localhost:%d/v3/mock_b_models?filter=", RULE.getLocalPort())+filter)
				.request().header("Authorization", createAuthHeader()).get(Response.class);

		getResponse = response.readEntity(ApiResponse.class);
		return getResponse;
	}
	
	private String createAuthHeader() {
		return createAuthHeaderWith(null);
	}
	
	@SuppressWarnings("serial")
	private String createAuthHeaderWith(String role) {
		JWTToken token = new JWTToken();
		token.setEmail("jdoe@topcoder.com");
		token.setHandle("jdoe");
		token.setUserId("123456789");
		if(role!=null) {
			token.setRoles(new ArrayList<String>(){{ add(role); }});
		}
		token.setIssuer(token.createIssuerFor(JWTAuthenticator.DEFAULT_AUTH_DOMAIN));
		return "Bearer " + token.generateToken(JWT_SECRET);
	}
}
