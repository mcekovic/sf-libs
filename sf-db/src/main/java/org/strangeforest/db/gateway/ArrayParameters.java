package org.strangeforest.db.gateway;

import java.sql.*;
import java.text.*;

import org.strangeforest.db.*;

public class ArrayParameters implements StatementPreparer {

	// DSL

	public static ArrayParameters params(Object... values) {
		return values != null ? new ArrayParameters(values) : NULL_PARAM;
	}


	// Impl

	private final Object[] values;

	private static final ArrayParameters NULL_PARAM = new ArrayParameters(new Object[] {null});

	private ArrayParameters(Object... values) {
		this.values = values;
	}

	@Override public void prepare(PreparedStatementHelper st) throws SQLException {
		for (int i = 0, count = values.length; i < count; i++) {
			Object value = values[i];
			int index = i + 1;
			if (!DataHelper.trySetObject(st, index, value))
				unknownParamType(index, value);
		}
	}

	protected void unknownParamType(int index, Object value) {
		throw new IllegalArgumentException(MessageFormat.format("Unsuported parameter type {0} for index {1}.", value.getClass().getName(), index));
	}
}
