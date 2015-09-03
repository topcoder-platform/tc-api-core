/**
 * 
 */
package com.appirio.tech.core.api.v3.request;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to handle orderby queries for API.
 * 
 * from v3,
 * orderBy can have multiple fields separated by comma.
 * ex)
 * 	orderBy=name,id
 * 	orderBy=name asc, id desc
 * 
 * @since v2
 * @author sudo
 *
 */
public class OrderByQuery {
	private List<OrderByItem> orderByItemList = new ArrayList<OrderByItem>();
	
	public class OrderByItem {
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
	}

	/**
	 * @deprecated V3 supports multiple orderBy fields. Use getItems().get(n).getOrderByField() instead.
	 */
	public String getOrderByField() {
		if(getItems().isEmpty()) return null;
		return getItems().get(0).getOrderByField();
	}
	/**
	 * @deprecated V3 supports multiple orderBy fields. Use getItems().get(n).getSortOrder() instead.
	 */
	public SortOrder getSortOrder() {
		if(getItems().isEmpty()) return null;
		return getItems().get(0).getSortOrder();
	}
	/**
	 * This will over write only the FIRST OrderByItem
	 * @deprecated V3 supports multiple orderBy fields. Use getItems().get(n).getOrderByField() instead.
	 */
	public void setOrderByField(String orderByField) {
		if(orderByItemList.isEmpty()) {
			orderByItemList.add(new OrderByItem());
		}
		orderByItemList.get(0).orderByField = orderByField;
	}
	/**
	 * This will over write only the FIRST OrderByItem
	 * @deprecated V3 supports multiple orderBy fields. Use getItems().get(n).setSortOrder() instead.
	 */
	public void setSortOrder(SortOrder sortOrder) {
		if(orderByItemList.isEmpty()) {
			orderByItemList.add(new OrderByItem());
		}
		orderByItemList.get(0).sortOrder = sortOrder;
	}
	public List<OrderByItem> getItems() {
		return orderByItemList;
	}
	
	private OrderByItem createNewItem() {
		OrderByItem item = new OrderByItem();
		item.setSortOrder(SortOrder.ASC_NULLS_FIRST); // this is the default.
		orderByItemList.add(item);
		return item;
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
		
		if (orderBy != null && !orderBy.isEmpty()) {
			String[] items = orderBy.split(",");
			for(String orderByItem : items) {
				OrderByItem item = query.createNewItem();
				String[] order = orderByItem.trim().split(" ");
				if (order.length > 0) {
					item.setOrderByField(order[0]);
					if (order.length > 1) {
						if (order[1].equalsIgnoreCase("asc")) {
							if (order.length > 2) {
								if (order[3].equalsIgnoreCase("last")) {
									item.setSortOrder(SortOrder.ASC_NULLS_LAST);
								}
							}
						} else {
							if (order.length > 2) {
								if (order[3].equalsIgnoreCase("last")) {
									item.setSortOrder(SortOrder.DESC_NULLS_LAST);
								} else {
									item.setSortOrder(SortOrder.DESC_NULLS_FIRST);
								}
							} else {
								//default is "nulls first"
								item.setSortOrder(SortOrder.DESC_NULLS_FIRST);
							}
						}
					}
				}
			}
		}
		return query;
	}
}
