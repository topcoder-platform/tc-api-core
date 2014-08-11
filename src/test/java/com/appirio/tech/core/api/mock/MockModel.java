/**
 * 
 */
package com.appirio.tech.core.api.mock;

import com.appirio.tech.core.api.v2.CMCID;
import com.appirio.tech.core.api.v2.model.CMCResource;

/**
 * @author sudo
 *
 */
public class MockModel extends CMCResource {

	private CMCID id;
	private String strTest;
	private Integer intTest;
	
	public MockModel(CMCID id, String strTest, Integer intTest) {
		this.id = id;
		this.strTest = strTest;
		this.intTest = intTest;
	}
	
	public CMCID getId() {
		return id;
	}
	public void setId(CMCID id) {
		this.id = id;
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
}
