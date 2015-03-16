/**
 * 
 */
package com.appirio.tech.core.api.v3.dropwizard;

import io.dropwizard.Configuration;

import java.util.Map;

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
}
