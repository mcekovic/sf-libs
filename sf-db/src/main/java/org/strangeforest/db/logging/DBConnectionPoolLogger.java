package org.strangeforest.db.logging;

import java.sql.*;

import org.slf4j.*;
import org.strangeforest.db.*;

import static org.strangeforest.db.ConnectionPoolLogger.*;

public final class DBConnectionPoolLogger implements ConnectionPoolLogger {

	private Logger dbLogger;

	public DBConnectionPoolLogger(String loggerName) {
		dbLogger = LoggerFactory.getLogger(loggerName);
	}

	@Override public void logMessage(String message) {
		dbLogger.debug(message);
	}

	@Override public void logError(String message, Throwable th) {
		dbLogger.warn(message, th);
	}

	@Override public void logStatement(Statement st) {
		if (dbLogger.isTraceEnabled())
			dbLogger.trace(toStatementString(st));
	}

	@Override public void logPreparedStatement(PreparedStatement pst) {
		if (dbLogger.isTraceEnabled())
			dbLogger.trace(toStatementString(pst));
	}

	@Override public void logCallableStatement(CallableStatement call) {
		if (dbLogger.isTraceEnabled())
			dbLogger.trace(toStatementString(call));
	}
}
