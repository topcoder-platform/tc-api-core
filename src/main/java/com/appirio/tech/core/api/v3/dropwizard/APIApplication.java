/**
 * 
 */
package com.appirio.tech.core.api.v3.dropwizard;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.ws.rs.Path;

import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	protected List<Object> getAllResources() throws Exception {
		Reflections reflections = new Reflections("com.appirio.tech");
		Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(Path.class);
		
		List<Object> resourceList = new ArrayList<Object>();
		for(Class<?> clazz : annotated) {
			resourceList.add(clazz.getConstructor().newInstance());
		}
		return resourceList;
	}
	
	@Override
	public void run(APIBaseConfiguration configuration, Environment environment) throws Exception {
		environment.jersey().setUrlPattern("/v3/*");
		
		//Find all Resource class and register them to jersey.
		for(Object resource : getAllResources()) {
			logger.debug("Registering Resource to Jersey:" + resource.toString());
			environment.jersey().register(resource);
		}
		
		//Register V3 API query/put/post/delete parameter objects to map into annotated instances
		environment.jersey().register(new FieldSelectorProvider());
		environment.jersey().register(new QueryParameterProvider());
		//Register Authentication Provider to validate JWT with @Auth annotation
		environment.jersey().register(new JWTAuthProvider());
		//Register ExceptionMapper to catch all exception and wrap to V3 format
		environment.jersey().register(new RuntimeExceptionMapper());
	}

	public static void main(String[] args) throws Exception {
		new APIApplication<>().run(args);
	}
}
