/**
 * 
 */
package com.appirio.tech.core.auth;

import io.dropwizard.auth.Auth;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;

import java.util.ResourceBundle;

import javax.ws.rs.core.HttpHeaders;

import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.appirio.tech.core.api.v3.exception.APIRuntimeException;
import com.appirio.tech.core.api.v3.util.jwt.TokenExpiredException;
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

	private static final Logger logger = LoggerFactory.getLogger(JWTAuthProvider.class);
	
	public static final String DEFAULT_AUTH_DOMAIN = "topcoder-dev.com";
	
	private String secret;
	
	private String authDomain;

	/**
	 * constructs with this("topcoder-dev.com")
	 */
	public JWTAuthProvider() {
		this(DEFAULT_AUTH_DOMAIN);
	}

	/**
	 * 
	 * @param authDomain topcoder-dev.com|topcoder-qa.com|topcoder.com
	 */
	public JWTAuthProvider(String authDomain) {
		this.authDomain = authDomain;
		loadSecret();
	}
	
	protected static class JWTAuthInjectable extends AbstractHttpContextInjectable<AuthUser> {
		
		private static final String PREFIX = "bearer";

		private final Authenticator<String, AuthUser> authenticator;
		private final boolean required;

		protected JWTAuthInjectable(Authenticator<String, AuthUser> authenticator, boolean required) {
			this.authenticator = authenticator;
			this.required = required;
		}

		@Override
		public AuthUser getValue(HttpContext c) {
			//If @Auth is not required, don't do any validation and just return null
			if(!required) return null;
			
			String credentials = null;
			try {
				final String header = c.getRequest().getHeaderValue(HttpHeaders.AUTHORIZATION);
				if (header != null) {
					final int space = header.indexOf(' ');
					if (space > 0) {
						final String method = header.substring(0, space);
						if (PREFIX.equalsIgnoreCase(method)) {
							credentials = header.substring(space + 1);
							final Optional<AuthUser> result = authenticator.authenticate(credentials);
							if (result.isPresent()) {
								return result.get();
							}
						}
					}
				}
			} catch (TokenExpiredException e) {
				logger.debug("Credential expired: " + credentials);
				throw new APIRuntimeException(HttpStatus.UNAUTHORIZED_401, "jwt expired");
			} catch (AuthenticationException e) {
				logger.warn("Error authenticating credentials. " + e.getMessage());
				throw new APIRuntimeException(HttpStatus.INTERNAL_SERVER_ERROR_500, "Error authenticating credentials", e);
			}
			if (required) {
				logger.debug("Invalid credential: " + credentials);
				throw new APIRuntimeException(HttpStatus.UNAUTHORIZED_401, "Valid credentials are required to access this resource.");
			}
			return null;
		}
	}
	
	protected String getSecret() {
		return secret;
	}

	protected void setSecret(String secret) {
		this.secret = secret;
	}

	protected String getAuthDomain() {
		return authDomain;
	}

	protected void setAuthDomain(String authDomain) {
		this.authDomain = authDomain;
	}

	@Override
	public ComponentScope getScope() {
		return ComponentScope.PerRequest;
	}

	@Override
	public Injectable<?> getInjectable(ComponentContext ic, Auth a, Parameter c) {
		Authenticator<String, AuthUser> authenticator = new JWTAuthenticator(getAuthDomain(), getSecret());
		return new JWTAuthInjectable(authenticator, a.required());
	}
	
	/**
	 * load secret 
	 */
	protected void loadSecret() {
		try {
			logger.info("Loading the secret data..");
			ResourceBundle res = ResourceBundle.getBundle("auth");
			String key = res.getString("secret");
			if(key==null || key.length()==0)
				throw new Exception("'secret' has not been set in auth.properties.");
			setSecret(key);
		} catch (Exception e) {
			logger.error("Failed to load the secret data.", e);
		}
	}
}
