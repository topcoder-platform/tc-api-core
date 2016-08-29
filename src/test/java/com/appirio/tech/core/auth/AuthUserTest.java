package com.appirio.tech.core.auth;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;


public class AuthUserTest {

	@Test
	public void testHasRole() {
		
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
	}
	
	@Test
	public void testHasRole_EmptyRole() {
		
		// testee
		AuthUser authUser = spy(new AuthUser());
		authUser.setRoles(null);

		// test
		boolean result = authUser.hasRole("ROLE");
		
		// verify
		assertFalse(result);
	}
}
