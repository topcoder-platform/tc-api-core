/**
 * 
 */
package com.appirio.tech.core.api.v2.response;

import com.appirio.tech.core.api.v2.ApiVersion;


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
		private Object content;
		public Result(Boolean success, Integer status, Object content) {
			this.success = success;
			this.status = status;
			this.content = content;
		}
		public Boolean getSuccess() {
			return success;
		}
		public Integer getStatus() {
			return status;
		}
		public Object getContent() {
			return content;
		}
		@Override
		public String toString() {
			return "{success:" + success + "},{status:" + status + "},{content:" + content + "}";
		}
	}

	private String id;
	private Result result;
	private ApiVersion version = ApiVersion.v2;
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
		this.result = new Result(success, status, content);
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
