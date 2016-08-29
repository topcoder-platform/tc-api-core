/**
 * 
 */
package com.appirio.tech.core.auth;

import java.security.Principal;
import java.util.List;

import org.apache.log4j.Logger;

import com.appirio.tech.core.api.v3.TCID;

/**
 * A call to hold user information that was authenticated using API V3 authentication mechanism
 * 
 * @author sudo
 *
 */
public class AuthUser implements Principal {
	
	private static final Logger logger = Logger.getLogger(AuthUser.class);
	
	private TCID userId;
	
	private String token;
	
	private String handle;
	
	private String email;
	
	private List<String> roles;
	
	private String authDomain;
	

	@Override
	public String getName() {
		return this.handle;
	}

	public TCID getUserId() {
		return userId;
	}

	protected void setUserId(TCID userId) {
		this.userId = userId;
	}
	
	public String getHandle() {
		return handle;
	}

	protected void setHandle(String handle) {
		this.handle = handle;
	}

	public String getEmail() {
		return email;
	}

	protected void setEmail(String email) {
		this.email = email;
	}

	public List<String> getRoles() {
		return roles;
	}

	protected void setRoles(List<String> roles) {
		this.roles = roles;
	}

	public String getToken() {
		return token;
	}

	protected void setToken(String token) {
		this.token = token;
	}
	
	public String getAuthDomain() { return authDomain; }

	protected void setAuthDomain(String authDomain) { this.authDomain = authDomain; }

	public boolean hasRole(String role) {
		if(role==null)
			return false; //TODO: should throw an error?

		// TODO: Need a fix
		// Currently checking with roles in JWT. [SUP-3098]
		// This has the potential issue that any change in role assignment is not reflected immediately.
		if(this.roles==null || this.roles.size()==0)
			return false;
		return this.roles.contains(role);
	}
}
