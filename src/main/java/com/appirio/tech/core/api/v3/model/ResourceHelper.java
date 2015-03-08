/**
 * 
 */
package com.appirio.tech.core.api.v3.model;

import java.beans.Introspector;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.appirio.tech.core.api.v3.dropwizard.APIApplication;
import com.appirio.tech.core.api.v3.exception.APIParseException;
import com.appirio.tech.core.api.v3.exception.APIRuntimeException;
import com.appirio.tech.core.api.v3.model.annotation.ApiMapping;
import com.appirio.tech.core.api.v3.request.FieldSelector;
import com.appirio.tech.core.api.v3.resource.old.RESTResource;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author sudo
 *
 */
public class ResourceHelper {

	/**
	 * Translates getter/setter method names to API field name
	 * which will be used as API field label
	 * 
	 * @param methodName
	 * @return
	 * @throws APIParseException when the specified method is not getter/setter/is-getter
	 * @since va1
	 */
	public static String getApiFieldName(String methodName) {
		if(methodName.startsWith("get") || 
				methodName.startsWith("set")) {
			return Introspector.decapitalize(methodName.substring(3));
		} else if (methodName.startsWith("is")) {
			return Introspector.decapitalize(methodName.substring(2));
		} else {
			throw new APIParseException("Unable to transform method to underscore field name");
		}
	}

	/**
	 * Returns fields to return as default for api.
	 * 
	 * @param clazz
	 * @since v2
	 */
	public static FieldSelector getDefaultFieldSelector(Class<?> clazz) {
		FieldSelector selector = new FieldSelector();
		try {
			Set<String> fields = ResourceHelper.getDefaultFields(clazz);
			for(String field : fields) {
				selector.addField(field);
			}
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
		return selector;
	}
	
	/**
	 * Return name of fields of Representation class that should be default
	 * present as V3 FilterParameter if filter parameter is not defined.
	 * 
	 * Default fields are those that (1)jackson WILL serialize and (2) are NOT
	 * annotated with {@link ApiMapping} with visible=false or queryDefault=false
	 * 
	 * NOTE: This method uses {@link ObjectMapper} that is used by dropwizard
	 * which gets populated during initialization process.
	 * 
	 * @param representationClass
	 * @return
	 */
	public static Set<String> getDefaultFields(Class<?> representationClass) {
		//Get set of fields that jackson will serialize
		Set<String> jacksonSet = new HashSet<String>();
		Object testObject;
		try {
			testObject = representationClass.getConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			throw new APIRuntimeException("failed to instanciate representation pojo class", e);
		}
		JsonNode node = APIApplication.JACKSON_OBJECT_MAPPER.valueToTree(testObject);
		Iterator<String> fieldNames = node.fieldNames();
		while(fieldNames.hasNext()) {
			jacksonSet.add(fieldNames.next());
		}
		
		//Get set of fields that @ApiMapping will ignore
		Set<String> apiMappingIgnore = new HashSet<String>();
		Method[] methods = representationClass.getMethods();
		for(Method method : methods) {
			String methodName = method.getName();
			if(methodName.startsWith("get") && method.getParameterTypes().length==0) {
				ApiMapping api = method.getAnnotation(ApiMapping.class);
				if(api!=null && !(api.visible() && api.queryDefault())) {
					apiMappingIgnore.add(getApiFieldName(method.getName()));
				}
			}
		}
		
		//remove @ApiMapping ignores from jackson Set
		Iterator<String> apiMapIterator = apiMappingIgnore.iterator();
		while (apiMapIterator.hasNext()) {
			String type = apiMapIterator.next();
			jacksonSet.remove(type);
		}
		
		return jacksonSet;
	}
	
	/**
	 * Sets collection of fields that will get Serialized and return to the client
	 * upon REST api call.
	 * The method will call children's {@link #setSerializeFields(APIFieldParam)} if
	 * this instance has any from the specified {@link APIFieldParam}
	 * 
	 * See {@link ApiBeanSerializeFilter} for the implementation and usage of {@link #serializeFields}
	 *  
	 * @param selector
	 * @since va1
	 */
	public static void setSerializeFields(RESTResource cobj, FieldSelector selector, Map<Integer, Set<String>> objectFieldMap) {
		if(selector==null) return;
		Set<String> serializeFields = new HashSet<String>();
		serializeFields = selector.getSelectedFields();
		if(serializeFields.isEmpty()) {
			serializeFields = ResourceHelper.getDefaultFields(cobj.getClass());
		}
		Method[] methods = cobj.getClass().getMethods();
		
		//Get all getters of this class that might have child CMCResources
		Map<String, Method> methodMap = new HashMap<String, Method>(); //key: underscore field name, value: getter method mapped to it
		for(Method method : methods) {
			String methodName = method.getName();
			if(methodName.startsWith("get") && method.getParameterTypes().length==0) {
				if(RESTResource.class.isAssignableFrom(method.getReturnType()) ||
						isReturnNotNullOrTypeCollectionOfCMCResource(cobj, method)) {
					
					String underscoreLabel = getApiFieldName(methodName);
					if(serializeFields.contains(underscoreLabel)) {
						methodMap.put(underscoreLabel, method);
					}
				}
			}
		}
		
		//Call getter methods to invoke child#setSeriaizeInfo()
		for(String fieldName : serializeFields) {
			try {
				Method method = methodMap.get(fieldName);
				if(method!=null) {
					Class<?> returnType = method.getReturnType();
					if(RESTResource.class.isAssignableFrom(returnType)) {
						RESTResource co = (RESTResource)method.invoke(cobj);
						if(co!=null) setSerializeFields(co, selector.getField(fieldName), objectFieldMap);
					} else if (Collection.class.isAssignableFrom(returnType)) {
						Collection<?> collection = (Collection<?>)method.invoke(cobj);
						for(Object item : collection) {
							if(item instanceof RESTResource) {
								RESTResource co = (RESTResource)item;
								setSerializeFields(co, selector.getField(fieldName), objectFieldMap);
							}
						}
					}
				}
			} catch (Exception e) {
				throw new APIParseException("Was unable to locate getter method for the field:" + fieldName, e);
			}
		}
		
		objectFieldMap.put(System.identityHashCode(cobj), serializeFields);
	}

	/**
	 * Asserts that given method returns null or is type Collection<CMCResource>
	 * 
	 * @param method
	 * @return
	 */
	private static boolean isReturnNotNullOrTypeCollectionOfCMCResource(RESTResource co, Method method) {
		if (Collection.class.isAssignableFrom(method.getReturnType())) {
			Object obj;
			try {
				obj = method.invoke(co);
			} catch (Exception e) {
				return false;
			}
			if (obj != null) {
				Collection<?> col = (Collection<?>) obj;
				if (col.size() > 0 && col.iterator().next() instanceof RESTResource) {
					return true;
				}
			}
		}
		return false;
	}

}
