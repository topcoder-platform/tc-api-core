package com.appirio.tech.core.auth;

import io.dropwizard.auth.Authorizer;

public class AuthUserBasedAuthorizer<P extends AuthUser> implements Authorizer<P> {

	@Override
	public boolean authorize(AuthUser principal, String role) {
		if(principal==null)
			return false;
		return principal.hasRole(role);
	}

}
