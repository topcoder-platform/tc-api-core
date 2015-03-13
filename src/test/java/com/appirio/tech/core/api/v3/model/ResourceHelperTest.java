package com.appirio.tech.core.api.v3.model;

import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import com.appirio.tech.core.api.v3.dropwizard.APIApplication;
import com.appirio.tech.core.api.v3.model.annotation.ApiMapping;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ResourceHelperTest {
	
	public static class TestPojo {
		private boolean test1;
		private String test2;
		private Integer test3;
		private String test4;
		private Integer test5;
		private String test6;
		private Integer test7;
		public boolean isTest1() {
			return test1;
		}
		public void setTest1(boolean test1) {
			this.test1 = test1;
		}
		public String getTest2() {
			return test2;
		}
		public void setTest2(String test2) {
			this.test2 = test2;
		}
		@JsonIgnore
		public Integer getTest3() {
			return test3;
		}
		public void setTest3(Integer test3) {
			this.test3 = test3;
		}
		@ApiMapping(visible=false)
		public String getTest4() {
			return test4;
		}
		public void setTest4(String test4) {
			this.test4 = test4;
		}
		@ApiMapping(queryDefault=false)
		public Integer getTest5() {
			return test5;
		}
		public void setTest5(Integer test5) {
			this.test5 = test5;
		}
		@ApiMapping(visible=true)
		public String getTest6() {
			return test6;
		}
		public void setTest6(String test6) {
			this.test6 = test6;
		}
		@ApiMapping(queryDefault=true)
		public Integer getTest7() {
			return test7;
		}
		public void setTest7(Integer test7) {
			this.test7 = test7;
		}
	}
	
	@Test
	public void testDefaultFields() {
		//Set app's ObjectMapper just to test out the method, since we don't want to run dw just to test the utility method.
		APIApplication.JACKSON_OBJECT_MAPPER = new ObjectMapper();
		
		Set<String> fieldSet = ResourceHelper.getDefaultFields(TestPojo.class);
		System.out.println(fieldSet);
		//Should see
		Assert.assertTrue(fieldSet.contains("test1"));
		Assert.assertTrue(fieldSet.contains("test2"));
		Assert.assertTrue(fieldSet.contains("test6"));
		Assert.assertTrue(fieldSet.contains("test7"));
		//Should NOT see
		Assert.assertFalse(fieldSet.contains("test3"));
		Assert.assertFalse(fieldSet.contains("test4"));
		Assert.assertFalse(fieldSet.contains("test5"));
	}
}
