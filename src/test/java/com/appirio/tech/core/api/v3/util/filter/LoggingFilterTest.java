package com.appirio.tech.core.api.v3.util.filter;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.net.URI;

import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.MDC;
import org.junit.Test;

import com.appirio.tech.core.api.v3.util.jwt.JWTToken;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;

public class LoggingFilterTest {

	@Test
	public void testGetUserInfo() throws Exception {
		// data (authorization header)
		JWTToken token = new JWTToken();
		token.setUserId("12345678");
		token.setHandle("HANDLE-DUMMY");
		String secret = "SECRET-DUMMY";
		String tokentext = token.generateToken(secret);
		
		String authHeader = String.format("Bearer %s", tokentext);
		
		// mock
		ContainerRequestContext request = mock(ContainerRequestContext.class);
		
		// testee
		LoggingFilter filter = spy(new LoggingFilter());
		doReturn(authHeader).when(filter).getAuthorizationHeader(request);
		
		// test
		String result = filter.getUserInfo(request);
		
		// verify
		assertEquals(String.format("%s,%s", token.getHandle(), token.getUserId()), result);
		verify(filter).getAuthorizationHeader(request);
	}
	
	@Test
	public void testGetRequestBody() throws Exception {
		// data
		String body =
				"DUMMY-BODY-LINE1\n" +
				"DUMMY-BODY-LINE2\n" +
				"DUMMY-BODY-LINE3\n";
		ByteArrayInputStream in = new ByteArrayInputStream(body.getBytes("UTF-8"));
		
		// mock
		ContainerRequestContext request = mock(ContainerRequestContext.class);
		doReturn(in).when(request).getEntityStream();
		
		// testee
		LoggingFilter filter = new LoggingFilter();
		String result = filter.getRequestBody(request);
		
		// verify
		assertEquals(body.replaceAll("\\n", ""), result);
		verify(request).getEntityStream();
	}
	
	@Test
	public void testGetPath() throws Exception {
		// data
		String path = "/test?p1=v1&p2=v2";
		URI baseUri = new URI("https://api.topcoder.com/v3");
		URI requestUri = new URI(baseUri + path);
		
		// mock
		ContainerRequestContext request = mock(ContainerRequestContext.class);
		UriInfo uri = mock(UriInfo.class);
		doReturn(uri).when(request).getUriInfo();
		doReturn(requestUri).when(uri).getRequestUri();
		doReturn(baseUri).when(uri).getBaseUri();

		// testee
		LoggingFilter filter = new LoggingFilter();
		String result = filter.getPath(request);
		
		// verify
		assertEquals(path, result);
		verify(uri, atLeastOnce()).getRequestUri();
		verify(uri, atLeastOnce()).getBaseUri();
	}
	
	@Test
	public void testGetResponseBody() throws Exception {
		// data
		String jasonBody = "{\"DUMMY-FIELD1\":\"DUMMY-VALUE1\",\"DUMMY-FIELD2\":{\"DUMMY-FIELD3\":\"DUMMY-VALUE3\"}}";
		Object entity = new ObjectMapper().readTree(jasonBody);
		
		// mock
		ContainerResponseContext response = mock(ContainerResponseContext.class);
		doReturn(entity).when(response).getEntity();
		
		// testee
		LoggingFilter filter = new LoggingFilter();
		String result = filter.getResponseBody(response);
		
		// verify
		assertEquals(jasonBody, result);
		verify(response).getEntity();
	}
	
	@Test
	public void testGetResponseBody_NonJasonData() throws Exception {
		// data
		String textBody = "DUMMY-BODY";
		
		// mock
		ContainerResponseContext response = mock(ContainerResponseContext.class);
		doReturn(textBody).when(response).getEntity();
		
		// testee
		LoggingFilter filter = new LoggingFilter();
		String result = filter.getResponseBody(response);
		
		// verify
		assertEquals("\""+textBody+"\"", result);
		verify(response).getEntity();
	}
	
	@Test
	public void testMaskPassword() throws Exception {
		
		// data
		String pw = "PASSWORD-DUMMY";
		String mask = StringUtils.repeat("*", pw.length());
		String temp = "email=jdoe%%40topcoder.com&password=%s";
		String query = String.format(temp, pw);
		
		// testee
		LoggingFilter filter = new LoggingFilter();
		
		// test
		String result = filter.maskPassword(query);
		
		// verify
		assertEquals(String.format(temp, mask), result);
		
		// data2
		String temp2 = "{\"id\":\"-1ad55a10:152e519fb57:-7ffb\",\"result\":{\"success\":true,\"status\":200,\"metadata\":null,\"content\":{\"id\":\"40135645\",\"modifiedBy\":null,\"modifiedAt\":null,\"createdBy\":null,\"createdAt\":null,\"handle\":\"jdoe\",\"email\":\"jdoe@topcoder.com\",\"firstName\":\"John\",\"lastName\":\"Doe\",\"credential\":{,\"resetToken\":null,\"password\":\"%s\"},\"status\":\"A\",\"country\":null,\"regSource\":null,\"utmSource\":null,\"utmMedium\":null,\"utmCampaign\":null,\"active\":true,\"profile\":null,\"emailActive\":true},\"version\":\"v3\"}";
		String json = String.format(temp2, pw);

		// test2
		String result2 = filter.maskPassword(json);
		
		// verify
		assertEquals(String.format(temp2, mask), result2);
	}
	
	@Test
	public void testGenerateLog() throws Exception {
		// data
		String userInfo = "USER-INFO-DUMMY";
		String method = "METHOD-DUMMY";
		String path = "PATH-DUMMY";
		String requestBody = "REQUEST-BODY-DUMMY";
		int status = 200;
		String responseBody = "RESPONSE-BODY-DUMMY";
		
		MDC.put(LoggingFilter.MDC_KEY_USER, userInfo);
		MDC.put(LoggingFilter.MDC_KEY_REQUESTBODY, requestBody);
		
		// mock
		ContainerRequestContext request = mock(ContainerRequestContext.class);
		doReturn(method).when(request).getMethod();
		
		ContainerResponseContext response = mock(ContainerResponseContext.class);
		doReturn(status).when(response).getStatus();
		
		// testee
		LoggingFilter filter = spy(new LoggingFilter());
		// mock
		doReturn(path).when(filter).getPath(request);
		doReturn(path).when(filter).maskPassword(path);
		doReturn(responseBody).when(filter).getResponseBody(response);
		doReturn(responseBody).when(filter).maskPassword(responseBody);
		
		// test
		String result = filter.generateLog(request, response);
		
		// verify
		String expected = String.format(LoggingFilter.LOG_TEMPLATE, userInfo,method,path,requestBody,status,responseBody);
		assertEquals(expected, result);
		
		assertNull(MDC.get(LoggingFilter.MDC_KEY_USER));
		assertNull(MDC.get(LoggingFilter.MDC_KEY_REQUESTBODY));
		
		verify(request).getMethod();
		verify(response).getStatus();
		verify(filter).getPath(request);
		verify(filter).maskPassword(path);
		verify(filter).getResponseBody(response);
		verify(filter).maskPassword(responseBody);
	}

	@Test
	public void testFilterRequest() throws Exception {
		// data
		String userInfo = "USER-INFO-DUMMY";
		String requestBody = "REQUEST-BODY-DUMMY";

		// mock
		ContainerRequestContext request = mock(ContainerRequestContext.class);

		// testee
		LoggingFilter filter = spy(new LoggingFilter());
		// mock
		doReturn(userInfo).when(filter).getUserInfo(request);
		doReturn(requestBody).when(filter).getRequestBody(request);
		
		// test
		filter.filter(request);

		// verify
		assertEquals(userInfo, MDC.get(LoggingFilter.MDC_KEY_USER));
		assertEquals(requestBody, MDC.get(LoggingFilter.MDC_KEY_REQUESTBODY));
		
		verify(filter).getUserInfo(request);
		verify(filter).getRequestBody(request);
	}

}
