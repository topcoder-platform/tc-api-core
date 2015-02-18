/**
 * 
 */
package com.appirio.tech.core.api.v3.controller;

import java.util.Map;
import java.util.Set;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.ser.BeanPropertyFilter;
import org.codehaus.jackson.map.ser.BeanPropertyWriter;

import com.appirio.tech.core.api.v3.model.AbstractIdResource;
import com.appirio.tech.core.api.v3.model.AbstractResource;

/**
 * See {@link AbstractIdResource#setSerializeFields(com.appirio.cmc.api.selector.FieldSelector)} for details
 * of this class
 * 
 * @author sudo
 *
 */
public class ApiBeanSerializeFilter implements BeanPropertyFilter {

	private Map<Integer, Set<String>> objectFieldSerializeMap;
	
	public ApiBeanSerializeFilter() {
		super();
	}

	public Map<Integer, Set<String>> getObjectFieldSerializeMap() {
		return objectFieldSerializeMap;
	}

	public void setObjectFieldSerializeMap(Map<Integer, Set<String>> objectFieldSerializeMap) {
		this.objectFieldSerializeMap = objectFieldSerializeMap;
	}

	public void serializeAsField(Object bean, JsonGenerator jgen, SerializerProvider prov, BeanPropertyWriter writer)
			throws Exception {
		if(bean instanceof AbstractResource) {
			Set<String> serializabileFields = objectFieldSerializeMap.get(System.identityHashCode(bean));
			String name = writer.getName();
			if(serializabileFields == null) {
				//just write id if no field is specified for resource that has id.
				if(name.equalsIgnoreCase("id") && (bean instanceof AbstractIdResource)) {
					writer.serializeAsField(bean, jgen, prov);
				}
			} else {
				if(serializabileFields.contains(name)) {
					writer.serializeAsField(bean, jgen, prov);
				}
			}
		} else {
			writer.serializeAsField(bean, jgen, prov);
		}
	}
}
