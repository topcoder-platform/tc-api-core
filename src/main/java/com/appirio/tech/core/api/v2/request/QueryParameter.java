/**
 * 
 */
package com.appirio.tech.core.api.v2.request;


/**
 * Parameter parsing class for /v2 query string.
 * 
 * @author sudo
 *
 */
public class QueryParameter {

	private FieldSelector selector;
	private FilterParameter filter;
	private LimitQuery limit;
	private OrderByQuery orderBy;
	
	public QueryParameter(FieldSelector fields) {
		this.selector = fields;
	}
	
	public QueryParameter(FieldSelector fields, FilterParameter filter, LimitQuery limit, OrderByQuery orderBy){
		this.selector = fields;
		this.filter = filter;
		this.limit = limit;
		this.orderBy = orderBy;
	}
	
	public FieldSelector getFieldSelector() {
		return selector;
	}

	public FilterParameter getFilterParameter() {
		return filter;
	}

	public FieldSelector getSelector() {
		return selector;
	}

	public void setSelector(FieldSelector selector) {
		this.selector = selector;
	}

	public FilterParameter getFilter() {
		return filter;
	}

	public void setFilter(FilterParameter filter) {
		this.filter = filter;
	}

	public LimitQuery getLimit() {
		return limit;
	}

	public void setLimit(LimitQuery limit) {
		this.limit = limit;
	}

	public OrderByQuery getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(OrderByQuery orderBy) {
		this.orderBy = orderBy;
	}

}
