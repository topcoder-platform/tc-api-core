/**
 * 
 */
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
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.SecurityContext;

import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.appirio.tech.core.api.v3.exception.APIRuntimeException;
import com.appirio.tech.core.api.v3.util.jwt.TokenExpiredException;


/**
 * @author sudo
 * 
 */
@Priority(Priorities.AUTHORIZATION)
public class JWTAuthProvider <P extends AuthUser> extends AuthFilter<String, P> {

	private static final Logger logger = LoggerFactory.getLogger(JWTAuthProvider.class);
	
	public static final String PREFIX = "bearer";
	
	private boolean required = true; 
	
    public JWTAuthProvider() {
		super();
		this.prefix = PREFIX;
	}

    public JWTAuthProvider(boolean required) {
		this();
		this.required = required;
	}

    public void setAuthenticator(Authenticator<String, P> authenticator) {
    	this.authenticator = authenticator;
    }

    public Authenticator<String, P> getAuthenticator() {
    	return this.authenticator;
    }
    
	@Override
    public void filter(final ContainerRequestContext requestContext) throws IOException {
		String credentials = extractCredentials(requestContext);
		if(credentials!=null) {
			try {
		    	final Optional<P> result = authenticator.authenticate(credentials);
	            if (result!=null && result.isPresent()) {
	                requestContext.setSecurityContext(new SecurityContext() {
	                    @Override public Principal getUserPrincipal() {
	                        return result.get();
	                    }
	                    @Override public boolean isUserInRole(String role) {
	                        return authorizer.authorize(result.get(), role);
	                    }
	                    @Override public boolean isSecure() {
	                        return requestContext.getSecurityContext().isSecure();
	                    }
	                    @Override public String getAuthenticationScheme() {
	                        return "Bearer";
	                    }
	                });
	                return;
	            }
			} catch (TokenExpiredException e) {
				if(required) {
					logger.debug("Credential expired: " + credentials);
					throw new APIRuntimeException(HttpStatus.UNAUTHORIZED_401, "jwt expired");
				}
			} catch (AuthenticationException e) {
				throw new APIRuntimeException(HttpStatus.INTERNAL_SERVER_ERROR_500, "Error in authenticating credentials", e);
			}
		}
		if(required) {
			logger.debug("Invalid credential: " + credentials);
			throw new APIRuntimeException(HttpStatus.UNAUTHORIZED_401, "Valid credentials are required to access this resource.");
		}
    }
    
	protected String extractCredentials(ContainerRequestContext requestContext) {
    	String header = requestContext.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
    	if(header==null || header.trim().length()==0)
    		return null;
		final int space = header.indexOf(' ');
		if(space <= 0)
			return null;
		
		String method = header.substring(0, space);
		if(!prefix.equalsIgnoreCase(method))
			return null;
		
		return header.substring(space + 1);
	}
	
    /**
     * Builder for {@link JWTAuthProvider}.
     * <p>An {@link Authenticator} must be provided during the building process.</p>
     *
     * @param <P> the type of the principal
     */
    public static class Builder<P extends AuthUser>
            extends AuthFilterBuilder<String, P, JWTAuthProvider<P>> {

    	private boolean required = true;
    	
    	public Builder() {
			setPrefix(PREFIX);
			setAuthorizer(new AuthUserBasedAuthorizer<P>());
		}
    	
    	public Builder<P> setRequired(boolean required) {
    		this.required = required;
    		return this;
    	}
    	
		@Override
        protected JWTAuthProvider<P> newInstance() {
			return new JWTAuthProvider<>(required);
        }
		
    }
}
