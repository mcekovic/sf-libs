package org.strangeforest.db.gateway;

import java.sql.*;

public class DataIntegrityViolationException extends DBException {

	public DataIntegrityViolationException(String message, SQLException cause, String sql) {
		super(message, cause, sql);
	}
}
