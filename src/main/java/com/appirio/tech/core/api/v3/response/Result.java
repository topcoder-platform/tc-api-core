package com.appirio.tech.core.api.v3.response;

public class Result {
	private Boolean success;
	private Integer status;
	private Object metadata;
	private Object content;
	public Result() {}
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
	
	public void setSuccess(Boolean success) {
        this.success = success;
    }
    public void setStatus(Integer status) {
        this.status = status;
    }
    public void setMetadata(Object metadata) {
        this.metadata = metadata;
    }
    public void setContent(Object content) {
        this.content = content;
    }

    @Override
	public String toString() {
		return "{success:" + success + "},{status:" + status + "},{metadata:" + metadata + "},{content:" + content + "}";
	}
}