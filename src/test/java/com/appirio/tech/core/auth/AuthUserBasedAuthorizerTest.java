package com.appirio.tech.core.auth;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.Matchers.*;

import org.junit.Test;


public class AuthUserBasedAuthorizerTest {

	@Test
	public void testAuthorize_AuthorizedWithRole() {
		testAuthorize_AuthorizeWithRole(true);
	}

	@Test
	public void testAuthorize_UnauthorizedWithRole() {
		testAuthorize_AuthorizeWithRole(false);
	}

	public void testAuthorize_AuthorizeWithRole(boolean authorized) {
		
		String role = "DUMMY-ROLE";
		AuthUser user = spy(new AuthUser());
		doReturn(authorized).when(user).hasRole(role);
		
		AuthUserBasedAuthorizer<AuthUser> testee = new AuthUserBasedAuthorizer<>();
		
		boolean result = testee.authorize(user, role);
		if(authorized) {
			assertTrue("The result should be true when AuthUser#hasRole(role) returns true.", result);
		} else {
			assertFalse("The result should be false when AuthUser#hasRole(role) returns false.", result);
		}
		
		verify(user).hasRole(role);
	}
	
	@Test
	public void testAuthorize_FalseWhenPrincipalIsNull() {
		
		String role = "DUMMY-ROLE";
		
		AuthUserBasedAuthorizer<AuthUser> testee = new AuthUserBasedAuthorizer<>();
		
		boolean result = testee.authorize(null, role);
		
		assertFalse("The result should be false when a principal(AuthUser) is null.", result);
	}

}
