package com.appirio.tech.core.api.v3.util.jdbi;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.BeanMapperFactory;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

public class TCBeanMapperFactory extends BeanMapperFactory {

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ResultSetMapper mapperFor(Class type, StatementContext ctx) {
		return new TCBeanMapper(type);
	}
}
