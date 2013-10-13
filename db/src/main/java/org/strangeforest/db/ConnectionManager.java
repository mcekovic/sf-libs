package org.strangeforest.db;

import java.sql.*;

import org.strangeforest.pool.*;
import org.strangeforest.util.*;

abstract class ConnectionManager implements ResourceManager<PooledConnection> {

	protected ConnectionPool pool;
	protected String username, password;

	protected ConnectionManager(ConnectionPool pool, String username, String password) {
		super();
		this.pool = pool;
		this.username = username;
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	@Override public PooledConnection allocateResource() throws PoolException {
		try {
			Connection conn = allocateConnection();
			return new PooledConnection(pool, conn);
		}
		catch (SQLException ex) {
			throw new PoolException("Error allocating new database connection.", ex);
		}
	}

	@Override public void releaseResource(PooledConnection conn) {
		try {
			conn.close();
		}
		catch (SQLException ex) {
			pool.logError("Error releasing database connection.", ex);
		}
	}

	@Override public boolean checkResource(PooledConnection conn) {
		boolean ok = conn.checkConnection();
		pool.logMessage("Check connection: " + (ok ? "OK" : "FAILED"));
		return ok;
	}

	protected abstract Connection allocateConnection() throws SQLException;

	public boolean checkCredentials(String username, String password) {
		return ObjectUtil.equal(username, this.username) && ObjectUtil.equal(password, this.password);
	}
}
