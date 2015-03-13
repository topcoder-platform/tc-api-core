/**
 * 
 */
package com.appirio.tech.core.api.v3.mock.b;

import com.appirio.tech.core.api.v3.TCID;
import com.appirio.tech.core.api.v3.model.AbstractIdResource;
import com.appirio.tech.core.api.v3.model.annotation.ApiMapping;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author sudo
 *
 */
public class MockModelB extends AbstractIdResource {

	private String strTest;
	private Integer intTest;
	private String dummyField = "dummy";
	private String apiHidden = "dummy";
	private String apiDefaultHidden = "default_hidden";
	
	public MockModelB() {}
	
	public MockModelB(TCID id, String strTest, Integer intTest) {
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

	@JsonIgnore
	@ApiMapping(visible=false)
	public String getApiHidden() {
		return apiHidden;
	}

	public void setApiHidden(String apiHidden) {
		this.apiHidden = apiHidden;
	}

	@JsonIgnore
	@ApiMapping(queryDefault=false)
	public String getApiDefaultHidden() {
		return apiDefaultHidden;
	}

	public void setApiDefaultHidden(String apiDefaultHidden) {
		this.apiDefaultHidden = apiDefaultHidden;
	}
}
