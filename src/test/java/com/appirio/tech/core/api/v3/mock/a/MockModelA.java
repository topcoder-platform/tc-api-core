/**
 * 
 */
package com.appirio.tech.core.api.v3.mock.a;

import com.appirio.tech.core.api.v3.TCID;
import com.appirio.tech.core.api.v3.model.AbstractIdResource;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author sudo
 *
 */
public class MockModelA extends AbstractIdResource {

	private String strTest;
	private Integer intTest;
	private String dummyField = "dummy";
	
	public MockModelA() {}
	
	public MockModelA(TCID id, String strTest, Integer intTest) {
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
}
