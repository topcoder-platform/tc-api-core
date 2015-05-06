/**
 * 
 */
package com.appirio.tech.core.auth;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.ws.rs.core.MediaType;

import com.appirio.tech.core.api.v3.TCID;
import com.appirio.tech.core.api.v3.exception.APIRuntimeException;
import com.appirio.tech.core.api.v3.response.ApiResponse;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

/**
 * A call to hold user information that was authenticated using API V3 authentication mechanism
 * 
 * @author sudo
 *
 */
public class AuthUser {
	private TCID userId;
	private String token;
	
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
			WebResource res = client.resource("http://localhost:8080/v3/"
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
