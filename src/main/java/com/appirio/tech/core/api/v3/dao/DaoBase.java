package com.appirio.tech.core.api.v3.dao;

import java.util.List;

import com.appirio.tech.core.api.v3.TCID;
import com.appirio.tech.core.api.v3.model.AbstractIdResource;
import com.appirio.tech.core.api.v3.request.FieldSelector;
import com.appirio.tech.core.api.v3.request.QueryParameter;

public interface DaoBase <T extends AbstractIdResource> {

	/**
	 * Return list of CMCObjects with given selector and filter
	 * 
	 * @param query
	 * @return
	 * @throws SalesforceServiceException
	 */
	List<T> populate(QueryParameter query) throws Exception;

	/**
	 * Return a single object of the given record id.
	 * Fields will get populated from the defined selector.
	 * 
	 * @param selector
	 * @param recordId
	 * @return
	 * @throws SalesforceServiceException
	 */
	T populateById(FieldSelector selector, TCID recordId) throws Exception;

	TCID insert(T obj) throws Exception;
	
	TCID update(T obj) throws Exception;

	void delete(TCID id) throws Exception;

}
