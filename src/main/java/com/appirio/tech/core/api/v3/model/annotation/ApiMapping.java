/**
 * 
 */
package com.appirio.tech.core.api.v3.model.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.appirio.tech.core.api.v3.request.annotation.APIFieldParam;

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
	 * TODO: We do use this now.
	 * @return
	 */
	//String[] alias() default {};
	
	/**
	 * Set false if we do not want to return the fields when API didn't specify in {@link APIFieldParam}
	 * @return
	 */
	boolean queryDefault() default true;
	
	/**
	 * Set false if we want to hide the method from api.
	 * Has same effect as Jackson's @JsonIgnore annotation.
	 * @return
	 */
	boolean visible() default true;
}
