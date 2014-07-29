/**
 * 
 */
package com.appirio.tech.core.api.v2.model;

import java.util.Set;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonFilter;

import com.appirio.tech.core.api.v2.CMCID;

/**
 * Base class to represent CMC Domain object.
 * 
 * The class is annotated with ApiResonseFilter which will be used to create Response for
 * api using {@link ApiBeanSerializeFilter} inside {@link ApiHttpMessageConverter}
 * 
 * CMCResource class was introduced to handle "virtual" models that do not have id (== persistent storage)
 * 
 * @author sudo
 *
 */
@JsonFilter("ApiResponseFilter")
public class CMCResource {
	private Set<String> serializeFields;
	private CMCID accountId;
	
	/**
	 * Get fields to serialize.
	 * 
	 * @see CMCResourceHelper#setSerializeFields(CMCObject, FieldSelector) for details.
	 * @return
	 */
	@JsonIgnore
	public Set<String> getSerializeFields() {
		return serializeFields;
	}
	
	public void setSerializeFields(Set<String> serializeFields) {
		this.serializeFields = serializeFields;
	}
	
	/**
	 * Returns account id of this resource resides.
	 * If resource is not data for specifiec Account, then accountId is null.
	 * @return
	 */
	public CMCID getAccountId() {
		return accountId;
	}
	
	public void setAccountId(CMCID accountId) {
		this.accountId = accountId;
	}
}
