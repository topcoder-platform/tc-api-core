/**
 * 
 */
package com.appirio.tech.core.auth;

import java.net.URLEncoder;

import javax.ws.rs.core.MediaType;

import com.appirio.tech.core.api.v3.TCID;
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
	String token;
	
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

	public boolean hasRole(String role){
		ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);

		String subjectID = this.getUserId().toString();

		WebResource res = client.resource("http://localhost:8080/roles?filter=" 
											+ URLEncoder.encode("ID="+role+"&subjectID="+subjectID)
											+ "&fields=&limit=&orderBy=");

		ApiResponse response = res.accept(
		        MediaType.APPLICATION_JSON_TYPE).
		        header("Authorization", "Bearer "+this.getToken()).
		        get(ApiResponse.class);

		if (response.getResult().getStatus() == 200)
			return true;
		else
			return false;
	}

	public boolean isPermitted(String permission) {
		ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);

		WebResource res = client.resource("http://localhost:8080/permissions");

		ApiResponse response = res.accept(
		        MediaType.APPLICATION_JSON_TYPE).
		        header("X-Authorization", "Bearer <jwt>").
		        get(ApiResponse.class);

		if (response.getResult().getStatus() == 200)
			return true;
		else
			return false;
	}
	
}
