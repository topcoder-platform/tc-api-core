/**
 * 
 */
package com.appirio.tech.core.api.v3.request;

/**
 * Class to handle orderby queries for API
 * 
 * @since v2
 * @author sudo
 *
 */
public class OrderByQuery {
	private String orderByField;
	private SortOrder sortOrder;
	
	public String getOrderByField() {
		return orderByField;
	}
	public void setOrderByField(String orderByField) {
		this.orderByField = orderByField;
	}
	public SortOrder getSortOrder() {
		return sortOrder;
	}
	public void setSortOrder(SortOrder sortOrder) {
		this.sortOrder = sortOrder;
	}

	/**
	 * Creates this instance from raw string parameter.
	 * ASC_NULLS_FIRST is the default query order.
	 * 
	 * Ex.) orderBy=name asc nulls first
	 * 
	 * @param orderBy
	 */
	public static OrderByQuery instanceFromRaw(String orderBy) {
		OrderByQuery query = new OrderByQuery();
		query.setSortOrder(SortOrder.ASC_NULLS_FIRST); // this is the default.
		if (orderBy != null && !orderBy.isEmpty()) {
			String[] order = orderBy.split(" ");
			if (order.length > 0) {
				query.setOrderByField(order[0]);
				if (order.length > 1) {
					if (order[1].equalsIgnoreCase("asc")) {
						if (order.length > 2) {
							if (order[3].equalsIgnoreCase("last")) {
								query.setSortOrder(SortOrder.ASC_NULLS_LAST);
							}
						}
					} else {
						if (order.length > 2) {
							if (order[3].equalsIgnoreCase("last")) {
								query.setSortOrder(SortOrder.DESC_NULLS_LAST);
							} else {
								query.setSortOrder(SortOrder.DESC_NULLS_FIRST);
							}
						}
					}
				}
			}
		}
		return query;
	}
}
