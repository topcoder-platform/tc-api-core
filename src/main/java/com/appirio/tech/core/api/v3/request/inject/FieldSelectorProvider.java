/**
 * 
 */
package com.appirio.tech.core.api.v3.request.inject;

import java.util.List;
import java.util.Map.Entry;

import javax.ws.rs.core.MultivaluedMap;

import com.appirio.tech.core.api.v3.model.ResourceHelper;
import com.appirio.tech.core.api.v3.request.FieldSelector;
import com.appirio.tech.core.api.v3.request.annotation.APIFieldParam;
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
public class FieldSelectorProvider implements InjectableProvider<APIFieldParam, Parameter> {

	public static class FieldSelectorInjectable extends AbstractHttpContextInjectable<FieldSelector> {
		
		private Class<?> repClass;
		
		public FieldSelectorInjectable(Class<?> repClass) {
			this.repClass = repClass;
		}

		@Override
		public FieldSelector getValue(HttpContext c) {
			String fields = null;
			
			MultivaluedMap<String, String> params = c.getUriInfo().getQueryParameters();
			// Populate FieldSelector if query param and value is present
			for (Entry<String, List<String>> param : params.entrySet()) {
				String key = param.getKey();
				String value = param.getValue().iterator().next();
				if(key.equals("fields") && value!=null) {
					fields = value;
					break;
				}
			}
			/* Push to move ResrouceHelper --> TODO: Cleanup if this works.
			// If value isn't present, assign default fields to populate
			if(fields==null) {
				fields = "";
				for(String field : ResourceHelper.getDefaultFields(repClass)) {
					fields = field + ",";
				}
				//remove the last ","
				fields = fields.replaceAll(",$", "").trim();
			}
			*/
			FieldSelector selector;
			if(fields != null) {
				selector = FieldSelector.instanceFromV2String(fields);
			} else {
				selector = ResourceHelper.getDefaultFieldSelector(repClass);
			}

			return selector;
		}
	}

	@Override
	public ComponentScope getScope() {
		return ComponentScope.PerRequest;
	}

	@Override
	public Injectable<?> getInjectable(ComponentContext ic, APIFieldParam a, Parameter c) {
		return new FieldSelectorInjectable(a.repClass());
	}

}
