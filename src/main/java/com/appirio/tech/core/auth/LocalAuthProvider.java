package com.appirio.tech.core.auth;

import io.dropwizard.auth.AuthFilter;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;

import java.io.IOException;
import java.security.Principal;
import java.util.Optional;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.SecurityContext;

import com.appirio.tech.core.auth.AuthUser;

@Priority(Priorities.AUTHORIZATION)
public class LocalAuthProvider <P extends AuthUser> extends AuthFilter<String, P> {

	private AuthUser authUser;
	
	public LocalAuthProvider(AuthUser authUser) {
		this.authUser = authUser;
	}
	
	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		requestContext.setSecurityContext(new SecurityContext() {
            @Override public Principal getUserPrincipal() {
                return authUser;
            }
            @SuppressWarnings("unchecked")
			@Override public boolean isUserInRole(String role) {
                return authorizer.authorize((P)authUser, role);
            }
            @Override public boolean isSecure() {
                return requestContext.getSecurityContext().isSecure();
            }
            @Override public String getAuthenticationScheme() {
                return "Bearer";
            }
        });		
	}
	
	public static class Builder<P extends AuthUser> extends AuthFilterBuilder<String, P, LocalAuthProvider<P>> {

		private AuthUser authUser;
		
		public Builder(final P authUser) {
			this.authUser = authUser;
			setAuthenticator(new Authenticator<String, P>() { // dummy
				@Override public Optional<P> authenticate(String credentials) throws AuthenticationException {
					return Optional.of(authUser);
				}
			});
		}
		
		@Override
		protected LocalAuthProvider<P> newInstance() {
			return new LocalAuthProvider<>(authUser);
		}
	}
}

