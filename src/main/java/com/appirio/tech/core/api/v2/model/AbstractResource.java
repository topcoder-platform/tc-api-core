/**
 * 
 */
package com.appirio.tech.core.api.v2.model;

import org.codehaus.jackson.map.annotate.JsonFilter;

import com.appirio.tech.core.api.v2.CMCID;
import com.appirio.tech.core.api.v2.controller.ApiBeanSerializeFilter;
import com.appirio.tech.core.api.v2.controller.ApiHttpMessageConverter;
import com.appirio.tech.core.api.v2.service.RESTService;

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
public abstract class AbstractResource implements RESTService {
	
	private CMCID accountId;
	
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
