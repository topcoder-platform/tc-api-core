package com.appirio.tech.core.api.v3;


import org.junit.Assert;

import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import com.appirio.tech.core.api.v3.dropwizard.APIApplication;

public class TestApplication extends APIApplication<TestConfiguration> {

	@Override
	public void initialize(Bootstrap<TestConfiguration> bootstrap) {
		super.initialize(bootstrap);
	}
	
	@Override
	public void run(TestConfiguration configuration, Environment environment)
			throws Exception {
		super.run(configuration, environment);
		
		// See resources/initializer_test.yml
		String foo = System.getProperty("FOO");
		Assert.assertEquals("foo", foo);
	}

}
