package com.appirio.tech.core.api.v3.util.jwt;

import java.security.SignatureException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

import com.auth0.jwt.Algorithm;
import com.auth0.jwt.JWTExpiredException;
import com.auth0.jwt.JWTSigner;
import com.auth0.jwt.JWTSigner.Options;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.JWTVerifyException;

public class JWTToken {
	
	private static final Logger logger = Logger.getLogger(JWTToken.class);

	public static final String CLAIM_USER_ID = "userId";
	public static final String CLAIM_HANDLE = "handle";
	public static final String CLAIM_EMAIL = "email";
	
	public static final String CLAIM_ISSUER  = "iss";
	public static final String CLAIM_ISSUED_TIME = "iat";
	public static final String CLAIM_EXPIRATION_TIME = "exp";
	
	public static final int DEFAULT_EXP_SECONDS = 10 * 60; //10 mins
	
	public static final String ISSUER_TEMPLATE = "https://api.%s";
	
	public String userId;
	
	public String handle;
	
	public String email;
	
	public String issuer;
	
	public Integer expirySeconds = DEFAULT_EXP_SECONDS;
	
	public String algorithm = Algorithm.HS256.name();
	
	protected SecretEncoder encoder = new SecretEncoder();
	
	public JWTToken(){
	}

	public JWTToken(String token, String secret){
		verifyAndApply(token, secret);
	}

	public String generateToken(String secret) {
		if(secret==null || secret.length()==0)
			throw new IllegalArgumentException("secret must be specified");
		
		Map<String, Object> claims = new HashMap<String, Object>();
		claims.put(CLAIM_USER_ID, getUserId());
		claims.put(CLAIM_HANDLE, getHandle());
		claims.put(CLAIM_EMAIL, getEmail());
		claims.put(CLAIM_ISSUER, getIssuer());
		
		Options options = new Options();
		if(getAlgorithm()!=null) {
			options.setAlgorithm(Algorithm.valueOf(getAlgorithm()));
		}
		options.setExpirySeconds(getExpirySeconds()!=null ? getExpirySeconds() : DEFAULT_EXP_SECONDS);
		options.setIssuedAt(true); // auto
		options.setJwtId(true); // auto
		
		JWTSigner signer = new JWTSigner(secret);
		return signer.sign(claims, options);
	}
	
	public JWTToken verifyAndApply(String token, String secret) throws JWTException {
		return verifyAndApply(token, secret, this.encoder);
	}
	
	protected JWTToken verifyAndApply(String token, String secret, SecretEncoder enc) throws JWTException {
		if(secret==null || secret.length()==0)
			throw new IllegalArgumentException("secret must be specified.");
		if(enc==null)
			enc = new SecretEncoder();
		return verifyAndApply(token, enc.encode(secret));
	}
	
	protected JWTToken verifyAndApply(String token, byte[] secretBytes) throws JWTException {
		if(token==null)
			throw new IllegalArgumentException("token must be specified.");
		
		Map<String, Object> map = null;
		try {
			JWTVerifier verifier = new JWTVerifier(secretBytes);
			map = verifier.verify(token);
			logger.debug("Decoded JWT token" + map);
		} catch (JWTExpiredException e) {
			throw new TokenExpiredException(token, "Token is expired.", e);
		} catch (JWTVerifyException | SignatureException | IllegalStateException e) {
			throw new InvalidTokenException(token, "Token is invalid. "+e.getLocalizedMessage(), e);
 		} catch (Exception e) {
 			throw new JWTException(token, "Error occurred in verifying token. "+e.getLocalizedMessage(), e);
 		}
		setUserId((String)map.get(CLAIM_USER_ID));
		setHandle((String)map.get(CLAIM_HANDLE));
		setEmail((String)map.get(CLAIM_EMAIL));
		setIssuer((String)map.get(CLAIM_ISSUER));
		Integer iat = (Integer)map.get(CLAIM_ISSUED_TIME);
		Integer exp = (Integer)map.get(CLAIM_EXPIRATION_TIME);
		if(exp!=null)
			setExpirySeconds(calcExpirySeconds(exp, iat));
		return this;
	}

	protected Integer calcExpirySeconds(Integer exp, Integer iat) {
		if(exp==null)
			return null;
		int issuedAt = iat != null ? iat : (int)(System.currentTimeMillis()/1000L);
		return exp - issuedAt;
	}
	
	/**
	 * Creates iss field data from the domain.
	 * @param authDomain
	 * @return
	 */
	public String createIssuerFor(String authDomain) {
		if(authDomain==null || authDomain.length()==0)
			throw new IllegalArgumentException("authDomain must be specifeid.");
		return String.format(ISSUER_TEMPLATE, authDomain);
	}
	
	public boolean isValidIssuerFor(String authDomain) {
		return createIssuerFor(authDomain).equals(getIssuer());
	}
	
	public String getUserId() {
		return userId;
	}

    /**
     * Set JWT claim "userId" (private).
     */
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	public String getHandle() {
		return handle;
	}

    /**
     * Set JWT claim "handle" (private).
     */
	public void setHandle(String handle) {
		this.handle = handle;
	}

	public String getEmail() {
		return email;
	}

    /**
     * Set JWT claim "email" (private).
     */
	public void setEmail(String email) {
		this.email = email;
	}

	public String getIssuer() {
		return issuer;
	}

    /**
     * Set JWT claim "iss".
     */
	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}

	public Integer getExpirySeconds() {
		return expirySeconds;
	}
	
    /**
     * Set JWT claim "exp" to current timestamp plus this value.
     */
	public void setExpirySeconds(Integer expirySeconds) {
		this.expirySeconds = expirySeconds;
	}
	
	
	public String getAlgorithm() {
		return algorithm;
	}

	/**
	 * Set algorithm (default: HS256 [HMAC SHA-256])
	 */
	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}
	
	public static class SecretEncoder {
		public byte[] encode(String secret) { return secret!=null ? secret.getBytes() : null; }
	}
	
	public static class Base64SecretEncoder extends SecretEncoder {
		public byte[] encode(String secret) { return secret!=null ? Base64.decodeBase64(secret) : null; }
	}
}
