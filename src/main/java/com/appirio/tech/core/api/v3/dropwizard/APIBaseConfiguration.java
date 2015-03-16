/**
 * 
 */
package com.appirio.tech.core.api.v3.dropwizard;

import java.util.List;
import java.util.Map;

import io.dropwizard.Configuration;

/**
 * Configuration class to be passed upon DropWizard boot sequence.
 * The class is loaded from yaml file that is specified at main() arguments, using Jackson mapper to deseriarize.
 * 
 * @author sudo
 * 
 */
public class APIBaseConfiguration extends Configuration {
	
<<<<<<< HEAD
	boolean useResourceAutoRegistering = false;
	
=======
	/* TODO: Cleanup after sample is fixed.
	 * 
>>>>>>> sudo-v3api
	List<String> v3services;
	
	Map<String, String> corsSettings;
	
	
	public boolean isUseResourceAutoRegistering() {
		return useResourceAutoRegistering;
	}

	public void setUseResourceAutoRegistering(boolean useResourceAutoRegistering) {
		this.useResourceAutoRegistering = useResourceAutoRegistering;
	}

	public List<String> getV3services() {
		return v3services;
	}

	public void setV3services(List<String> v3services) {
		this.v3services = v3services;
	}
<<<<<<< HEAD
	
	public Map<String, String> getCorsSettings() {
		return corsSettings;
	}

	public void setCorsSettings(Map<String, String> corsSettings) {
		this.corsSettings = corsSettings;
	}
=======
	*/
>>>>>>> sudo-v3api
}
