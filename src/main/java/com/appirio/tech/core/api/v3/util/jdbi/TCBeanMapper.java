package com.appirio.tech.core.api.v3.util.jdbi;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import com.appirio.tech.core.api.v3.TCID;

/**
 * https://github.com/jdbi/jdbi/blob/jdbi-2.55/src/main/java/org/skife/jdbi/v2/BeanMapper.java
 */
public class TCBeanMapper<T> implements ResultSetMapper<T> {
	
	private static final Logger logger = Logger.getLogger(TCBeanMapper.class);

	private final Class<T> type;
	private final Map<String, PropertyDescriptor> properties = new HashMap<String, PropertyDescriptor>();
	private String pathSeparatorPattern = "\\$";

	public TCBeanMapper(Class<T> type) {
		this.type = type;
		try {
			BeanInfo info = Introspector.getBeanInfo(type);
			for (PropertyDescriptor descriptor : info.getPropertyDescriptors()) {
				properties.put(descriptor.getName().toLowerCase(), descriptor);
			}
		}catch (IntrospectionException e) {
			throw new IllegalArgumentException(e);
		}
	}
	
	@SuppressWarnings({"rawtypes"})
	public T map(int row, ResultSet rs, StatementContext ctx) throws SQLException {
		T bean;
		try {
			bean = type.newInstance();
		} catch (Exception e) {
			throw new IllegalArgumentException(String.format("A bean, %s, was mapped which was not instantiable", type.getName()), e);
		}

		ResultSetMetaData metadata = rs.getMetaData();

		for (int i = 1; i <= metadata.getColumnCount(); ++i) {
			String name = metadata.getColumnLabel(i).toLowerCase();
			String[] path =  name.split(this.pathSeparatorPattern);
			
			PropertyDescriptor descriptor = properties.get(path[0]);
			if (descriptor == null)
				continue;
			
			Class type = descriptor.getPropertyType();
			Object value = getValue(rs, i, type);
			setValue(bean, path, value);
		}
		return bean;
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	protected void setValue(T bean, String[] path, Object value) {

		PropertyDescriptor descriptor = properties.get(path[0]);
		Class fieldType = descriptor.getPropertyType();

		logger.debug(String.format("setValue: path->%s, bean->%s, fieldType->%s, value->%s", join(path), bean.getClass().getSimpleName(), fieldType.getSimpleName(), value));
		
		if(path.length>1) {
			// nested property
			try {
				TCBeanMapper m = new TCBeanMapper(fieldType);
				Object child = descriptor.getReadMethod().invoke(bean, new Object[0]);
				if(child==null) {
					child = fieldType.newInstance();
					setValue(bean, path[0], child);
				}
				m.setValue(child, shift(path), value);
			} catch (Exception e) {
				throw new IllegalArgumentException(String.format("A bean, %s, was mapped which was not instantiable", fieldType.getName()), e);
			}
		} else {
			setValue(bean, path[0], value);
		}
	}
	
	protected void setValue(T bean, String name, Object value) {
		PropertyDescriptor descriptor = properties.get(name);
		try {
			descriptor.getWriteMethod().invoke(bean, value);
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException(String.format("Unable to access setter for property, %s", name), e);
		} catch (InvocationTargetException e) {
			throw new IllegalArgumentException(String.format("Invocation target exception trying to invoker setter for the %s property", name), e);
		} catch (NullPointerException e) {
			throw new IllegalArgumentException(String.format("No appropriate method to write property %s", name), e);
		}
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	protected Object getValue(ResultSet rs, int i, Class type) throws SQLException {
		
		Object value;
		if (type.isAssignableFrom(TCID.class)) {
			value = rs.getObject(i);
			if(value != null) value = new TCID(value.toString());
		}
		else if (type.isAssignableFrom(DateTime.class)) {
			value = null;
			Timestamp ts = rs.getTimestamp(i);
			if(ts != null) {
				value = new DateTime(ts.getTime());
			}
		}
		else
		//
		if (type.isAssignableFrom(Boolean.class) || type.isAssignableFrom(boolean.class)) {
			value = rs.getBoolean(i);
		}
		else if (type.isAssignableFrom(Byte.class) || type.isAssignableFrom(byte.class)) {
			value = rs.getByte(i);
		}
		else if (type.isAssignableFrom(Short.class) || type.isAssignableFrom(short.class)) {
			value = rs.getShort(i);
		}
		else if (type.isAssignableFrom(Integer.class) || type.isAssignableFrom(int.class)) {
			value = rs.getInt(i);
		}
		else if (type.isAssignableFrom(Long.class) || type.isAssignableFrom(long.class)) {
			value = rs.getLong(i);
		}
		else if (type.isAssignableFrom(Float.class) || type.isAssignableFrom(float.class)) {
			value = rs.getFloat(i);
		}
		else if (type.isAssignableFrom(Double.class) || type.isAssignableFrom(double.class)) {
			value = rs.getDouble(i);
		}
		else if (type.isAssignableFrom(BigDecimal.class)) {
			value = rs.getBigDecimal(i);
		}
		else if (type.isAssignableFrom(Timestamp.class)) {
			value = rs.getTimestamp(i);
		}
		else if (type.isAssignableFrom(Time.class)) {
			value = rs.getTime(i);
		}
		else if (type.isAssignableFrom(Date.class)) {
			value = rs.getDate(i);
		}
		else if (type.isAssignableFrom(String.class)) {
			value = rs.getString(i);
		}
		else if (type.isEnum()) {
			String str = rs.getString(i);
			value = str != null ? Enum.valueOf(type, str) : null;
		}
		else {
			value = rs.getObject(i);
		}
		if (rs.wasNull() && !type.isPrimitive()) {
			value = null;
		}
		return value;
	}

	private String[] shift(String[] sarray) {
		String[] result = new String[sarray.length-1];
		System.arraycopy(sarray, 1, result, 0, result.length);
		return result;
	}

	private String join(String[] sarray) {
		if(sarray==null || sarray.length==0)
			return "";
		if(sarray.length==1)
			return sarray[0];
		try {
			StringBuilder sb = new StringBuilder();
			for(int i=0; i<sarray.length; i++)
				sb.append(sarray[i]).append(".");
			return sb.toString().replaceFirst(".$", "");
		} catch (Exception e) {
			return "";
		}
	}
	
	public String getPathSeparatorPattern() {
		return pathSeparatorPattern;
	}

	public void setPathSeparatorPattern(String pathSeparatorPattern) {
		this.pathSeparatorPattern = pathSeparatorPattern;
	}
	
}
