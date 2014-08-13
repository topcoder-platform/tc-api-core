package com.appirio.tech.core.api.v2.dao;

import java.util.List;

import com.appirio.tech.core.api.v2.CMCID;
import com.appirio.tech.core.api.v2.model.AbstractIdResource;
import com.appirio.tech.core.api.v2.request.FieldSelector;
import com.appirio.tech.core.api.v2.request.QueryParameter;

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
	 * Return a single CMCObject of the given record id.
	 * Fields will get populated from the defined selector.
	 * 
	 * @param selector
	 * @param recordId
	 * @return
	 * @throws SalesforceServiceException
	 */
	T populateById(FieldSelector selector, CMCID recordId) throws Exception;

	CMCID insert(T obj) throws Exception;
	
	CMCID update(T obj) throws Exception;

	void delete(CMCID id) throws Exception;

	/**
	 * Returns handling model class of this dao.
	 * The method is necessary since <T> is erased and can only be determined on runtime ("generics type erasure" issue)
	 * 
	 * @return
	 */
	public Class<T> getHandlingClass();
}
