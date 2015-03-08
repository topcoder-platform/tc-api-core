/**
 * 
 */
package com.appirio.tech.core.api.v3.request;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author sudo
 *
 */
public class FieldSelector {
	
	public static final String INITIAL_NAME = "root";
	private String rootName;
	private Map<String, FieldSelector> children = new HashMap<String, FieldSelector>();

	public static FieldSelector instanceFromVaString(String vaSelectorString) {
		FieldSelector selector = new FieldSelector();
		selector.fromVaString(vaSelectorString);
		return selector;
	}
	public static FieldSelector instanceFromV2String(String v2SelectorString) {
		FieldSelector selector = new FieldSelector();
		selector.fromV2String(v2SelectorString);
		return selector;
	}
	
	public FieldSelector() {
		this.rootName = INITIAL_NAME;
	}
	
	protected void fromVaString(String selectorString) {
		rootName = getRootField(selectorString);
		if(!selectorString.equals(rootName)) {
			/**
			 * parse children
			 * string comes in like : root-node:(child-field-1, child-field-2:(grand-field-1), child-field-3)
			 */
			Set<String> childFields = getChildrenFields(selectorString.substring(rootName.length() + 1));
			for(String child : childFields) {
				children.put(getRootField(child), instanceFromVaString(child));
			}
		}
	}

	protected void fromV2String(String selectorString) {
		rootName = INITIAL_NAME;
		if(selectorString!=null) {
			/**
			 * parse children
			 * string comes in like : child-field-1, child-field-2:(grand-field-1), child-field-3
			 */
			Set<String> childFields = getChildrenFields("(" + selectorString + ")");
			for(String child : childFields) {
				children.put(getRootField(child), instanceFromVaString(child));
			}
		}
	}

	public String getRootName() {
		return rootName;
	}
	public void setRootName(String rootName) {
		this.rootName = rootName;
	}
	/**
	 * returns true if this {@link APIFieldParam} has at least one child field
	 * @return
	 */
	public boolean hasChild() {
		return children!=null && !children.isEmpty();
	}
	/**
	 * returns true if this {@link APIFieldParam} has child field of given name
	 * 
	 * @param childFieldName
	 * @return
	 */
	public boolean hasField(String childFieldName) {
		return children.containsKey(childFieldName);
	}
	public Set<String> getSelectedFields() {
		return children.keySet();
	}
	public FieldSelector getField(String childFieldName) {
		return children.get(childFieldName);
	}
	public FieldSelector removeField(String childFieldName) {
		return children.remove(childFieldName);
	}
	/**
	 * Helper class to add fields easily. The method will return this instance.
	 * example usage: APIFieldParam selector = new APIFieldParam().addField("id").addField("name");
	 * 
	 * @param fieldName
	 * @return
	 */
	public FieldSelector addField(String fieldName) {
		this.children.put(getRootField(fieldName), FieldSelector.instanceFromVaString(fieldName));
		return this;
	}
	/**
	 * Helper class to add fields easily. The method will return this instance.
	 * @param fields
	 * @return
	 */
	public FieldSelector addFields(Set<String> fields) {
		for(String child : fields) {
			addField(child);
		}
		return this;
	}
	/*****   Methods for accessing fieldSelector String reprensetation  ***********/
	private String getRootField(String fieldSelector) {
		String root;
		if(fieldSelector.contains(":")) {
			root = fieldSelector.substring(0, fieldSelector.indexOf(":"));
		} else {
			root = fieldSelector;
		}
		return root.trim();
	}
	private Set<String> getChildrenFields(String childFieldSelector) {
		//remove first and last "(", ")"
		childFieldSelector = childFieldSelector.substring(1, childFieldSelector.length()-1);
		Set<String> resultSet = new HashSet<String>();
		String child;
		while((child = getNextChild(childFieldSelector)) != null) {
			resultSet.add(child.trim());
			childFieldSelector = childFieldSelector.substring(child.length());
			if(childFieldSelector.startsWith(",")) {
				childFieldSelector = childFieldSelector.substring(1);
			}
		}
		return resultSet;
	}
	private String getNextChild(String fieldSelector) {
		String result = "";
		int blacket = 0;
		for(int i=0; i<fieldSelector.length(); i++) {
			char ch = fieldSelector.charAt(i);
			if(ch == '(') {
				blacket++;
			} else if (ch == ')') {
				blacket--;
			} else if (ch == ',') {
				if(blacket==0) {
					break;
				}
			}
			result += ch;
		}
		return result.length()>0 ? result : null;
	}

	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append(rootName);
		if(children.size()>0) {
			buf.append(":(");
			int first = buf.length();
			for(FieldSelector child : children.values()) {
				if(buf.length()-first>0) buf.append(", ");
				buf.append(child.toString());
			}
			buf.append(")");
		}
		return buf.toString();
	}
}
