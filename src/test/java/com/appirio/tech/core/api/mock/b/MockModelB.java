/**
 * 
 */
package com.appirio.tech.core.api.mock.b;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.springframework.stereotype.Component;

import com.appirio.tech.core.api.v2.CMCID;
import com.appirio.tech.core.api.v2.model.AbstractIdResource;

/**
 * @author sudo
 *
 */
@Component
public class MockModelB extends AbstractIdResource {

	public static final String RESOURCE_PATH = "mock_b_models";

	private String strTest;
	private Integer intTest;
	private String dummyField = "dummy";
	
	public MockModelB() {}
	
	public MockModelB(CMCID id, String strTest, Integer intTest) {
		super.setId(id);
		this.strTest = strTest;
		this.intTest = intTest;
	}
	
	public String getStrTest() {
		return strTest;
	}
	public void setStrTest(String strTest) {
		this.strTest = strTest;
	}
	public Integer getIntTest() {
		return intTest;
	}
	public void setIntTest(Integer intTest) {
		this.intTest = intTest;
	}
	/**
	 * Dummy field that Jackson Mapper should ignore
	 * @return
	 */
	@JsonIgnore
	public String getDummyField() {
		return dummyField;
	}
	public void setDummyField(String dummyField) {
		this.dummyField = dummyField;
	}

	public String getResourcePath() {
		return RESOURCE_PATH;
	}
}
