/**
 * 
 */
package com.appirio.tech.core.api.v3.model;

import java.beans.Introspector;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.appirio.tech.core.api.v3.controller.ApiBeanSerializeFilter;
import com.appirio.tech.core.api.v3.exception.CMCParseException;
import com.appirio.tech.core.api.v3.model.annotation.ApiMapping;
import com.appirio.tech.core.api.v3.request.FieldSelector;

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
	 * @throws CMCParseException when the specified method is not getter/setter/is-getter
	 * @since va1
	 */
	public static String getApiFieldName(String methodName) {
		if(methodName.startsWith("get") || 
				methodName.startsWith("set")) {
			return Introspector.decapitalize(methodName.substring(3));
		} else if (methodName.startsWith("is")) {
			return Introspector.decapitalize(methodName.substring(2));
		} else {
			throw new CMCParseException("Unable to transform method to underscore field name");
		}
	}

	/**
	 * Returns fields to return as default for api.
	 * 
	 * @param clazz
	 * @since v2
	 */
	public static FieldSelector getDefaultFieldSelector(Class<? extends AbstractResource> clazz) {
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
	
	public static Set<String> getDefaultFields(Class<? extends AbstractResource> clazz) {
		Set<String> ret = new HashSet<String>();
		
		Method[] methods = clazz.getMethods();
		for(Method method : methods) {
			String methodName = method.getName();
			if(methodName.startsWith("get") && method.getParameterTypes().length==0) {
				ApiMapping api = method.getAnnotation(ApiMapping.class);
				if(api==null || (api.visible() && api.queryDefault())) {
					ret.add(getApiFieldName(method.getName()));
					if(api!=null && api.alias()!=null) {
						for(String alias : api.alias()) {
							ret.add(alias);
						}
					}
				}
			}
		}
		
		return ret;
	}
	
	/**
	 * Sets collection of fields that will get Serialized and return to the client
	 * upon REST api call.
	 * The method will call children's {@link #setSerializeFields(FieldSelector)} if
	 * this instance has any from the specified {@link FieldSelector}
	 * 
	 * See {@link ApiBeanSerializeFilter} for the implementation and usage of {@link #serializeFields}
	 *  
	 * @param selector
	 * @since va1
	 */
	public static void setSerializeFields(AbstractResource cobj, FieldSelector selector, Map<Integer, Set<String>> objectFieldMap) {
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
				if(AbstractResource.class.isAssignableFrom(method.getReturnType()) ||
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
					if(AbstractResource.class.isAssignableFrom(returnType)) {
						AbstractResource co = (AbstractResource)method.invoke(cobj);
						if(co!=null) setSerializeFields(co, selector.getField(fieldName), objectFieldMap);
					} else if (Collection.class.isAssignableFrom(returnType)) {
						Collection<?> collection = (Collection<?>)method.invoke(cobj);
						for(Object item : collection) {
							if(item instanceof AbstractResource) {
								AbstractResource co = (AbstractResource)item;
								setSerializeFields(co, selector.getField(fieldName), objectFieldMap);
							}
						}
					}
				}
			} catch (Exception e) {
				throw new CMCParseException("Was unable to locate getter method for the field:" + fieldName, e);
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
	private static boolean isReturnNotNullOrTypeCollectionOfCMCResource(AbstractResource co, Method method) {
		if (Collection.class.isAssignableFrom(method.getReturnType())) {
			Object obj;
			try {
				obj = method.invoke(co);
			} catch (Exception e) {
				return false;
			}
			if (obj != null) {
				Collection<?> col = (Collection<?>) obj;
				if (col.size() > 0 && col.iterator().next() instanceof AbstractResource) {
					return true;
				}
			}
		}
		return false;
	}

}
