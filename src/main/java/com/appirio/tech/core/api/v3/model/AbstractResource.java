/**
 * 
 */
package com.appirio.tech.core.api.v3.model;

import org.codehaus.jackson.map.annotate.JsonFilter;

import com.appirio.tech.core.api.v3.TCID;
import com.appirio.tech.core.api.v3.controller.ApiBeanSerializeFilter;
import com.appirio.tech.core.api.v3.controller.ApiHttpMessageConverter;
import com.appirio.tech.core.api.v3.service.RESTService;

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
	
	private TCID accountId;
	
	/**
	 * Returns account id of this resource resides.
	 * If resource is not data for specifiec Account, then accountId is null.
	 * @return
	 */
	public TCID getAccountId() {
		return accountId;
	}
	
	public void setAccountId(TCID accountId) {
		this.accountId = accountId;
	}
}
