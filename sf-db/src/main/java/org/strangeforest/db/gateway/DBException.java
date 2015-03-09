package org.strangeforest.db.gateway;

import java.sql.*;

import org.strangeforest.util.*;

public class DBException extends RuntimeException {

	// Factory

	public static DBException create(String message, String sql, SQLException cause) {
		if (cause instanceof SQLIntegrityConstraintViolationException)
			return new DataIntegrityViolationException(message, cause, sql);
		else if (cause instanceof SQLDataException)
			return new DataException(message, cause, sql);
		else
			return new DBException(message, cause, sql);
	}


	// Instance

	private String sql;

	public DBException(String message) {
		super(message);
	}

	public DBException(String message, String sql) {
		super(message);
		this.sql = sql;
	}

	public DBException(Throwable cause) {
		super(cause);
	}

	public DBException(SQLException cause, String sql) {
		super(cause);
		this.sql = sql;
	}

	public DBException(String message, Throwable cause) {
		super(message, cause);
	}

	public DBException(String message, SQLException cause, String sql) {
		super(message, cause);
		this.sql = sql;
	}

	public int getErrorCode() {
		return ExceptionUtil.getRootSQLErrorCode(getCause());
	}

	public static DBException wrap(Throwable th) {
		return th instanceof DBException ? (DBException)th : new DBException(th);
	}

	@Override public String getMessage() {
		StringBuilder sb = new StringBuilder();
		sb.append(super.getMessage());
		if (sql != null) {
			sb.append('\n');
			sb.append(sql);
		}
		return sb.toString();
	}
}
