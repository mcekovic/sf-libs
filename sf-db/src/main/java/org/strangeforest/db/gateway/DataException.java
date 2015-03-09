package org.strangeforest.db.gateway;

import java.sql.*;

public class DataException extends DBException {

	public DataException(String message, SQLException cause, String sql) {
		super(message, cause, sql);
	}
}
