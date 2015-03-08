/**
 * 
 */
package com.appirio.tech.core.api.v3.request;


/**
 * Parameter parsing class for /v3 query string.
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

	public LimitQuery getLimitQuery() {
		return limit;
	}

	public void setLimitQuery(LimitQuery limit) {
		this.limit = limit;
	}

	public OrderByQuery getOrderByQuery() {
		return orderBy;
	}

	public void setOrderByQuery(OrderByQuery orderBy) {
		this.orderBy = orderBy;
	}

}
