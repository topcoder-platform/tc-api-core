/**
 * 
 */
package com.appirio.tech.core.api.v3.dropwizard;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration.Dynamic;
import javax.ws.rs.Path;

import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.appirio.tech.core.api.v3.exception.ResourceInitializationException;
import com.appirio.tech.core.api.v3.model.annotation.ApiJacksonAnnotationIntrospector;
import com.appirio.tech.core.api.v3.request.inject.FieldSelectorProvider;
import com.appirio.tech.core.api.v3.request.inject.QueryParameterProvider;
import com.appirio.tech.core.api.v3.response.ApiResponseFilter;
import com.appirio.tech.core.auth.JWTAuthProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import com.sun.jersey.spi.container.ContainerResponseFilter;

/**
 * Application entry point for DropWizard framework.
 * This class is the standard class to run API V3 based RESTful apps.
 * 
 * @author sudo
 * 
 */
public class APIApplication<T extends APIBaseConfiguration> extends Application<T> {

	private static final Logger logger = LoggerFactory.getLogger(APIApplication.class);

	/**
	 * The jackson object mapper this application uses.
	 * Instance will be populated during dropwizard's initialization process.
	 */
	public static ObjectMapper JACKSON_OBJECT_MAPPER;

	@Override
	public String getName() {
		return "V3API-Application";
	}

	@Override
	public void initialize(Bootstrap<T> bootstrap) {
		//V3 API communicates in ISO8601 format for DateTime
		bootstrap.getObjectMapper().setDateFormat(ISO8601DateFormat.getInstance());
		JACKSON_OBJECT_MAPPER = bootstrap.getObjectMapper();
		
		//Register Jackson annotation introspector to add additional annotations for API usage,
		//and register default filter that will ignore additional annotation filters if not present during seriarization.
		ApiJacksonAnnotationIntrospector intr = new ApiJacksonAnnotationIntrospector();
		JACKSON_OBJECT_MAPPER.setAnnotationIntrospector(intr);
		SimpleFilterProvider filters = new SimpleFilterProvider();
		filters.setFailOnUnknownId(false);
		JACKSON_OBJECT_MAPPER.setFilters(filters);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void run(T configuration, Environment environment) throws Exception {
		//delegate.run(this, configuration, environment);
		configureCors(configuration, environment);
		// Register filters
		configureFilters(configuration, environment);

		environment.jersey().setUrlPattern("/v3/*");
		
		//Find all Resource class and register them to jersey if auto registering is set true.
		if(configuration.isUseResourceAutoRegistering()) {
			for(Object resource : getAllResources()) {
				logger.debug("Registering Resource to Jersey:" + resource.toString());
				environment.jersey().register(resource);
			}
		}
		
		//Register V3 API query/put/post/delete parameter objects to map into annotated instances
		environment.jersey().register(new FieldSelectorProvider());
		environment.jersey().register(new QueryParameterProvider());
		
		//Register V3 API response filter for GET call (handling partial response and includes param)
		environment.jersey().getResourceConfig().getContainerResponseFilters().add(new ApiResponseFilter(JACKSON_OBJECT_MAPPER));
		
		//Register Authentication Provider to validate JWT with @Auth annotation
		String authDomain = configuration.getAuthDomain();
		if(authDomain==null || authDomain.length()==0)
			authDomain = JWTAuthProvider.DEFAULT_AUTH_DOMAIN; // default
		environment.jersey().register(new JWTAuthProvider(authDomain));
		
		//Register ExceptionMapper to catch all exception and wrap to V3 format
		environment.jersey().register(new RuntimeExceptionMapper());
	}

	private void configureFilters(T configuration, Environment environment) {
		if(configuration.getFilters()==null)
			return;
		for (String filter : configuration.getFilters()) {
			if(filter==null || filter.trim().length()==0)
				continue;
			configureFilter(environment, filter);
		}
	}

	private void configureFilter(Environment environment, String filter) {
		@SuppressWarnings("rawtypes")
		Class filterClass = null;
		try {
			filterClass = Class.forName(filter);
		} catch (Exception e) {
			logger.error(String.format("Filter '%s' is invalid class name.", filter), e);
		}
		if(filterClass==null)
			return;
		
		if(ContainerRequestFilter.class.isAssignableFrom(filterClass)) {
			logger.info(String.format("Registering Request filter: '%s'", filter));
			environment.jersey().property(ResourceConfig.PROPERTY_CONTAINER_REQUEST_FILTERS, filter);
		}
		if(ContainerResponseFilter.class.isAssignableFrom(filterClass)) {
			logger.info(String.format("Registering Response filter: '%s'", filter));
			environment.jersey().property(ResourceConfig.PROPERTY_CONTAINER_RESPONSE_FILTERS, filter);
		}
	}

	/**
	 * @param configuration
	 * @param environment
	 */
	protected void configureCors(APIBaseConfiguration configuration, Environment environment) {
		// http://jitterted.com/tidbits/2014/09/12/cors-for-dropwizard-0-7-x/
		Map<String, String> corsSettings = configuration.getCorsSettings();
		if(corsSettings != null) {
			Dynamic filter = environment.servlets().addFilter("CORS", CrossOriginFilter.class);
			filter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
			/* Supposed parameters:
			 * allowedMethods
			 * allowedOrigins
			 * allowCredentials
			 * allowCredentials
			 */
			for(Iterator<String> keys = corsSettings.keySet().iterator(); keys.hasNext();) {
				String key = keys.next();
				String value = corsSettings.get(key);
				filter.setInitParameter(key, value);
				logger.info("Cross-origin filter configuration["+key+"]: "+value);
			}
		}
	}
	
	protected List<Object> getAllResources() throws Exception {
		Reflections reflections = new Reflections("com.appirio.tech");
		Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(Path.class);
		
		List<Object> resourceList = new ArrayList<Object>();
		for(Class<?> clazz : annotated) {
			Object resource = createResource(clazz);
			if(resource!=null)
				resourceList.add(resource);
		}
		return resourceList;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected Object createResource(Class clazz) throws Exception {
		try {
			return clazz.getConstructor().newInstance();
		} catch (NoSuchMethodException e) {
			logger.info("Resource class: " + clazz.getName() + " does not have default constructor. Skipped.");
			return null;
		} catch (Exception e) {
			throw new ResourceInitializationException("Failed to instantiate "+clazz.getName()+". "+e.getMessage(), e);
		}
	}
	
	public static void main(String[] args) throws Exception {
		new APIApplication<>().run(args);
	}
}
