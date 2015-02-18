/**
 * 
 */
package com.appirio.tech.core.api.v3.metadata;

import java.util.Set;

/**
 * Base class for metadata.
 * Extend this class for resource specific metadata information.
 * 
 * @author sudo
 *
 */
public class Metadata {
	Set<FieldInfo> fields;
	
	public Set<FieldInfo> getFields() {
		return fields;
	}
	public void setFields(Set<FieldInfo> fieldInfoSet) {
		this.fields = fieldInfoSet;
	}
}
