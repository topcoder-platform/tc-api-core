/**
 * 
 */
package com.appirio.tech.core.api.v3.dropwizard;

import io.dropwizard.Configuration;

import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * Configuration class to be passed upon DropWizard boot sequence.
 * The class is loaded from yaml file that is specified at main() arguments, using Jackson mapper to deseriarize.
 * 
 * @author sudo
 * 
 */
public class APIBaseConfiguration extends Configuration {
	
	boolean useResourceAutoRegistering = false;
	
	Map<String, String> corsSettings;
	
	@Valid
	@NotNull
	String authDomain;
	
	public boolean isUseResourceAutoRegistering() {
		return useResourceAutoRegistering;
	}

	public void setUseResourceAutoRegistering(boolean useResourceAutoRegistering) {
		this.useResourceAutoRegistering = useResourceAutoRegistering;
	}

	public Map<String, String> getCorsSettings() {
		return corsSettings;
	}

	public void setCorsSettings(Map<String, String> corsSettings) {
		this.corsSettings = corsSettings;
	}

	public String getAuthDomain() {
		return authDomain;
	}

	public void setAuthDomain(String authDomain) {
		this.authDomain = authDomain;
	}
}
