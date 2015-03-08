/**
 * 
 */
package com.appirio.tech.core.auth;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.junit.Test;

import com.auth0.jwt.JWTExpiredException;
import com.auth0.jwt.JWTSigner;
import com.auth0.jwt.JWTSigner.Options;
import com.auth0.jwt.JWTVerifier;

/**
 * @author sudo
 *
 */
public class AuthenticationTest {
	
	public static final String secret = "0fjm47MSE1ea18WRPX9v3K6EM3iI8dc0OF5VNc-NMTNWEiwBwsmfjEYqOBW9HLhY";
	
	@Test
	public void testJWT() throws Exception {
		Map<String, Object> claims = new HashMap<String, Object>();
		claims.put("name", "sudo");
		claims.put("iss", "appirio:v3:150306");
		Options options = new Options();
		options.setExpirySeconds(60*10); //10 min.
		options.setIssuedAt(true);
		options.setJwtId(true);
		
		JWTSigner signer = new JWTSigner(secret);
		String token = signer.sign(claims, options);
		System.out.println(token);
		
		JWTVerifier verifier = new JWTVerifier(secret);
		verifier.verify(token);
	}
	
	@Test(expected=JWTExpiredException.class)
	public void testVerify() throws Exception {
		JWTVerifier verifier = new JWTVerifier(Base64.decodeBase64(secret));
		verifier.verify("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJfaWQiOiI5ZDIxODQ3ODczZDNjNDMzYTM5ZTkyNDNlNmE5NDc2YyIsIm5hbWUiOiJzdWRvMDEyNCIsIm5pY2tuYW1lIjoic3VkbzAxMjQiLCJncm91cHMiOltdLCJkbiI6InVpZD0yMjc0ODc5MCxvdT1tZW1iZXJzLGRjPXRvcGNvZGVyLGRjPWNvbSIsImNsaWVudElEIjoiSkZEbzdITWtmMHEyQ2tWRkhvankzekhXYWZ6aXByaFQiLCJwaWN0dXJlIjoiaHR0cHM6Ly9zc2wuZ3N0YXRpYy5jb20vczIvcHJvZmlsZXMvaW1hZ2VzL3NpbGhvdWV0dGU4MC5wbmciLCJ1c2VyX2lkIjoiYWR8TERBUHwyMjc0ODc5MCIsImlkZW50aXRpZXMiOlt7InVzZXJfaWQiOiJMREFQfDIyNzQ4NzkwIiwicHJvdmlkZXIiOiJhZCIsImNvbm5lY3Rpb24iOiJMREFQIiwiaXNTb2NpYWwiOmZhbHNlfV0sImNyZWF0ZWRfYXQiOiIyMDE0LTEyLTEwVDA1OjEzOjQ2LjMwMVoiLCJnbG9iYWxfY2xpZW50X2lkIjoiQWtLMVFiWkhVd0VDMjhTaVNNa1FIOGVoSkpScDNpNDciLCJpc3MiOiJodHRwczovL3RvcGNvZGVyLWRldi5hdXRoMC5jb20vIiwic3ViIjoiYWR8TERBUHwyMjc0ODc5MCIsImF1ZCI6IkpGRG83SE1rZjBxMkNrVkZIb2p5M3pIV2FmemlwcmhUIiwiZXhwIjoxNDI1NjA1MTA5LCJpYXQiOjE0MjU1NjkxMDl9.iMoo4FeYP-91B973yzf1I_Q9XK7vn0wWZWKytUuO7xw");
	}
}
