package org.strangeforest.db;

import java.sql.*;
import javax.sql.*;

class DataSourceConnectionManager extends ConnectionManager {

	private final DataSource dataSource;

	DataSourceConnectionManager(ConnectionPool pool, DataSource dataSource, String username, String password) {
		super(pool, username, password);
		this.dataSource = dataSource;
	}

	@Override protected Connection allocateConnection() throws SQLException {
		if (username == null || password == null)
			return dataSource.getConnection();
		else
			return dataSource.getConnection(username, password);
	}
}
