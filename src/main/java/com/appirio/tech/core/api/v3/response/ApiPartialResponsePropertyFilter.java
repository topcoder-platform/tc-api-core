/**
 * 
 */
package com.appirio.tech.core.api.v3.response;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.ws.rs.container.ContainerRequestContext;

import org.apache.commons.lang3.StringUtils;

import com.appirio.tech.core.api.v3.model.ResourceHelper;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ser.PropertyFilter;
import com.fasterxml.jackson.databind.ser.PropertyWriter;

/**
 * Filter out properties in response for V3 API format.
 * 
 * @author sudo
 * 
 */
public class ApiPartialResponsePropertyFilter implements PropertyFilter {

	private static final String DELIMS = ",()";
	private Map<String, Set<String>> map;

	public ApiPartialResponsePropertyFilter(ApiResponse apiResponse, ContainerRequestContext request) {
		
		//Find fields to return
		String queryParam = request.getUriInfo().getQueryParameters().getFirst("fields");
		
		Object content = apiResponse.getResult()!=null ? apiResponse.getResult().getContent() : null;
		if (queryParam == null || queryParam.trim().isEmpty()) {
			if(content != null) {
				if(content instanceof Collection){
					Iterator it = ((Collection)content).iterator();
					if(it.hasNext()) {
						queryParam = StringUtils.join(ResourceHelper.getDefaultFields(it.next().getClass()), ",");
					}
				} else {
					queryParam = StringUtils.join(ResourceHelper.getDefaultFields(content.getClass()), ",");
				}
			}
		}
		
		if(content==null)
			return;
		String entityName;
		if(content instanceof List) {
			if(((List) content).isEmpty()) return;
			entityName = ((List)content).get(0).getClass().getSimpleName();
		} else {
			entityName = content.getClass().getSimpleName();
		}
		map = parse(entityName.toLowerCase(), queryParam.toLowerCase());
	}

	@Override
	public void serializeAsField(Object pojo, JsonGenerator jgen,
			SerializerProvider prov, PropertyWriter writer) throws Exception {
		String beanName = pojo.getClass().getSimpleName();
		String propertyName = writer.getName();
		if (include(beanName, propertyName)) {
			writer.serializeAsField(pojo, jgen, prov);
		}
	}

	@Override
	public void serializeAsElement(Object elementValue, JsonGenerator jgen,
			SerializerProvider prov, PropertyWriter writer) throws Exception {
		String beanName = elementValue.getClass().getSimpleName();
		String propertyName = writer.getName();
		if (include(beanName, propertyName)) {
			writer.serializeAsElement(elementValue, jgen, prov);
		}
	}

	@Override
	public void depositSchemaProperty(PropertyWriter writer,
			ObjectNode propertiesNode, SerializerProvider provider)
			throws JsonMappingException {
		writer.depositSchemaProperty(propertiesNode, provider);
	}

	@Override
	public void depositSchemaProperty(PropertyWriter writer,
			JsonObjectFormatVisitor objectVisitor, SerializerProvider provider)
			throws JsonMappingException {
		writer.depositSchemaProperty(objectVisitor, provider);
	}

	protected boolean include(String beanName, String propertyName) {
		beanName = beanName.toLowerCase();
		propertyName = propertyName.toLowerCase();
		if (map.containsKey(beanName)) {
			return map.get(beanName).contains(propertyName);
		}
		return false;
	}

	private Map<String, Set<String>> parse(String baseName, String queryString) {
		StringTokenizer tokenizer = new StringTokenizer(queryString, DELIMS, true);
		Map<String, Set<String>> newMap = new LinkedHashMap<>();
		recursiveParse(tokenizer, newMap, baseName);
		return newMap;
	}

	private Set<String> recursiveParse(StringTokenizer tokenizer,
			Map<String, Set<String>> map, String key) {
		Set<String> set = new LinkedHashSet<>();
		map.put(key, set);
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			if (token.equals("(")) {
				recursiveParse(tokenizer, map, key);
			} else if (token.equals(")")) {
				return set;
			}
			if (!DELIMS.contains(token)) {
				set.add(token);
				key = token;
			}
		}
		return set;
	}

}
