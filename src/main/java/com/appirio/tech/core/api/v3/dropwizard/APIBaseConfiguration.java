/**
 * 
 */
package com.appirio.tech.core.api.v3.dropwizard;

import java.util.List;

import io.dropwizard.Configuration;

/**
 * Configuration class to be passed upon DropWizard boot sequence.
 * The class is loaded from yaml file that is specified at main() arguments, using Jackson mapper to deseriarize.
 * 
 * @author sudo
 * 
 */
public class APIBaseConfiguration extends Configuration {
	
	/* TODO: Cleanup after sample is fixed.
	 * 
	List<String> v3services;
	
	public List<String> getV3services() {
		return v3services;
	}

	public void setV3services(List<String> v3services) {
		this.v3services = v3services;
	}
	*/
}
