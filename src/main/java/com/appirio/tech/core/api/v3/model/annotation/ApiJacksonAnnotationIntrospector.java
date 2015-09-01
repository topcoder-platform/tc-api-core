/**
 * 
 */
package com.appirio.tech.core.api.v3.model.annotation;

import com.appirio.tech.core.api.v3.response.ApiResponseFilter;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;

/**
 * Custom AnnotationIntrospector to fetch API specific Annotations.
 * 
 * @see http://wiki.fasterxml.com/JacksonFeatureJsonFilter
 * http://www.cowtowncoder.com/blog/archives/2011/09/entry_461.html
 * 
 * @author sudo
 *
 */
public class ApiJacksonAnnotationIntrospector extends JacksonAnnotationIntrospector {
	
	private static final long serialVersionUID = 1L;

	/**
	 * Searches for filters which also returns representation POJO filters to do API's partial response.
	 */
	@Override
	public Object findFilterId(Annotated a) {
		ApiRepresentation ann = a.getAnnotation(ApiRepresentation.class);
		if (ann != null) {
			return ApiResponseFilter.FILTER_NAME;
		}
		return super.findFilterId(a);
	}
}
