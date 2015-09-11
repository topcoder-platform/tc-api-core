/**
 * 
 */
package com.appirio.tech.core.auth;

import com.appirio.tech.core.api.v3.TCID;
import com.appirio.tech.core.api.v3.response.ApiResponse;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;

import java.net.URLEncoder;
import java.util.List;

/**
 * A call to hold user information that was authenticated using API V3 authentication mechanism
 * 
 * @author sudo
 *
 */
public class AuthUser {
	
	private static final Logger logger = Logger.getLogger(AuthUser.class);
	
	private TCID userId;
	
	private String token;
	
	private String handle;
	
	private String email;
	
	private List<String> roles;
	
	private String authDomain;
	
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
		
		if(this.roles!=null && this.roles.size()>0) {
			return this.roles.contains(role);
		}
		String subjectId = this.getUserId().toString();
		return makeRequest("roles", role, "hasrole", subjectId);
	}

	public boolean isPermitted(String permission) {

		String subjectId = this.getUserId().toString();

		return makeRequest("permissions", permission, "ispermitted", subjectId);
	}

	protected boolean makeRequest(String resource, String resourceId, String endPoint, String subjectId) {

		ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);

		try {
			WebResource res = client.resource("http://"
					+ this.authDomain
					+ ":8080/v3/"
					+ resource.toString()
					+ "/"
					+ resourceId.toString()
					+ "/"
					+ endPoint.toString()
					+ "/"
					+ "?filter="
					+ URLEncoder.encode("subjectID="+ subjectId.toString(), "UTF-8")
					+ "&fields=&limit=&orderBy=");

			ApiResponse response = res.accept(
					MediaType.APPLICATION_JSON_TYPE).
					header("Authorization", "Bearer " + this.getToken()).
					get(ApiResponse.class);

			if (response.getResult().getStatus() == 200) {
				return true;
			}
		} catch(Exception e) {
			logger.error("Error in REST call. "+e.getMessage(), e);
			return false;
		}

		return false;
	}
}
