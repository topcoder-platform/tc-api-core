/**
 * 
 */
package com.appirio.tech.core.auth;

import io.dropwizard.auth.Auth;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;

import javax.ws.rs.core.HttpHeaders;

import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.appirio.tech.core.api.v3.exception.APIRuntimeException;
import com.google.common.base.Optional;
import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.api.model.Parameter;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.server.impl.inject.AbstractHttpContextInjectable;
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.spi.inject.InjectableProvider;

/**
 * @author sudo
 * 
 */
public class JWTAuthProvider implements InjectableProvider<Auth, Parameter> {

	private static class JWTAuthInjectable extends AbstractHttpContextInjectable<AuthUser> {
		private static final Logger LOGGER = LoggerFactory.getLogger(JWTAuthInjectable.class);
		private static final String PREFIX = "bearer";

		private final Authenticator<String, AuthUser> authenticator;
		private final boolean required;

		private JWTAuthInjectable(Authenticator<String, AuthUser> authenticator, boolean required) {
			this.authenticator = authenticator;
			this.required = required;
		}

		@Override
		public AuthUser getValue(HttpContext c) {
			try {
				final String header = c.getRequest().getHeaderValue(HttpHeaders.AUTHORIZATION);
				if (header != null) {
					final int space = header.indexOf(' ');
					if (space > 0) {
						final String method = header.substring(0, space);
						if (PREFIX.equalsIgnoreCase(method)) {
							final String credentials = header.substring(space + 1);
							final Optional<AuthUser> result = authenticator.authenticate(credentials);
							if (result.isPresent()) {
								return result.get();
							}
						}
					}
				}
			} catch (AuthenticationException e) {
				LOGGER.warn("Error authenticating credentials", e);
				throw new APIRuntimeException(e.getMessage());
			}

			if (required) {
				throw new APIRuntimeException(HttpStatus.UNAUTHORIZED_401, "Credentials are required to access this resource.");
			}
			return null;
		}
	}

	@Override
	public ComponentScope getScope() {
		return ComponentScope.PerRequest;
	}

	@Override
	public Injectable<?> getInjectable(ComponentContext ic, Auth a, Parameter c) {
		Authenticator<String, AuthUser> authenticator = new JWTAuthenticator();
		return new JWTAuthInjectable(authenticator, a.required());
	}
}
