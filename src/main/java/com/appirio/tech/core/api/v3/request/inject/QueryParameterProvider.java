/**
 * 
 */
package com.appirio.tech.core.api.v3.request.inject;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map.Entry;

import javax.ws.rs.core.MultivaluedMap;

import com.appirio.tech.core.api.v3.exception.APIParseException;
import com.appirio.tech.core.api.v3.model.ResourceHelper;
import com.appirio.tech.core.api.v3.request.FieldSelector;
import com.appirio.tech.core.api.v3.request.FilterParameter;
import com.appirio.tech.core.api.v3.request.LimitQuery;
import com.appirio.tech.core.api.v3.request.OrderByQuery;
import com.appirio.tech.core.api.v3.request.QueryParameter;
import com.appirio.tech.core.api.v3.request.annotation.APIQueryParam;
import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.api.model.Parameter;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.server.impl.inject.AbstractHttpContextInjectable;
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.spi.inject.InjectableProvider;

/**
 * @author sudo
 * 
 */
public class QueryParameterProvider implements InjectableProvider<APIQueryParam, Parameter> {

	public static class QueryParameterInjectable extends AbstractHttpContextInjectable<QueryParameter> {
		
		private Class<?> repClass;
		
		public QueryParameterInjectable(Class<?> repClass) {
			this.repClass = repClass;
		}

		@Override
		public QueryParameter getValue(HttpContext c) {
			String fields = null;
			String filter = null;
			String limit = null;
			String offset = null;
			String offsetId = null;
			String orderBy = null;
			String include = null;
			
			MultivaluedMap<String, String> params = c.getUriInfo().getQueryParameters();
			// Populate URI for query param and value is present
			for (Entry<String, List<String>> param : params.entrySet()) {
				String key = param.getKey();
				String value = param.getValue().iterator().next();
				if(key.equals("fields") && value!=null) {
					fields = value;
				} else if (key.equals("filter") && value!=null) {
					filter = value;
				} else if (key.equals("limit") && value!=null) {
					limit = value;
				} else if (key.equals("offset") && value!=null) {
					offset = value;
				} else if (key.equals("offsetId") && value!=null) {
					offsetId = value;
				} else if (key.equals("orderBy") && value!=null) {
					orderBy = value;
				} else if (key.equals("include") && value!=null) {
					include = value;
				}
			}
			
			// Populate FieldSelector. If value isn't present, assign default fields to populate
			FieldSelector selector;
			if(fields != null) {
				selector = FieldSelector.instanceFromV2String(fields);
			} else {
				selector = ResourceHelper.getDefaultFieldSelector(repClass);
			}
			
			try {
				return new QueryParameter(selector,
						FilterParameter.fromEncodedString(filter),
						LimitQuery.instanceFromRaw(limit, offset, offsetId),
						OrderByQuery.instanceFromRaw(orderBy));
			} catch (UnsupportedEncodingException e) {
				throw new APIParseException("Failed to parse Query Parameter", e);
			}
		}
	}

	@Override
	public ComponentScope getScope() {
		return ComponentScope.PerRequest;
	}

	@Override
	public Injectable<?> getInjectable(ComponentContext ic, APIQueryParam a, Parameter c) {
		return new QueryParameterInjectable(a.repClass());
	}

}
