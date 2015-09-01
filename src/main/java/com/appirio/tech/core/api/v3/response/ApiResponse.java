/**
 * 
 */
package com.appirio.tech.core.api.v3.response;

import java.io.IOException;
import java.rmi.server.UID;
import java.util.ArrayList;
import java.util.List;

import com.appirio.tech.core.api.v3.ApiVersion;
import com.appirio.tech.core.api.v3.dropwizard.APIApplication;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.type.TypeFactory;


/**
 * A response object wrapper for all API requests.
 * The class will get serialized into JSON format using {@link ApiHttpMessageConverter}
 * 
 * @since va1
 * @author sudo
 *
 */
public class ApiResponse {
	private String id;
	private Result result;
	private ApiVersion version;
	
	public ApiResponse() {
		id = new UID().toString();
		version = ApiVersion.v3;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Result getResult() {
		return result;
	}
	public void setResult(Result result) {
		this.result = result;
	}
	public void setResult(Boolean success, Integer status, Object content) {
		this.result = new Result(success, status, null, content);
	}
	public void setResult(Boolean success, Integer status, Object metadata, Object content) {
		this.result = new Result(success, status, metadata, content);
	}
	public ApiVersion getVersion() {
		return version;
	}
	public void setVersion(ApiVersion version) {
		this.version = version;
	}
	
	/**
	 * Utility method to return Resource content.
	 * 
	 * @param typeParameterClass
	 * 		POJO class or its array that this ApiResponse should be holding.
	 */
	@JsonIgnore
	public <T> T getContentResource(Class<T> typeParameterClass) throws JsonParseException, JsonMappingException, JsonProcessingException, IOException {
		return APIApplication.JACKSON_OBJECT_MAPPER.readValue(
				APIApplication.JACKSON_OBJECT_MAPPER.writeValueAsString(getResult().getContent()), typeParameterClass);
	}
	/**
	 * Utility method to return List of Resource content.
	 * Call example:
	 * 	getListContentResource(Sample.class)
	 * 	will return List<Sample>
	 * 
	 * @param clazz
	 * @return
	 * @throws IOException 
	 * @throws JsonProcessingException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 */
	@JsonIgnore
	public <T> List<T> getContentResourceList(Class<T> typeParameterClass) throws JsonParseException, JsonMappingException, JsonProcessingException, IOException {
		TypeFactory factory = TypeFactory.defaultInstance();
		List<T> list = APIApplication.JACKSON_OBJECT_MAPPER.readValue(
				APIApplication.JACKSON_OBJECT_MAPPER.writer().writeValueAsString(getResult().getContent()),
				factory.constructCollectionType(ArrayList.class, typeParameterClass));
		return list;
	}

	@Override
	public String toString() {
		return "{id:" + id + "}, {result:" + result + "}";
	}

}
