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
import java.net.URLEncoder;

/**
 * A call to hold user information that was authenticated using API V3 authentication mechanism
 * 
 * @author sudo
 *
 */
public class AuthUser {
	private TCID userId;
	private String token;
	private String authDomain;
	
	public TCID getUserId() {
		return userId;
	}

	public void setUserId(TCID userId) {
		this.userId = userId;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getAuthDomain() { return authDomain; }

	public void setAuthDomain(String authDomain) { this.authDomain = authDomain; }

	public boolean hasRole(String role) {

		String subjectId = this.getUserId().toString();

		return makeRequest("roles", role, "hasrole", subjectId);
	}

	public boolean isPermitted(String permission) {

		String subjectId = this.getUserId().toString();

		return makeRequest("permissions", permission, "ispermitted", subjectId);
	}

	private boolean makeRequest(String resource, String resourceId, String endPoint, String subjectId) {

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
			return false;
		}

		return false;
	}
}
