package org.strangeforest.db;

import java.sql.*;

import org.strangeforest.pool.*;

public interface ConnectionPoolLogger extends ResourcePoolLogger {

	void logStatement(Statement st);
	void logPreparedStatement(PreparedStatement pst);
	void logCallableStatement(CallableStatement call);
}
