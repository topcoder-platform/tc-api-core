package com.appirio.tech.core.api.v3.util.jdbi;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Timestamp;

import org.joda.time.DateTime;
import org.junit.Test;

import com.appirio.tech.core.api.v3.TCID;

public class TCBeanMapperTest {

	@Test
	public void testSimple() throws Exception {
		
		TCBeanMapper<Simple> mapper = new TCBeanMapper<Simple>(Simple.class);
		
		ResultSet rset = mock(ResultSet.class);
		ResultSetMetaData meta = mock(ResultSetMetaData.class);

		when(rset.getMetaData()).thenReturn(meta);
		
		when(meta.getColumnCount()).thenReturn(4);
		when(meta.getColumnLabel(1)).thenReturn("id");
		when(meta.getColumnLabel(2)).thenReturn("s");
		when(meta.getColumnLabel(3)).thenReturn("i");
		when(meta.getColumnLabel(4)).thenReturn("dt");
		
		String id = "123456";
		String str = "abcdefg";
		int i = 1;
		Timestamp ts = new Timestamp(System.currentTimeMillis());
		when(rset.getObject(1)).thenReturn(id);
		when(rset.getString(2)).thenReturn(str);
		when(rset.getInt(3)).thenReturn(i);
		when(rset.getTimestamp(4)).thenReturn(ts);
		
		Simple simple = mapper.map(0, rset, null);
		
		assertNotNull(simple);
		assertEquals(id, simple.getId().toString());
		assertEquals(str, simple.getS());
		assertEquals(i, (int)simple.getI());
		assertEquals(ts.getTime(), simple.getDt().toDateTime().getMillis());
		
	}

	@Test
	public void testNested() throws Exception {
	
		TCBeanMapper<Nested> mapper = new TCBeanMapper<Nested>(Nested.class);
		
		ResultSet rset = mock(ResultSet.class);
		ResultSetMetaData meta = mock(ResultSetMetaData.class);

		when(rset.getMetaData()).thenReturn(meta);
		
		when(meta.getColumnCount()).thenReturn(2);
		when(meta.getColumnLabel(1)).thenReturn("id");
		when(meta.getColumnLabel(2)).thenReturn("simple$s");
		
		String id = "123456";
		String str = "abcdefg";
		when(rset.getObject(1)).thenReturn(id);
		when(rset.getObject(2)).thenReturn(str);
		when(rset.wasNull()).thenReturn(false);

		Nested nested = mapper.map(0, rset, null);
		
		assertNotNull(nested);
		assertEquals(id, nested.getId().toString());
		assertNotNull(nested.getSimple());
		assertEquals(str, nested.getSimple().getS());

	}
	
	@Test
	public void testNested2() throws Exception {
	
		TCBeanMapper<Nested> mapper = new TCBeanMapper<Nested>(Nested.class);
		
		ResultSet rset = mock(ResultSet.class);
		ResultSetMetaData meta = mock(ResultSetMetaData.class);

		when(rset.getMetaData()).thenReturn(meta);
		
		when(meta.getColumnCount()).thenReturn(4);
		when(meta.getColumnLabel(1)).thenReturn("id");
		when(meta.getColumnLabel(2)).thenReturn("simple$s");
		when(meta.getColumnLabel(3)).thenReturn("nested$simple$i");
		when(meta.getColumnLabel(4)).thenReturn("simple$i");
		
		String id = "123456";
		String str = "abcdefg";
		int i = 100;
		when(rset.getObject(1)).thenReturn(id);
		when(rset.getObject(2)).thenReturn(str);
		when(rset.getObject(3)).thenReturn(i);
		when(rset.getObject(4)).thenReturn(i+1);
		when(rset.wasNull()).thenReturn(false);

		Nested nested = mapper.map(0, rset, null);
		
		assertNotNull(nested);
		assertEquals(id, nested.getId().toString());
		assertNotNull(nested.getSimple());
		assertEquals(str, nested.getSimple().getS());
		assertEquals(i+1, (int)nested.getSimple().getI());
		assertNotNull(nested.getNested());
		assertNotNull(nested.getNested().getSimple());
		assertEquals(i, (int)nested.getNested().getSimple().getI());
	}
	
	public static class Simple {
		TCID id;
		String  s;
		Integer i;
		DateTime dt;
		public TCID getId() {
			return id;
		}
		public void setId(TCID id) {
			this.id = id;
		}
		public String getS() {
			return s;
		}
		public void setS(String s) {
			this.s = s;
		}
		public Integer getI() {
			return i;
		}
		public void setI(Integer i) {
			this.i = i;
		}
		public DateTime getDt() {
			return dt;
		}
		public void setDt(DateTime dt) {
			this.dt = dt;
		}
	}
	public static class Nested {
		TCID id;
		Simple simple;
		Nested nested;
		public TCID getId() {
			return id;
		}
		public void setId(TCID id) {
			this.id = id;
		}
		public Simple getSimple() {
			return simple;
		}
		public void setSimple(Simple simple) {
			this.simple = simple;
		}
		public Nested getNested() {
			return nested;
		}
		public void setNested(Nested nested) {
			this.nested = nested;
		}
	}
}
