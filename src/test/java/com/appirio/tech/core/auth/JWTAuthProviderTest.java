package com.appirio.tech.core.auth;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.SecurityContext;

import org.glassfish.jersey.internal.util.collection.MultivaluedStringMap;
import org.junit.Test;

import com.appirio.tech.core.api.v3.exception.APIRuntimeException;
import com.appirio.tech.core.api.v3.util.jwt.TokenExpiredException;
import java.util.Optional;

public class JWTAuthProviderTest {
	
	@Test
	public void testFilter() throws Exception {
		// test data
		String token = "DUMMY-TOKEN";
		String authHeader = "Bearer " + token;
		// mock
		ContainerRequestContext requestContext = createMockRequestContext(authHeader);
		AuthUser principal = new AuthUser(); 
		@SuppressWarnings("unchecked")
		Authenticator<String, AuthUser> authenticator = mock(Authenticator.class);
		doReturn(Optional.of(principal)).when(authenticator).authenticate(token);

		// test
		JWTAuthProvider<AuthUser> testee = new JWTAuthProvider<>();
		testee.setAuthenticator(authenticator);
		testee.filter(requestContext);
		
		// verify
		verify(requestContext).getHeaders();
		verify(requestContext).setSecurityContext(any(SecurityContext.class));
		verify(authenticator).authenticate(token);
	}
	
	@Test
	public void testFilter_401_WhenNoCredentialInRequest() throws Exception {
		// mock
		ContainerRequestContext requestContext = createMockRequestContext(null);

		// test
		JWTAuthProvider<AuthUser> testee = new JWTAuthProvider<>();
		try {
			testee.filter(requestContext);
			fail("APIRuntimeException should be thrown in the previous step.");
		} catch(APIRuntimeException e) {
			assertEquals(401, e.getHttpStatus());
		}
		
		// verify
		verify(requestContext).getHeaders();
		verify(requestContext, never()).setSecurityContext(any(SecurityContext.class));
	}
	
	@Test
	public void testFilter_401_WhenCredentialIsNotAuthenticated() throws Exception {
		// test data
		String token = "DUMMY-TOKEN";
		String authHeader = "Bearer " + token;
		// mock
		ContainerRequestContext requestContext = createMockRequestContext(authHeader);
		@SuppressWarnings("unchecked")
		Authenticator<String, AuthUser> authenticator = mock(Authenticator.class);
		doReturn(Optional.empty()).when(authenticator).authenticate(token); // unauthenticated
		
		// test
		JWTAuthProvider<AuthUser> testee = new JWTAuthProvider<>();
		testee.setAuthenticator(authenticator);
		try {
			testee.filter(requestContext);
			fail("APIRuntimeException should be thrown in the previous step.");
		} catch(APIRuntimeException e) {
			assertEquals(401, e.getHttpStatus());
		}
		
		// verify
		verify(requestContext).getHeaders();
		verify(requestContext, never()).setSecurityContext(any(SecurityContext.class));
		verify(authenticator).authenticate(token);
	}
	
	@Test
	public void testFilter_401_WhenTokenIsExpired() throws Exception {
		// test data
		String token = "DUMMY-TOKEN";
		String authHeader = "Bearer " + token;
		// mock
		ContainerRequestContext requestContext = createMockRequestContext(authHeader);
		@SuppressWarnings("unchecked")
		Authenticator<String, AuthUser> authenticator = mock(Authenticator.class);
		doThrow(new TokenExpiredException(token)).when(authenticator).authenticate(token); // token is expired
		
		// test
		JWTAuthProvider<AuthUser> testee = new JWTAuthProvider<>();
		testee.setAuthenticator(authenticator);
		try {
			testee.filter(requestContext);
			fail("APIRuntimeException should be thrown in the previous step.");
		} catch(APIRuntimeException e) {
			assertEquals(401, e.getHttpStatus());
		}
		
		// verify
		verify(requestContext).getHeaders();
		verify(requestContext, never()).setSecurityContext(any(SecurityContext.class));
		verify(authenticator).authenticate(token);
	}
	
	@Test
	public void testFilter_500_WhenAuthenticationFailed() throws Exception {
		// test data
		String token = "DUMMY-TOKEN";
		String authHeader = "Bearer " + token;
		// mock
		ContainerRequestContext requestContext = createMockRequestContext(authHeader);
		@SuppressWarnings("unchecked")
		Authenticator<String, AuthUser> authenticator = mock(Authenticator.class);
		doThrow(new AuthenticationException("Authentication failed")).when(authenticator).authenticate(token); // authentication failed
		
		// test
		JWTAuthProvider<AuthUser> testee = new JWTAuthProvider<>();
		testee.setAuthenticator(authenticator);
		try {
			testee.filter(requestContext);
			fail("APIRuntimeException should be thrown in the previous step.");
		} catch(APIRuntimeException e) {
			assertEquals(500, e.getHttpStatus());
		}
		
		// verify
		verify(requestContext).getHeaders();
		verify(requestContext, never()).setSecurityContext(any(SecurityContext.class));
		verify(authenticator).authenticate(token);
	}
	
	protected ContainerRequestContext createMockRequestContext(String authHeader) {
		MultivaluedMap<String, String> headers = new MultivaluedStringMap();
		if(authHeader!=null)
			headers.add(HttpHeaders.AUTHORIZATION, authHeader);
		ContainerRequestContext requestContext = mock(ContainerRequestContext.class);
		doReturn(headers).when(requestContext).getHeaders();
		
		return requestContext;
	}
	
	@Test
	public void testExtractCredentials() throws Exception {
		// test data
		String token = "DUMMY-TOKEN";
		String authHeader = "Bearer " + token;
		// mock
		ContainerRequestContext requestContext = createMockRequestContext(authHeader);
		
		// test
		JWTAuthProvider<AuthUser> testee = new JWTAuthProvider<>();
		String result = testee.extractCredentials(requestContext);

		// verify
		assertEquals(token, result);
		verify(requestContext).getHeaders();
	}
	
	@Test
	public void testExtractCredentials_NullWhenNoAuthorizationHeader() throws Exception {
		// mock
		ContainerRequestContext requestContext = createMockRequestContext(null);
		
		// test
		JWTAuthProvider<AuthUser> testee = new JWTAuthProvider<>();
		String result = testee.extractCredentials(requestContext);

		// verify
		assertNull(result);
		verify(requestContext).getHeaders();
	}
	
	@Test
	public void testExtractCredentials_NullWhenAuthorizationHeaderHasInvalidScheme() throws Exception {		
		// test data
		String token = "DUMMY-TOKEN";
		String authHeader = "Basic " + token; // Unsupported scheme
		
		// mock
		ContainerRequestContext requestContext = createMockRequestContext(authHeader);
		
		// test
		JWTAuthProvider<AuthUser> testee = new JWTAuthProvider<>();
		String result = testee.extractCredentials(requestContext);

		// verify
		assertNull(token, result);
		verify(requestContext).getHeaders();
	}
	
	@Test
	public void testExtractCredentials_NullWhenAuthorizationHeaderIsInvalidFormat() throws Exception {		
		// test data
		String token = "DUMMY-TOKEN";
		String authHeader = "Bearer" + token;
		
		// mock
		ContainerRequestContext requestContext = createMockRequestContext(authHeader);
		
		// test
		JWTAuthProvider<AuthUser> testee = new JWTAuthProvider<>();
		String result = testee.extractCredentials(requestContext);

		// verify
		assertNull(token, result);
		verify(requestContext).getHeaders();
	}
}
