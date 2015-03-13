/**
 * 
 */
package com.appirio.tech.core.api.v3.resource.old;




/**
 * Interface to define classes that handles API requests.
 * Base class to represent TC Domain object.
 * 
 * The class is annotated with ApiResonseFilter which will be used to create Response for
 * api using {@link ApiBeanSerializeFilter} inside {@link ApiHttpMessageConverter}
 * 
 * @author sudo
 *
 */
//@JsonFilter("ApiResponseFilter")
public interface RESTResource {
}
