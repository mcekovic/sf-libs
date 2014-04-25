package org.strangeforest.db.gateway;

import java.sql.*;
import java.text.*;

import org.strangeforest.db.*;

import static org.strangeforest.db.gateway.DataHelper.*;

public class NamedParameters implements StatementPreparer {

	// DSL

	public static NamedParameters params(String name, Object value) {
		return new NamedParameters(new Parameter[] {new Parameter(name, value)});
	}

	public static NamedParameters params(Parameter... params) {
		return params != null ? new NamedParameters(params) : EMPTY_PARAMS;
	}


	// Impl

	private final Parameter[] params;

	private static final NamedParameters EMPTY_PARAMS = new NamedParameters();

	private NamedParameters(Parameter... params) {
		this.params = params;
	}

	@Override public void prepare(PreparedStatementHelper st) throws SQLException {
		for (Parameter param : params) {
			if (!trySetObject(st, param.name, param.value))
				unknownParamType(param);
		}
	}

	protected void unknownParamType(Parameter param) {
		throw new IllegalArgumentException(MessageFormat.format("Unsupported parameter type {0} for name {1}.", param.value.getClass().getName(), param.name));
	}
}
