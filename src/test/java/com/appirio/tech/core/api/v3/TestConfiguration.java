package com.appirio.tech.core.api.v3;

import com.appirio.tech.core.api.v3.dropwizard.APIBaseConfiguration;

public class TestConfiguration extends APIBaseConfiguration {

	@Override
	public boolean isUseResourceAutoRegistering() {
		return true;
	}
}
