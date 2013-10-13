package org.strangeforest.db;

import java.sql.*;

import org.strangeforest.transaction.*;

final class PooledConnectionResource implements TransactionalResource {

	private final ConnectionPool pool;
	private final ConnectionProxy proxy;

	public PooledConnectionResource(ConnectionPool pool, ConnectionProxy proxy) {
		super();
		this.pool = pool;
		this.proxy = proxy;
		proxy.setManaged(true);
	}

	public ConnectionProxy getConnectionProxy() {
		return proxy;
	}

	@Override public void commit() throws TransactionException {
		proxy.setManaged(false);
		try {
			proxy.commit();
			proxy.close();
		}
		catch (SQLException ex) {
			throw new TransactionException("Error committing transaction.", ex);
		}
	}

	@Override public void rollback() throws TransactionException {
		proxy.setManaged(false);
		try {
			proxy.rollback();
		}
		catch (Throwable th) {
			proxy.markBad();
		}
		try {
			proxy.close();
		}
		catch (SQLException ex) {
			throw new TransactionException("Error rollbacking transaction.", ex);
		}
	}

	@Override public boolean isSameResource(TransactionalResource resource) {
		if (resource == this)
			return true;
		if (!(resource instanceof PooledConnectionResource))
			return false;
		return ((PooledConnectionResource)resource).pool == pool;
	}

	public boolean isForConnectionPool(ConnectionPool pool) {
		return this.pool == pool;
	}
}
