/**
 * 
 */
package com.appirio.tech.core.api.v3.dropwizard;

import io.dropwizard.Application;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
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
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseFilter;

import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.appirio.tech.core.api.v3.exception.ResourceInitializationException;
import com.appirio.tech.core.api.v3.model.annotation.ApiJacksonAnnotationIntrospector;
import com.appirio.tech.core.api.v3.request.inject.FieldSelectorProvider;
import com.appirio.tech.core.api.v3.request.inject.QueryParameterProvider;
import com.appirio.tech.core.api.v3.response.ApiResponseFilter;
import com.appirio.tech.core.auth.AllowAnonymousFeature;
import com.appirio.tech.core.auth.AuthUser;
import com.appirio.tech.core.auth.JWTAuthProvider;
import com.appirio.tech.core.auth.JWTAuthenticator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;



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
		JACKSON_OBJECT_MAPPER.setFilterProvider(filters);
	}
	
	@Override
	public void run(T configuration, Environment environment) throws Exception {
		// apply system properties defined in configuration
		applySystemProperties(configuration, environment);
		// delegate.run(this, configuration, environment);
		configureCors(configuration, environment);
		// Register filters
		configureFilters(configuration, environment);

		//Find all Resource class and register them to jersey if auto registering is set true.
		if(configuration.isUseResourceAutoRegistering()) {
			for(Object resource : getAllResources()) {
				logger.debug("Registering Resource to Jersey:" + resource.toString());
				environment.jersey().register(resource);
			}
		}
		
		//Register V3 API query/put/post/delete parameter objects to map into annotated instances
		environment.jersey().register(new FieldSelectorProvider.Binder());
		environment.jersey().register(new QueryParameterProvider.Binder());
		
		//Register V3 API response filter for GET call (handling partial response and includes param)
		configureApiResponseFilter(configuration, environment);
		
		//Register ExceptionMapper to catch all exception and wrap to V3 format
		configureRuntimeExceptionMapper(configuration, environment);
		
		//Register Auth components
		configureAuth(configuration, environment);
	}

	public static final String PROP_KEY_JWT_SECRET = "TC_JWT_KEY";

	/**
	 * secret 
	 */
	protected String getSecret() {
		String key = System.getenv(PROP_KEY_JWT_SECRET);
		if(key!=null)
			return key;
		key = System.getProperty(PROP_KEY_JWT_SECRET);
		if(key==null)
			logger.warn(PROP_KEY_JWT_SECRET + " is not found in both of environment variables and system properties.");
		return key;
	}


	protected void configureFilters(T configuration, Environment environment) {
		if(configuration.getFilters()==null)
			return;
		for (String filter : configuration.getFilters()) {
			if(filter==null || filter.trim().length()==0)
				continue;
			if(ApiResponseFilter.class.getName().equals(filter)) {
				configureApiResponseFilter(configuration, environment);
				continue;
			}
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
		
		if(ContainerRequestFilter.class.isAssignableFrom(filterClass) ||
				ContainerResponseFilter.class.isAssignableFrom(filterClass)) {
			logger.info(String.format("Registering Filter: '%s'", filter));
			environment.jersey().register(filter);
		}
	}
	
	protected void configureApiResponseFilter(T configuration, Environment environment) {
		logger.info(String.format("Configuration#useResponseFilter: %s", configuration.getUseResponseFilter()));
		if(configuration.getUseResponseFilter()) {
			logger.info(String.format("Registering Filter: '%s'", ApiResponseFilter.class.getName()));
			environment.jersey().register(new ApiResponseFilter(JACKSON_OBJECT_MAPPER));
		}
	}
	
	protected void configureRuntimeExceptionMapper(T configuration, Environment environment) {
		environment.jersey().register(new RuntimeExceptionMapper());
	}
	
	protected void configureAuth(T configuration, Environment environment) {
		JWTAuthenticator authenticator = new JWTAuthenticator(configuration.getAuthDomain(), getSecret());
		environment.jersey().register(new AuthDynamicFeature(
		        new JWTAuthProvider.Builder<AuthUser>()
		        	.setAuthenticator(authenticator)
		            .buildAuthFilter()));
		
		environment.jersey().register(new AllowAnonymousFeature(
		        new JWTAuthProvider.Builder<AuthUser>()
		        	.setRequired(false)
		        	.setAuthenticator(authenticator)
		            .buildAuthFilter()));
		
		environment.jersey().register(RolesAllowedDynamicFeature.class);
		
	    // Binder to inject a custom Principal object into parameters annotated with @Auth
	    environment.jersey().register(new AuthValueFactoryProvider.Binder<>(AuthUser.class));
	}

	/**
	 * @param configuration
	 * @param environment
	 */
	protected void applySystemProperties(APIBaseConfiguration configuration, Environment environment) {
		// 
		Map<String, String> systemProperties = configuration.getSystemProperties();
		if(systemProperties==null || systemProperties.isEmpty())
			return;
		for(Iterator<String> keys = systemProperties.keySet().iterator(); keys.hasNext();) {
			String key = keys.next();
			String value = systemProperties.get(key);
			if(System.getProperty(key)!=null) {
				logger.info("System propery["+key+"] = "+System.getProperty(key));
				continue;
			}
			System.setProperty(key, value);
			logger.info("System propery["+key+"] -> "+value);
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
