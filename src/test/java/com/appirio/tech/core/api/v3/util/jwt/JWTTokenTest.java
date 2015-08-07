package com.appirio.tech.core.api.v3.util.jwt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.auth0.jwt.Algorithm;

public class JWTTokenTest {

	@Test
	public void testWalkThrough() throws Exception {
		// data
		String secret = "SECRET-DUMMY";
		JWTToken jwt = new JWTToken();
		jwt.setIssuer("ISSUER-DUMMY");
		jwt.setUserId("USER-ID-DUMMY");
		jwt.setHandle("HANDLE-DUMMY");
		jwt.setEmail("EMAIL-DUMMY");
		jwt.setExpirySeconds(5*60);
		
		// generate token
		String token = jwt.generateToken(secret);
		assertNotNull(token);
		
		// decode token
		JWTToken jwtDec = new JWTToken(token, secret);
		
		// verify results
		assertEquals(jwt.getUserId(), jwtDec.getUserId());
		assertEquals(jwt.getIssuer(), jwtDec.getIssuer());
		assertEquals(jwt.getHandle(), jwtDec.getHandle());
		assertEquals(jwt.getEmail(), jwtDec.getEmail());
		assertEquals(jwt.getExpirySeconds(), jwtDec.getExpirySeconds());
	}
	
	@Test
	public void testCreateIssuerFor() throws Exception {
		// domain
		String authDomain = "topcoder-dev.com";

		// test
		JWTToken jwt = new JWTToken();
		String result = jwt.createIssuerFor(authDomain);
		
		// verify result
		assertNotNull(result);
		assertEquals(String.format(JWTToken.ISSUER_TEMPLATE, authDomain), result);
		
	}
	
	@Test
	public void testIsValidIssuerFor() throws Exception {
		// domain
		String authDomain = "topcoder-dev.com";
		String wrongDomain = "topcoder-qa.com";
		
		// test
		JWTToken jwt = new JWTToken();
		jwt.setIssuer(jwt.createIssuerFor(authDomain));
		
		// verify result
		assertTrue("should be valid for "+authDomain,
					jwt.isValidIssuerFor(authDomain));
		assertFalse("should not be valid for "+wrongDomain,
					jwt.isValidIssuerFor(wrongDomain));
	}
	
	
	@Test(expected=InvalidTokenException.class)
	public void testVerifyToken_ThrowsInvalidTokenExceptionWhenSecretIsDifferent() throws Exception {
		// data
		String secret = "SECRET-DUMMY";
		JWTToken jwt = new JWTToken();
		jwt.setIssuer("ISSUER-DUMMY");
		jwt.setUserId("USER-ID-DUMMY");
		
		// generate token
		String token = jwt.generateToken(secret);
		assertNotNull(token);
		
		// decode token
		String wrongSecret = "SECRET-WRONG";
		new JWTToken(token, wrongSecret);
	}
	
	@Test(expected=InvalidTokenException.class)
	public void testVerifyToken_ThrowsInvalidTokenExceptionWhenTokenIsInvalid() throws Exception {
		// data
		String secret = "SECRET-DUMMY";
		JWTToken jwt = new JWTToken();
		jwt.setIssuer("ISSUER-DUMMY");
		jwt.setUserId("USER-ID-DUMMY");
		
		// generate token
		String token = jwt.generateToken(secret);
		assertNotNull(token);
		
		// decode token
		String broken = token.replaceFirst("^.{7}", "GARBAGE");
		new JWTToken(token, broken);
	}
	
	@Test
	public void testVerifyToken_VerifiedWhenAlgorithmIsDifferent() throws Exception {
		// data
		String secret = "SECRET-DUMMY";
		JWTToken jwt = new JWTToken();
		jwt.setIssuer("ISSUER-DUMMY");
		jwt.setUserId("USER-ID-DUMMY");
		jwt.setHandle("HANDLE-DUMMY");
		jwt.setEmail("EMAIL-DUMMY");
		// specify algorithm
		jwt.setAlgorithm(Algorithm.HS512.name());
		
		// generate token
		String token = jwt.generateToken(secret);
		assertNotNull(token);
		
		// test - should be able to decode 
		JWTToken jwtDec = new JWTToken();
		assertNotEquals(jwtDec.getAlgorithm(), jwt.getAlgorithm());
		jwtDec.verifyAndApply(token, secret);

		// verify results
		assertEquals(jwt.getUserId(), jwtDec.getUserId());
		assertEquals(jwt.getIssuer(), jwtDec.getIssuer());
		assertEquals(jwt.getHandle(), jwtDec.getHandle());
		assertEquals(jwt.getEmail(), jwtDec.getEmail());
		assertEquals(jwt.getExpirySeconds(), jwtDec.getExpirySeconds());
	}

	@Test(expected=TokenExpiredException.class)
	public void testVerifyAuth0Token() throws Exception {
		
		String aoth0Token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJfaWQiOiI5ZDIxODQ3ODczZDNjNDMzYTM5ZTkyNDNlNmE5NDc2YyIsIm5hbWUiOiJzdWRvMDEyNCIsIm5pY2tuYW1lIjoic3VkbzAxMjQiLCJncm91cHMiOltdLCJkbiI6InVpZD0yMjc0ODc5MCxvdT1tZW1iZXJzLGRjPXRvcGNvZGVyLGRjPWNvbSIsImNsaWVudElEIjoiSkZEbzdITWtmMHEyQ2tWRkhvankzekhXYWZ6aXByaFQiLCJwaWN0dXJlIjoiaHR0cHM6Ly9zc2wuZ3N0YXRpYy5jb20vczIvcHJvZmlsZXMvaW1hZ2VzL3NpbGhvdWV0dGU4MC5wbmciLCJ1c2VyX2lkIjoiYWR8TERBUHwyMjc0ODc5MCIsImlkZW50aXRpZXMiOlt7InVzZXJfaWQiOiJMREFQfDIyNzQ4NzkwIiwicHJvdmlkZXIiOiJhZCIsImNvbm5lY3Rpb24iOiJMREFQIiwiaXNTb2NpYWwiOmZhbHNlfV0sImNyZWF0ZWRfYXQiOiIyMDE0LTEyLTEwVDA1OjEzOjQ2LjMwMVoiLCJnbG9iYWxfY2xpZW50X2lkIjoiQWtLMVFiWkhVd0VDMjhTaVNNa1FIOGVoSkpScDNpNDciLCJpc3MiOiJodHRwczovL3RvcGNvZGVyLWRldi5hdXRoMC5jb20vIiwic3ViIjoiYWR8TERBUHwyMjc0ODc5MCIsImF1ZCI6IkpGRG83SE1rZjBxMkNrVkZIb2p5M3pIV2FmemlwcmhUIiwiZXhwIjoxNDI1NjA1MTA5LCJpYXQiOjE0MjU1NjkxMDl9.iMoo4FeYP-91B973yzf1I_Q9XK7vn0wWZWKytUuO7xw";
		String secret = "0fjm47MSE1ea18WRPX9v3K6EM3iI8dc0OF5VNc-NMTNWEiwBwsmfjEYqOBW9HLhY";

		JWTToken auth0Jwt = new JWTToken() {
			@Override
			public JWTToken verifyAndApply(String token, String secret) throws JWTException {
				return verifyAndApply(token, secret, new Base64SecretEncoder());
			}
		};
		
		// test
		auth0Jwt.verifyAndApply(aoth0Token, secret);
	}
}
