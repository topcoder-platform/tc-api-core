/**
 * 
 */
package com.appirio.tech.core.auth;

import com.appirio.tech.core.api.v3.TCID;

/**
 * A call to hold user information that was authenticated using API V3 authentication mechanism
 * 
 * @author sudo
 *
 */
public class AuthUser {
	private TCID userId;
	
	public TCID getUserId() {
		return userId;
	}
	public void setUserId(TCID userId) {
		this.userId = userId;
	}
	
}
