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
import com.appirio.tech.core.api.v3.request.inject.FieldSelectorProvider;
import com.appirio.tech.core.api.v3.request.inject.QueryParameterProvider;
import com.appirio.tech.core.auth.JWTAuthProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

/**
 * Application entry point for DropWizard framework.
 * This class is the standard class to run API V3 based RESTful apps. When running app from this class,
 * API V3 library will load model/service/exception that has extended/inherited defined classes/interfaces
 * and will communicate via V3 protocol automatically.
 * In case, all request will go through {@link APIController}
 * 
 * Application has freedom to create their own main() to run DropWizard apps, in which case, individual app
 * need to implement V3 protocol.
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
	}
	
	@Override
	public void run(APIBaseConfiguration configuration, Environment environment) throws Exception {
		//delegate.run(this, configuration, environment);
		configureCors(configuration, environment);
		
		environment.jersey().setUrlPattern("/v3/*");
		
		//Find all Resource class and register them to jersey.
		if(configuration.isUseResourceAutoRegistering()) {
			for(Object resource : getAllResources()) {
				logger.debug("Registering Resource to Jersey:" + resource.toString());
				environment.jersey().register(resource);
			}
		}
		//Register V3 API query/put/post/delete parameter objects to map into annotated instances
		environment.jersey().register(new FieldSelectorProvider());
		environment.jersey().register(new QueryParameterProvider());
		//Register Authentication Provider to validate JWT with @Auth annotation
		environment.jersey().register(new JWTAuthProvider());
		//Register ExceptionMapper to catch all exception and wrap to V3 format
		environment.jersey().register(new RuntimeExceptionMapper());
	}

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
