/**
 * 
 */
package com.appirio.tech.core.api.v2.model.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.appirio.tech.core.api.v2.request.FieldSelector;

/**
 * Annotation to map api fields to Domain Object getter fields.
 * If the method is NOT annotated with this, we'll use default conversion (getter method to underscore label name)
 * to convert api label to the getter methods
 * 
 * @author sudo
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ApiMapping {
	/**
	 * If api has any alias other than pure conversion of getter method, specify using this method.
	 * 
	 * @return
	 */
	String[] alias() default {};
	
	/**
	 * Set true if we want to return the fields although API didn't specify in {@link FieldSelector}
	 * @return
	 */
	boolean queryDefault() default false;
	
	/**
	 * Set false if we want to hide the method from api
	 * @return
	 */
	boolean visible() default true;
}
