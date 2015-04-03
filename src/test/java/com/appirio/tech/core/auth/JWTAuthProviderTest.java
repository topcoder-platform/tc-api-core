package com.appirio.tech.core.auth;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;

import org.junit.Test;

import com.appirio.tech.core.api.v3.TCID;
import com.appirio.tech.core.api.v3.exception.APIRuntimeException;
import com.google.common.base.Optional;
import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.api.core.HttpRequestContext;


public class JWTAuthProviderTest {

	@Test
	public void testConstructor() throws Exception {
		
		String authDomain = "AUTH-DOMAIN-DUMMY";
		
		JWTAuthProvider provider = new JWTAuthProvider(authDomain);
		
		assertEquals("AUTH-DOMAIN-DUMMY", provider.getAuthDomain());
		assertEquals("SECRET-DUMMY", provider.getSecret());
	}
	
	@Test
	public void testGetValue() throws Exception {

		String token = "TOKEN-DUMMY";
		String uid = "USER-ID-DUMMY";
		AuthUser user = new AuthUser();
		user.setUserId(new TCID(uid));
		
		// mock: context
		HttpContext ctx = mockContext(token);
		// mock: authenticator
		@SuppressWarnings("unchecked")
		Authenticator<String, AuthUser> authenticator =
				(Authenticator<String, AuthUser>) mock(Authenticator.class);
		when(authenticator.authenticate(token)).thenReturn(Optional.of(user));
		
		// test
		JWTAuthProvider.JWTAuthInjectable jwtAuth = 
				new JWTAuthProvider.JWTAuthInjectable(authenticator, true);
		AuthUser result = jwtAuth.getValue(ctx);
		
		// verify result
		assertNotNull(result);
		assertEquals(result.getUserId().toString(), user.getUserId().toString());
		
		// verify mocks
		verify(ctx).getRequest();
		verify(authenticator).authenticate(token);
	}

	@Test
	public void testGetValue_401ErrorWhenNoAuthHeader() throws Exception {
		
		// mock: context
		HttpContext ctx = mockContext(null);
		// mock: authenticator
		@SuppressWarnings("unchecked")
		Authenticator<String, AuthUser> authenticator =
				(Authenticator<String, AuthUser>) mock(Authenticator.class);

		// test
		boolean required = true;
		JWTAuthProvider.JWTAuthInjectable jwtAuth = 
				new JWTAuthProvider.JWTAuthInjectable(authenticator, required);
		try {
			jwtAuth.getValue(ctx);
			fail("APIRuntimeException should be thrown in the previous step.");
		} catch (APIRuntimeException e) {
			// 401 unauthorized
			assertEquals(HttpServletResponse.SC_UNAUTHORIZED, e.getHttpStatus());
		}
		
		// verify mock
		verify(authenticator, never()).authenticate(anyString());
	}

	@Test
	public void testGetValue_401ErrorWhenUnauthorized() throws Exception {

		// mock: context
		HttpContext ctx = mockContext("TOKEN-DUMMY");
		// mock: authenticator
		@SuppressWarnings("unchecked")
		Authenticator<String, AuthUser> authenticator =
				(Authenticator<String, AuthUser>) mock(Authenticator.class);
		// returns Option.absent()
		when(authenticator.authenticate(anyString())).thenReturn(Optional.<AuthUser>absent());

		// test
		boolean required = true;
		JWTAuthProvider.JWTAuthInjectable jwtAuth = 
				new JWTAuthProvider.JWTAuthInjectable(authenticator, required);
		try {
			jwtAuth.getValue(ctx);
			fail("APIRuntimeException should be thrown in the previous step.");
		} catch (APIRuntimeException e) {
			// 401 unauthorized
			assertEquals(HttpServletResponse.SC_UNAUTHORIZED, e.getHttpStatus());
		}
		
		// verify mock
		verify(authenticator).authenticate(anyString());
	}
	
	@Test
	public void testGetValue_NullWhenNotRequiredAndUnauthorized() throws Exception {

		// mock: context
		HttpContext ctx = mockContext("TOKEN-DUMMY");
		// mock: authenticator
		@SuppressWarnings("unchecked")
		Authenticator<String, AuthUser> authenticator =
				(Authenticator<String, AuthUser>) mock(Authenticator.class);
		// returns Option.absent()
		when(authenticator.authenticate(anyString())).thenReturn(Optional.<AuthUser>absent());

		// test
		boolean required = false;
		JWTAuthProvider.JWTAuthInjectable jwtAuth = 
				new JWTAuthProvider.JWTAuthInjectable(authenticator, required);
		AuthUser user = jwtAuth.getValue(ctx);
		
		assertNull(user);
		
		// verify mock
		verify(authenticator).authenticate(anyString());
	}
	
	@Test
	public void testGetValue_500ErrorWhenAuthenticationException() throws Exception {

		// mock: context
		HttpContext ctx = mockContext("TOKEN-DUMMY");
		// mock: authenticator
		@SuppressWarnings("unchecked")
		Authenticator<String, AuthUser> authenticator =
				(Authenticator<String, AuthUser>) mock(Authenticator.class);
		// throw AuthenticationException
		when(authenticator.authenticate(anyString())).thenThrow(new AuthenticationException("Authentication error"));

		// test
		boolean required = true;
		JWTAuthProvider.JWTAuthInjectable jwtAuth = 
				new JWTAuthProvider.JWTAuthInjectable(authenticator, required);
		try {
			jwtAuth.getValue(ctx);
			fail("APIRuntimeException should be thrown in the previous step.");
		} catch (APIRuntimeException e) {
			// 500 internal server error
			assertEquals(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getHttpStatus());
		}
		// verify mock
		verify(authenticator).authenticate(anyString());
	}
	
	@Test
	public void testGetValue_ThorowsExceptionWhenUnexpectedError() throws Exception {
		// mock: context
		HttpContext ctx = mockContext("TOKEN-DUMMY");
		// mock: authenticator
		@SuppressWarnings("unchecked")
		Authenticator<String, AuthUser> authenticator =
				(Authenticator<String, AuthUser>) mock(Authenticator.class);
		// throw AuthenticationException
		when(authenticator.authenticate(anyString())).thenThrow(new RuntimeException("Unexpected error"));

		// test
		boolean required = true;
		JWTAuthProvider.JWTAuthInjectable jwtAuth = 
				new JWTAuthProvider.JWTAuthInjectable(authenticator, required);
		try {
			jwtAuth.getValue(ctx);
			fail("APIRuntimeException should be thrown in the previous step.");
		} catch (RuntimeException e) {
		}
		// verify mock
		verify(authenticator).authenticate(anyString());
	}
	
	protected HttpContext mockContext(String token) {
		HttpContext ctx = mock(HttpContext.class);
		HttpRequestContext reqctx = mock(HttpRequestContext.class);
		String header = token==null ? null : "Bearer " + token;
		when(reqctx.getHeaderValue(HttpHeaders.AUTHORIZATION)).thenReturn(header);		
		when(ctx.getRequest()).thenReturn(reqctx);
		return ctx;
	}
}
