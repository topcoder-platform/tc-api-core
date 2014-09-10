/**
 * 
 */
package com.appirio.tech.core.api.v2.metadata;

/**
 * Metadata for resources that can include counts.
 * 
 * @author sudo
 *
 */
public class CountableMetadata extends Metadata {
	Integer totalCount;
	
	public Integer getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(Integer totalCount) {
		this.totalCount = totalCount;
	}
}
