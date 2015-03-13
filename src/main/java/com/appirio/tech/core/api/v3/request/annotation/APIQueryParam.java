/**
 * 
 */
package com.appirio.tech.core.api.v3.request.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.appirio.tech.core.api.v3.request.QueryParameter;

/**
 * Parameter annotation to convert API V3 query parameters into {@link QueryParameter} object
 * 
 * @author sudo
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PARAMETER, ElementType.FIELD })
public @interface APIQueryParam {
    /**
     * If {@code true}, the request will not be processed in the absence of a valid FieldSelector. If
     * {@code false}, {@code null} will be passed in. Defaults to {@code false}.
     */
    boolean required() default false;
    
    /**
     * A Representation Class of an Resource that field selector is going to parse
     * 
     * @return
     */
    Class<?> repClass();
}
