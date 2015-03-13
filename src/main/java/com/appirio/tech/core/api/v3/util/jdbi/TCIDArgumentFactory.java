package com.appirio.tech.core.api.v3.util.jdbi;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.Argument;
import org.skife.jdbi.v2.tweak.ArgumentFactory;

import com.appirio.tech.core.api.v3.TCID;

public class TCIDArgumentFactory implements ArgumentFactory<TCID> {

    @Override
    public boolean accepts(final Class<?> expectedType,
                           final Object value,
                           final StatementContext ctx) {
        return value instanceof TCID;
    }

    @Override
    public Argument build(final Class<?> expectedType,
                          final TCID value,
                          final StatementContext ctx) {
        return new TCIDArgument(value);
    }

}
