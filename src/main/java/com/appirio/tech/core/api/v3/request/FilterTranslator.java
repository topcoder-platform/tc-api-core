/**
 * 
 */
package com.appirio.tech.core.api.v3.request;

import com.appirio.tech.core.api.v3.exception.CMCParseException;
import com.appirio.tech.core.api.v3.util.ApiUtil;

/**
 * Translates {@link FilterParameter} values to query strings
 * 
 * @author sudo
 *
 */
public class FilterTranslator {
	
	/**
	 * Returns each of field conditions in SF where clause.
	 * see API spec doc for details.
	 * 
	 * example 1)
	 * 	api param:
	 *  	filter=story_id%3D'a41P00000000Eqd'
	 *  fieldName:
	 *  	story_id
	 *  sfFieldName:
	 *  	storyId
	 *  method returns:
	 * 		storyId='3D'a41P00000000Eqd'
	 * 
	 * example 2)
	 * 	api param:
	 *  	filter=story_id%3Din('a41P00000000Eqd'%2C+'a41P00000000Eqd')
	 *  fieldName:
	 *  	story_id
	 *  sfFieldName:
	 *  	storyId
	 *  method returns:
	 * 		storyId in ('3D'a41P00000000Eqd', 'a41P00000000Eqd')
	 * 
	 * 
	 * @param filter
	 * 			FilterParameter object from API
	 * @param fieldName
	 * 			field name specified in API filter
	 * @param sfFieldName
	 * 			Salesforce field name for the filter field
	 * @return
	 */
	public static String toSFWhereClauseItems(FilterParameter filter, String fieldName, String sfFieldName) {
		if(filter.get(fieldName).toString().toLowerCase().startsWith("in(")){
			String cond = filter.get(fieldName).toString().substring(2);
			return sfFieldName + " IN " + cond;
		} else {
			if(!filter.isLike()){
				return sfFieldName + "=" + filter.get(fieldName);
			} else if(filter.isLike()) {
				return sfFieldName + " Like '%" + ApiUtil.escapeSoqlForLike((String)filter.get(fieldName)) + "%'";
			}
		}
		throw new CMCParseException("Unable to parse field for filter:" + fieldName);
	}
}
