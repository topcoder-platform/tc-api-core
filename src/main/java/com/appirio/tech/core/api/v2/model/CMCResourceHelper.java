/**
 * 
 */
package com.appirio.tech.core.api.v2.model;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy;

import com.appirio.tech.core.api.v2.exception.CMCParseException;
import com.appirio.tech.core.api.v2.model.annotation.ApiMapping;
import com.appirio.tech.core.api.v2.request.FieldSelector;

/**
 * @author sudo
 *
 */
public class CMCResourceHelper {

	private static final LowerCaseWithUnderscoresStrategy namingStrategy = new LowerCaseWithUnderscoresStrategy();

	/**
	 * Translates getter/setter method names to underscore field name
	 * which will be used as API field label
	 * 
	 * @param methodName
	 * @return
	 * @throws CMCParseException when the specified method is not getter/setter/is-getter
	 * @since va1
	 */
	@JsonIgnore
	protected static String getUnderscoreFieldName(String methodName) {
		if(methodName.startsWith("get") || 
				methodName.startsWith("set")) {
			return namingStrategy.translate(methodName.substring(3));
		} else if (methodName.startsWith("is")) {
			return namingStrategy.translate(methodName.substring(2));
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
	public static FieldSelector getDefaultFieldSelector(Class<? extends CMCResource> clazz) {
		FieldSelector selector = new FieldSelector();
		try {
			Set<String> fields = CMCResourceHelper.getDefaultFields(clazz);
			for(String field : fields) {
				selector.addField(field);
			}
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
		return selector;
	}
	
	public static Set<String> getDefaultFields(Class<? extends CMCResource> clazz) {
		Set<String> ret = new HashSet<String>();
		
		Method[] methods = clazz.getMethods();
		for(Method method : methods) {
			String methodName = method.getName();
			if(methodName.startsWith("get") && method.getParameterTypes().length==0) {
				ApiMapping api = method.getAnnotation(ApiMapping.class);
				if(api!=null && api.visible() && api.queryDefault()) {
					ret.add(getUnderscoreFieldName(method.getName()));
					if(api.alias()!=null) {
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
	 * NOTE: since CMCResource wants to be POJO, it should be better that "serializable"
	 * fields should only be taken care when actual serialization is going to happen,
	 * and {@link CMCResource} doesn't know anything about which fields that gets serialized.
	 * Unfortunately, as Jackson filter function (JSON serializer) only takes Object to it's
	 * method, we made the method here for it to use. See {@link ApiBeanSerializeFilter} for
	 * the implementation and usage of {@link #serializeFields}
	 *  
	 * @param selector
	 * @since va1
	 */
	public static void setSerializeFields(CMCResource cobj, FieldSelector selector) {
		Set<String> serializeFields = new HashSet<String>();
		serializeFields = selector.getSelectedFields();
		if(serializeFields.isEmpty()) {
			serializeFields = CMCResourceHelper.getDefaultFields(cobj.getClass());
		}
		Method[] methods = cobj.getClass().getMethods();
		
		//Get all getters of this class that might have child CMCResources
		Map<String, Method> methodMap = new HashMap<String, Method>(); //key: underscore field name, value: getter method mapped to it
		for(Method method : methods) {
			String methodName = method.getName();
			if(methodName.startsWith("get") && method.getParameterTypes().length==0) {
				if(CMCResource.class.isAssignableFrom(method.getReturnType()) ||
						isReturnNotNullOrTypeCollectionOfCMCResource(cobj, method)) {
					
					String underscoreLabel = getUnderscoreFieldName(methodName);
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
					if(CMCResource.class.isAssignableFrom(returnType)) {
						CMCResource co = (CMCResource)method.invoke(cobj);
						if(co!=null) setSerializeFields(co, selector.getField(fieldName));
					} else if (Collection.class.isAssignableFrom(returnType)) {
						Collection<?> collection = (Collection<?>)method.invoke(cobj);
						for(Object item : collection) {
							if(item instanceof CMCResource) {
								CMCResource co = (CMCResource)item;
								setSerializeFields(co, selector.getField(fieldName));
							}
						}
					}
				}
			} catch (Exception e) {
				throw new CMCParseException("Was unable to locate getter method for the field:" + fieldName, e);
			}
		}
		
		cobj.setSerializeFields(serializeFields);
	}

	/**
	 * Asserts that given method returns null or is type Collection<CMCResource>
	 * 
	 * @param method
	 * @return
	 */
	private static boolean isReturnNotNullOrTypeCollectionOfCMCResource(CMCResource co, Method method) {
		if (Collection.class.isAssignableFrom(method.getReturnType())) {
			Object obj;
			try {
				obj = method.invoke(co);
			} catch (Exception e) {
				return false;
			}
			if (obj != null) {
				Collection<?> col = (Collection<?>) obj;
				if (col.size() > 0 && col.iterator().next() instanceof CMCResource) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * String#substring which will cut of tail chars safely
	 * 
	 * @param value
	 * @param length
	 * @return
	 */
	public static String chop(String value, int length) {
		if(value==null) return null;
		return value.substring(0, Math.min(value.length(), length));
	}
}
