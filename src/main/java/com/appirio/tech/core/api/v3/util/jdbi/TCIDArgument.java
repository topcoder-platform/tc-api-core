package com.appirio.tech.core.api.v3.util.jdbi;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.Argument;

import com.appirio.tech.core.api.v3.TCID;

public class TCIDArgument implements Argument {

	private final TCID value;
	
	TCIDArgument(TCID value) {
		this.value = value;
	}
	
	@Override
	public void apply(int position,
						PreparedStatement statement,
						StatementContext ctx) throws SQLException {
        if (value != null) {
            statement.setLong(position, Long.parseLong(value.toString()));
        } else {
            statement.setNull(position, Types.BIGINT);
        }
	}
}
