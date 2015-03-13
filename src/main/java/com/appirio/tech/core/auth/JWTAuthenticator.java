/**
 * 
 */
package com.appirio.tech.core.auth;

import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.appirio.tech.core.api.v3.TCID;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.JWTVerifyException;
import com.google.common.base.Optional;

/**
 * @author sudo
 *
 */
public class JWTAuthenticator implements Authenticator<String, AuthUser> {

	public static final String JWT_USER_ID = "userId";

	private static final Logger logger = LoggerFactory.getLogger(JWTAuthenticator.class);

	private static final String clientId = "JFDo7HMkf0q2CkVFHojy3zHWafziprhT";
	private static final String clientSecret = "0fjm47MSE1ea18WRPX9v3K6EM3iI8dc0OF5VNc-NMTNWEiwBwsmfjEYqOBW9HLhY";
	
	/**
	 * @param token    raw JWT string in Authentication header
	 */
	@Override
	public Optional<AuthUser> authenticate(String token) throws AuthenticationException {
		//@SuppressWarnings("static-access")
		// Fixing bug
		//JWTVerifier jwtVerifier = new JWTVerifier(Base64.decodeBase64(clientSecret), clientId);
		JWTVerifier jwtVerifier = new JWTVerifier(clientSecret.getBytes(), clientId);
		Map<String, Object> decoded;
		try {
			decoded = jwtVerifier.verify(token);
			logger.debug("Decoded JWT token" + decoded);

			AuthUser user = new AuthUser();
			user.setUserId(new TCID((String)decoded.get(JWT_USER_ID)));
			return Optional.of(user);
		} catch (InvalidKeyException | NoSuchAlgorithmException | IllegalStateException | SignatureException
				| IOException | JWTVerifyException e) {
			logger.debug("Error occured while decoding JWT token: " + e.getLocalizedMessage());
			throw new AuthenticationException("Authentication error occured: " + e.getLocalizedMessage());
		}
		//return Optional.absent();
	}

}
