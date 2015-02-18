package com.appirio.tech.core.api.v3.util;

import org.apache.commons.lang3.StringUtils;

/**
 * various utility methods for api conversion.
 * @author satake
 *
 */
public class ApiUtil {
	private ApiUtil(){}

	/**
	 * convert method name to field name of API-va1.
	 * ex) getFooBar -> foo_bar, getFoo -> foo, isFooBar -> foo_bar
	 * @param name
	 * @return converted name. if methodName start with neither "get" nor "is", returns null.
	 */
	public static String convertMethodNameToApiField(String methodName){
		if(StringUtils.isEmpty(methodName) || (!methodName.startsWith("get") && !methodName.startsWith("is")))return null;
		return convertToApiField(StringUtils.substringAfter(methodName, methodName.startsWith("get") ? "get" : "is"));
	}

	/**
	 * convert name to field name of API-va1.
	 * ex) FooBar -> foo_bar, Foo -> foo
	 * @param name
	 * @return converted name
	 */
	public static String convertToApiField(String name){
		return name.replaceAll("(\\p{Upper})", "_$1").replaceFirst("_", "").toLowerCase();
	}

	/**
	 * convert method name to field name of API-v2.
	 * ex) getFooBar -> FooBar, getFoo -> foo, isFooBar -> fooBar
	 * @param name
	 * @return converted name. if methodName start with neither "get" nor "is", returns null.
	 */
	public static String convertMethodNameToV2ApiField(String methodName){
		if(StringUtils.isEmpty(methodName) || (!methodName.startsWith("get") && !methodName.startsWith("is")))return null;
		String str = StringUtils.substringAfter(methodName, methodName.startsWith("get") ? "get" : "is");
		str = Character.toLowerCase(str.charAt(0)) + (str.length() > 1 ? str.substring(1) : "");
		return str;
	}

	/**
	 * escapes the characters in a string using SOQL (LIKE)
	 * ex) Foo's Bar_50% -> Foo\'s Bar\_50\%
	 *
	 * @param likeStr
	 * @return a new escaped String
	 */
	public static String escapeSoqlForLike(String likeStr){
		StringBuilder sb = new StringBuilder(likeStr.length() * 2);
		for (int i=0; i < likeStr.length(); i++) {
			char c = likeStr.charAt(i);
			switch (c) {
				case '\'':
					sb.append("\\'");
					break;
				case '_':
					sb.append("\\_");
					break;
				case '%':
					sb.append("\\%");
					break;
				default:
					sb.append(c);
			}
		}
		return sb.toString();
	}
}
