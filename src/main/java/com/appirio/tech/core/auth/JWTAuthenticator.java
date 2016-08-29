/**
 * 
 */
package com.appirio.tech.core.auth;

import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.appirio.tech.core.api.v3.TCID;
import com.appirio.tech.core.api.v3.util.jwt.InvalidTokenException;
import com.appirio.tech.core.api.v3.util.jwt.JWTException;
import com.appirio.tech.core.api.v3.util.jwt.JWTToken;
import com.appirio.tech.core.api.v3.util.jwt.TokenExpiredException;

/**
 * @author sudo
 *
 */
public class JWTAuthenticator implements Authenticator<String, AuthUser> {

	private static final Logger logger = LoggerFactory.getLogger(JWTAuthenticator.class);

	public static final String DEFAULT_AUTH_DOMAIN = "topcoder-dev.com";

	private String secret;
	
	private String authDomain;
	
	public JWTAuthenticator(String authDomain, String secret) {
		if(secret==null || secret.length()==0)
			throw new IllegalArgumentException("secret must be specified.");
		
		this.secret = secret;
		this.authDomain = (authDomain==null || authDomain.length()==0) ? DEFAULT_AUTH_DOMAIN : authDomain;
	}

	/**
	 * @param token    raw JWT string in Authentication header
	 */
	@Override
	public Optional<AuthUser> authenticate(String token) throws AuthenticationException {
		try {
			JWTToken jwt = verifyToken(token);
			AuthUser user = new AuthUser();
			user.setUserId(new TCID(jwt.getUserId()));
			user.setToken(token);
			user.setHandle(jwt.getHandle());
			user.setEmail(jwt.getEmail());
			user.setRoles(jwt.getRoles());
			user.setAuthDomain(authDomain);

			return Optional.of(user);
		} catch (TokenExpiredException | InvalidTokenException e) {
			logger.info(String.format("Authentication failed with: %s, token: %s", e.getLocalizedMessage(), token));
			if(e instanceof TokenExpiredException)
				throw e; // re-throw TokenExpiredException to tell JWTAuthProvider an expiration occurred.
			
			return Optional.empty();
		} catch (JWTException e) {
			logger.error("Error occurred in authentication with error: " + e.getLocalizedMessage(), e);
			throw new AuthenticationException("Error occurred in authentication.", e);
		}
	}
	
	protected JWTToken verifyToken(String token) throws JWTException {
		JWTToken jwt = new JWTToken(token, getSecret());
		if(!jwt.isValidIssuerFor(getAuthDomain())) {
			throw new InvalidTokenException(token, String.format("The issuer is invalid: %s", jwt.getIssuer()));
		}
		return jwt;
	}
	
	protected String getSecret() {
		return secret;
	}

	protected void setSecret(String secret) {
		this.secret = secret;
	}

	public String getAuthDomain() {
		return authDomain;
	}

	public void setAuthDomain(String authDomain) {
		this.authDomain = authDomain;
	}
}
