package org.strangeforest.db;

import java.sql.*;

import org.strangeforest.transaction.*;

public class ConnectionResource implements TransactionalResource {

	private Connection conn;

	public ConnectionResource(Connection conn) {
		super();
		this.conn = conn;
	}

	public Connection getConnection() {
		return conn;
	}

	@Override public void commit() throws TransactionException {
		try {
			conn.commit();
		}
		catch (SQLException ex) {
			throw new TransactionException("Error committing transaction.", ex);
		}
	}

	@Override public void rollback() throws TransactionException {
		try {
			conn.rollback();
		}
		catch (SQLException ex) {
			throw new TransactionException("Error rollbacking transaction.", ex);
		}
	}

	@Override public boolean isSameResource(TransactionalResource resource) {
		if (resource == this)
			return true;
		else if (!(resource instanceof ConnectionResource))
			return false;
		return ((ConnectionResource)resource).conn == conn;
	}
}
