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
import com.appirio.tech.core.api.v3.request.annotation.APIFieldParam;



public class FieldSelectorProvider extends AbstractValueFactoryProvider {

	/**
  	 * {@link APIFieldParam} injection resolver.
	 */
	@Singleton
	public static final class FieldSelectorInjectionResolver extends ParamInjectionResolver<APIFieldParam> {
		public FieldSelectorInjectionResolver() {
			super(FieldSelectorProvider.class);
		}
	}
	
	public static final class FieldSelectorFactory extends AbstractContainerRequestValueFactory<FieldSelector> {

		private Class<?> repClass;

		FieldSelectorFactory(Class<?> repClass) {
			this.repClass = repClass;
		}
		
		@Override
		public FieldSelector provide() {
			String fields = null;
			
			MultivaluedMap<String, String> params = getContainerRequest().getUriInfo().getQueryParameters();
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
	
	/**
	 * Injection constructor.
	 * @param mpep multivalued map parameter extractor provider.
	 * @param locator HK2 service locator.
	 */
	@Inject
	protected FieldSelectorProvider(
			MultivaluedParameterExtractorProvider mpep,
			ServiceLocator locator) {
		super(mpep, locator, Parameter.Source.UNKNOWN);
	}

	@Override
	protected Factory<?> createValueFactory(Parameter parameter) {
		APIFieldParam annotation = parameter.getAnnotation(APIFieldParam.class);
		Class<?> paramType = parameter.getRawType();
		if(annotation!=null && paramType.isAssignableFrom(FieldSelector.class)) {
			return new FieldSelectorFactory(annotation.repClass());
		}
		return null;
	}
	
    public static class Binder extends AbstractBinder {

        @Override
        protected void configure() {
			bind(FieldSelectorProvider.class)
			.to(ValueFactoryProvider.class).in(Singleton.class);
			bind(FieldSelectorInjectionResolver.class)
			.to(new TypeLiteral<InjectionResolver<APIFieldParam>>(){}).in(Singleton.class);
        }
    }
}