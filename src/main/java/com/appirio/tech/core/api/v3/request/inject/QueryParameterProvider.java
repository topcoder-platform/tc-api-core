/**
 * 
 */
package com.appirio.tech.core.api.v3.request.inject;

import java.util.List;
import java.util.Map.Entry;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.core.MultivaluedMap;

import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.api.InjectionResolver;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.TypeLiteral;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.internal.inject.AbstractContainerRequestValueFactory;
import org.glassfish.jersey.server.internal.inject.AbstractValueFactoryProvider;
import org.glassfish.jersey.server.internal.inject.MultivaluedParameterExtractorProvider;
import org.glassfish.jersey.server.internal.inject.ParamInjectionResolver;
import org.glassfish.jersey.server.model.Parameter;
import org.glassfish.jersey.server.spi.internal.ValueFactoryProvider;

import com.appirio.tech.core.api.v3.model.ResourceHelper;
import com.appirio.tech.core.api.v3.request.FieldSelector;
import com.appirio.tech.core.api.v3.request.FilterParameter;
import com.appirio.tech.core.api.v3.request.LimitQuery;
import com.appirio.tech.core.api.v3.request.OrderByQuery;
import com.appirio.tech.core.api.v3.request.QueryParameter;
import com.appirio.tech.core.api.v3.request.annotation.APIQueryParam;

public class QueryParameterProvider extends AbstractValueFactoryProvider {
	
	/**
  	 * {@link APIQueryParam} injection resolver.
	 */
	@Singleton
	public static final class QueryParameterInjectionResolver extends ParamInjectionResolver<APIQueryParam> {
		public QueryParameterInjectionResolver() {
			super(QueryParameterProvider.class);
		}
	}
	
	public static final class QueryParameterFactory extends AbstractContainerRequestValueFactory<QueryParameter> {
		
		private Class<?> repClass;
		
		QueryParameterFactory(Class<?> repClass) {
			this.repClass = repClass;
		}
		
		@Override
		public QueryParameter provide() {
			String fields = null;
			String filter = null;
			String limit = null;
			String offset = null;
			String offsetId = null;
			String orderBy = null;
			//String include = null; TODO: implement V3 "include" parameter
			
			MultivaluedMap<String, String> params = getContainerRequest().getUriInfo().getQueryParameters();
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
				}/* TODO: implement V3 "include" parameter
				 else if (key.equals("include") && value!=null) {
					include = value;
				}*/
			}
			
			// Populate FieldSelector. If value isn't present, assign default fields to populate
			FieldSelector selector;
			if(fields != null) {
				selector = FieldSelector.instanceFromV2String(fields);
			} else {
				selector = ResourceHelper.getDefaultFieldSelector(repClass);
			}
			
			return new QueryParameter(selector,
					new FilterParameter(filter),
					LimitQuery.instanceFromRaw(limit, offset, offsetId),
					OrderByQuery.instanceFromRaw(orderBy));
		}
	}
	
	@Inject
	protected QueryParameterProvider(
			MultivaluedParameterExtractorProvider mpep, ServiceLocator locator) {
		super(mpep, locator, Parameter.Source.UNKNOWN);
	}

	@Override
	protected Factory<?> createValueFactory(Parameter parameter) {
		APIQueryParam annotation = parameter.getAnnotation(APIQueryParam.class);
		Class<?> paramType = parameter.getRawType();
		if(annotation!=null && paramType.isAssignableFrom(QueryParameter.class)) {
			return new QueryParameterFactory(annotation.repClass());
		}
		return null;
	}
	
    public static class Binder extends AbstractBinder {

        @Override
        protected void configure() {
			bind(QueryParameterProvider.class)
			.to(ValueFactoryProvider.class).in(Singleton.class);
			bind(QueryParameterInjectionResolver.class)
			.to(new TypeLiteral<InjectionResolver<APIQueryParam>>(){}).in(Singleton.class);
        }
    }
}
