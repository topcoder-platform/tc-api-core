package com.appirio.tech.core.auth;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.appirio.tech.core.api.v3.TCID;

public class AuthUserTest {

	@Test
	public void testHasRole_UsingLocalRoles() {
		
		// testee
		AuthUser authUser = spy(new AuthUser());
		List<String> roles = new ArrayList<String>();
		roles.add("ROLE");
		authUser.setRoles(roles);

		// test
		boolean result1 = authUser.hasRole("ROLE");
		boolean result2 = authUser.hasRole("ANOTHER-ROLE");
		
		// verify
		assertTrue(result1);
		assertFalse(result2);
		verify(authUser, never()).makeRequest(anyString(), anyString(), anyString(), anyString());
	}
	
	@Test
	public void testHasRole_GettingRolesFromService() {
		
		// testee
		AuthUser authUser = spy(new AuthUser());
		String userId = "USER-ID";
		authUser.setUserId(new TCID(userId));
		List<String> roles = new ArrayList<String>();
		authUser.setRoles(roles); // empty

		// mock
		doReturn(true).when(authUser).makeRequest("roles", "ROLE", "hasrole", userId);
		doReturn(false).when(authUser).makeRequest("roles", "ANOTHER-ROLE", "hasrole", userId);
		
		// test
		boolean result1 = authUser.hasRole("ROLE");
		boolean result2 = authUser.hasRole("ANOTHER-ROLE");
		
		// verify
		assertTrue(result1);
		assertFalse(result2);
		verify(authUser).makeRequest("roles", "ROLE", "hasrole", userId);
		verify(authUser).makeRequest("roles", "ANOTHER-ROLE", "hasrole", userId);
	}
}
