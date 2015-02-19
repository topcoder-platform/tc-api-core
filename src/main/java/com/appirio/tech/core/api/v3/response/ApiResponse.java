/**
 * 
 */
package com.appirio.tech.core.api.v3.response;

import java.rmi.server.UID;

import com.appirio.tech.core.api.v3.ApiVersion;


/**
 * A response object wrapper for all API requests.
 * The class will get serialized into JSON format using {@link ApiHttpMessageConverter}
 * 
 * @since va1
 * @author sudo
 *
 */
public class ApiResponse {
	public class Result {
		private Boolean success;
		private Integer status;
		private Object metadata;
		private Object content;
		public Result(Boolean success, Integer status, Object metadata, Object content) {
			this.success = success;
			this.status = status;
			this.metadata = metadata;
			this.content = content;
		}
		public Boolean getSuccess() {
			return success;
		}
		public Integer getStatus() {
			return status;
		}
		public Object getMetadata() {
			return metadata;
		}
		public Object getContent() {
			return content;
		}
		@Override
		public String toString() {
			return "{success:" + success + "},{status:" + status + "},{metadata:" + metadata + "},{content:" + content + "}";
		}
	}

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
	@Override
	public String toString() {
		return "{id:" + id + "}, {result:" + result + "}";
	}
}
