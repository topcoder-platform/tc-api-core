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

import org.joda.time.DateTime;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import com.appirio.tech.core.api.v3.TCID;

/**
 * https://github.com/jdbi/jdbi/blob/jdbi-2.55/src/main/java/org/skife/jdbi/v2/BeanMapper.java
 */
public class TCBeanMapper<T> implements ResultSetMapper<T> {

    private final Class<T> type;
    private final Map<String, PropertyDescriptor> properties = new HashMap<String, PropertyDescriptor>();

    public TCBeanMapper(Class<T> type) {
        this.type = type;
        try {
            BeanInfo info = Introspector.getBeanInfo(type);
            for (PropertyDescriptor descriptor : info.getPropertyDescriptors()) {
                properties.put(descriptor.getName().toLowerCase(), descriptor);
            }
        }
        catch (IntrospectionException e) {
            throw new IllegalArgumentException(e);
        }
    }
    
	@SuppressWarnings({"unchecked", "rawtypes"})
	public T map(int row, ResultSet rs, StatementContext ctx) throws SQLException {
        T bean;
        try {
            bean = type.newInstance();
        }
        catch (Exception e) {
            throw new IllegalArgumentException(String.format("A bean, %s, was mapped " +
                                                             "which was not instantiable", type.getName()), e);
        }

        ResultSetMetaData metadata = rs.getMetaData();

        for (int i = 1; i <= metadata.getColumnCount(); ++i) {
            String name = metadata.getColumnLabel(i).toLowerCase();

            PropertyDescriptor descriptor = properties.get(name);

            if (descriptor != null) {
                Class type = descriptor.getPropertyType();

                Object value;

                // extension for our API
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

                try
                {
                    descriptor.getWriteMethod().invoke(bean, value);
                }
                catch (IllegalAccessException e) {
                    throw new IllegalArgumentException(String.format("Unable to access setter for " +
                                                                     "property, %s", name), e);
                }
                catch (InvocationTargetException e) {
                    throw new IllegalArgumentException(String.format("Invocation target exception trying to " +
                                                                     "invoker setter for the %s property", name), e);
                }
                catch (NullPointerException e) {
                    throw new IllegalArgumentException(String.format("No appropriate method to " +
                                                                     "write property %s", name), e);
                }

            }
        }

        return bean;
	}
	
}
