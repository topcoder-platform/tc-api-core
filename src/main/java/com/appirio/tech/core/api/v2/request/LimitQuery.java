/**
 * 
 */
package com.appirio.tech.core.api.v2.request;

import com.appirio.tech.core.api.v2.CMCID;

/**
 * Class to handle limit/offset api queries
 * 
 * @since v2
 * @author sudo
 *
 */
public class LimitQuery {
	private Integer limit;
	private CMCID offsetId;
	private Integer offset;
	
	public LimitQuery(Integer limit) {
		this.limit = limit;
	}
	
	public LimitQuery(Integer limit, Integer offset) {
		this.limit = limit;
		this.offset = offset;
	}
	
	public LimitQuery(Integer limit, CMCID offsetId) {
		this.limit = limit;
		this.offsetId = offsetId;
	}
	
	public Integer getLimit() {
		return limit;
	}
	public void setLimit(Integer limit) {
		this.limit = limit;
	}
	public CMCID getOffsetId() {
		return offsetId;
	}
	public void setOffsetId(CMCID offsetId) {
		this.offsetId = offsetId;
	}
	public Integer getOffset() {
		return offset;
	}
	public void setOffset(Integer offset) {
		this.offset = offset;
	}

	/**
	 * Creates instance from raw parameter strings given from http access
	 */
	public static LimitQuery instanceFromRaw(String limit, String offset, String offsetId) {
		Integer l = null;
		Integer o;
		CMCID	oId;
		if(limit!=null && !limit.isEmpty()) {
			l = Integer.valueOf(limit);
			if(offset!=null && !offset.isEmpty()) {
				o = Integer.valueOf(offset);
				return new LimitQuery(l, o);
			} else if(offsetId!=null && !offsetId.isEmpty()) {
				oId = new CMCID(offsetId);
				return new LimitQuery(l, oId);
			}
		}
		return new LimitQuery(l);
	}

}
