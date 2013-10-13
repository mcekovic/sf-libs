package org.strangeforest.db;

import java.sql.*;
import java.text.*;
import java.util.*;

import org.strangeforest.cache.*;
import org.strangeforest.pool.*;
import org.strangeforest.util.*;

final class PooledConnection {

	private final ConnectionPool pool;
	private final Connection conn;
	private ConnectionPoolLogger logger;
	private PreparedStatement checkStatement;
	private volatile StatementHelper lastStatement;
	private volatile Cache<Object, PreparedStatementHelper> statements;
	private volatile boolean isClosed;

	PooledConnection(ConnectionPool pool, Connection conn) throws SQLException {
		super();
		this.pool = pool;
		this.conn = conn;
		ResourcePoolLogger logger = pool.getLogger();
		if (logger instanceof ConnectionPoolLogger)
			this.logger = (ConnectionPoolLogger)logger;
		initConnection(pool.getInitQuery());
		setCheckQuery(pool.getCheckQuery());
		setMaxStatements(pool.getMaxStatements());
		setClientInfo(pool.getClientInfo());
	}

	public ConnectionPool getConnectionPool() {
		return pool;
	}

	public Connection getConnection() {
		return conn;
	}

	public ConnectionPoolLogger getLogger() {
		return logger;
	}

	void setLogger(ConnectionPoolLogger logger) {
		this.logger = logger;
	}

	Cache<Object, PreparedStatementHelper> getStatementCache() {
		return statements;
	}

	public void close() throws SQLException {
		isClosed = true;
		try {
			if (lastStatement != null && !lastStatement.isClosed()) {
				lastStatement.cancel();
				lastStatement.close();
			}
		}
		catch (SQLException ignored) {}
		Cache<Object, PreparedStatementHelper> statements = this.statements;
		if (statements != null) {
			synchronized (statements) {
				closeStatements(statements);
			}
		}
		try {
			if (!conn.getAutoCommit())
				conn.rollback();
		}
		catch (SQLException ignored) {}
		conn.close();
	}

	public void initConnection(String initQuery) throws SQLException {
		if (!StringUtil.isNullOrEmpty(initQuery)) {
			try (Statement st = conn.createStatement()) {
				st.executeUpdate(initQuery);
			}
		}
	}

	public boolean checkConnection() {
		try {
			if (conn.isClosed())
				return false;
			int checkTimeout = (int)(pool.getCheckTimeout()/1000L);
			if (checkStatement != null) {
				checkStatement.setQueryTimeout(checkTimeout);
				checkStatement.executeQuery();
				return true;
			}
			else
				return conn.isValid(checkTimeout);
		}
		catch (Throwable th) {
			return false;
		}
	}

	public void returnToPool(boolean tranResolved) {
		if (!isClosed) {
			try {
				if (!conn.getAutoCommit()) {
					if (!tranResolved)
						conn.rollback();
					conn.setAutoCommit(true);
				}
				conn.clearWarnings();
				pool.returnConnection(this);
			}
			catch (SQLException ex) {
				pool.removeConnection(this);
			}
		}
	}

	public void removeFromPool(boolean tranResolved) {
		if (!isClosed) {
			try {
				if (!(conn.getAutoCommit() || tranResolved))
					conn.rollback();
			}
			catch (SQLException ignored) {}
			pool.removeConnection(this);
		}
	}

	public PreparedStatement prepareStatement(String sql) throws SQLException {
		Cache<Object, PreparedStatementHelper> statements = this.statements;
		if (statements != null) {
			StatementKey key = new StatementKey(sql, PREPARED_STATEMENT);
			synchronized (statements) {
				PreparedStatementHelper st = statements.get(key);
				if (st == null) {
					st = new PreparedStatementHelper(this, key, sql);
					statements.put(key, st);
					pool.updateStatementStats(statements);
				}
				else
					st.cleanForReuse();
				return st;
			}
		}
		else
			return new PreparedStatementHelper(this, null, sql);
	}

	public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
		Cache<Object, PreparedStatementHelper> statements = this.statements;
		if (statements != null) {
			RichStatementKey key = new RichStatementKey(sql, PREPARED_STATEMENT, resultSetType, resultSetConcurrency);
			synchronized (statements) {
				PreparedStatementHelper st = statements.get(key);
				if (st == null) {
					st = new PreparedStatementHelper(this, key, sql, resultSetType, resultSetConcurrency);
					statements.put(key, st);
					pool.updateStatementStats(statements);
				}
				else
					st.cleanForReuse();
				return st;
			}
		}
		else
			return new PreparedStatementHelper(this, null, sql, resultSetType, resultSetConcurrency);
	}

	public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
		Cache<Object, PreparedStatementHelper> statements = this.statements;
		if (statements != null) {
			RichStatementKey key = new RichStatementKey(sql, PREPARED_STATEMENT, resultSetType, resultSetConcurrency, resultSetHoldability);
			synchronized (statements) {
				PreparedStatementHelper st = statements.get(key);
				if (st == null) {
					st = new PreparedStatementHelper(this, key, sql, resultSetType, resultSetConcurrency, resultSetHoldability);
					statements.put(key, st);
					pool.updateStatementStats(statements);
				}
				else
					st.cleanForReuse();
				return st;
			}
		}
		else
			return new PreparedStatementHelper(this, null, sql, resultSetType, resultSetConcurrency, resultSetHoldability);
	}

	public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
		Cache<Object, PreparedStatementHelper> statements = this.statements;
		if (statements != null) {
			AutoIncStatementKey key = new AutoIncStatementKey(sql, autoGeneratedKeys);
			synchronized (statements) {
				PreparedStatementHelper st = statements.get(key);
				if (st == null) {
					st = new PreparedStatementHelper(this, key, sql, autoGeneratedKeys);
					statements.put(key, st);
					pool.updateStatementStats(statements);
				}
				else
					st.cleanForReuse();
				return st;
			}
		}
		else
			return new PreparedStatementHelper(this, null, sql, autoGeneratedKeys);
	}

	public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
		Cache<Object, PreparedStatementHelper> statements = this.statements;
		if (statements != null) {
			AutoIncStatementKey key = new AutoIncStatementKey(sql, columnIndexes);
			synchronized (statements) {
				PreparedStatementHelper st = statements.get(key);
				if (st == null) {
					st = new PreparedStatementHelper(this, key, sql, columnIndexes);
					statements.put(key, st);
					pool.updateStatementStats(statements);
				}
				else
					st.cleanForReuse();
				return st;
			}
		}
		else
			return new PreparedStatementHelper(this, null, sql, columnIndexes);
	}

	public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
		Cache<Object, PreparedStatementHelper> statements = this.statements;
		if (statements != null) {
			AutoIncStatementKey key = new AutoIncStatementKey(sql, columnNames);
			synchronized (statements) {
				PreparedStatementHelper st = statements.get(key);
				if (st == null) {
					st = new PreparedStatementHelper(this, key, sql, columnNames);
					statements.put(key, st);
					pool.updateStatementStats(statements);
				}
				else
					st.cleanForReuse();
				return st;
			}
		}
		else
			return new PreparedStatementHelper(this, null, sql, columnNames);
	}

	public CallableStatement prepareCall(String sql) throws SQLException {
		Cache<Object, PreparedStatementHelper> statements = this.statements;
		if (statements != null) {
			StatementKey key = new StatementKey(sql, CALLABLE_STATEMENT);
			synchronized (statements) {
				CallableStatementHelper call = (CallableStatementHelper)statements.get(key);
				if (call == null) {
					call = new CallableStatementHelper(this, key, sql);
					statements.put(key, call);
					pool.updateStatementStats(statements);
				}
				else
					call.cleanForReuse();
				return call;
			}
		}
		else
			return new CallableStatementHelper(this, null, sql);
	}

	public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
		Cache<Object, PreparedStatementHelper> statements = this.statements;
		if (statements != null) {
			RichStatementKey key = new RichStatementKey(sql, CALLABLE_STATEMENT, resultSetType, resultSetConcurrency);
			synchronized (statements) {
				CallableStatementHelper call = (CallableStatementHelper)statements.get(key);
				if (call == null) {
					call = new CallableStatementHelper(this, key, sql, resultSetType, resultSetConcurrency);
					statements.put(key, call);
					pool.updateStatementStats(statements);
				}
				else
					call.cleanForReuse();
				return call;
			}
		}
		else
			return new CallableStatementHelper(this, null, sql, resultSetType, resultSetConcurrency);
	}

	public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
		Cache<Object, PreparedStatementHelper> statements = this.statements;
		if (statements != null) {
			RichStatementKey key = new RichStatementKey(sql, CALLABLE_STATEMENT, resultSetType, resultSetConcurrency, resultSetHoldability);
			synchronized (statements) {
				CallableStatementHelper call = (CallableStatementHelper)statements.get(key);
				if (call == null) {
					call = new CallableStatementHelper(this, key, sql, resultSetType, resultSetConcurrency, resultSetHoldability);
					statements.put(key, call);
					pool.updateStatementStats(statements);
				}
				else
					call.cleanForReuse();
				return call;
			}
		}
		else
			return new CallableStatementHelper(this, null, sql, resultSetType, resultSetConcurrency, resultSetHoldability);
	}

	public synchronized void setCheckQuery(String checkQuery) throws SQLException {
		if (checkStatement != null)
			checkStatement.close();
		if (!StringUtil.isNullOrEmpty(checkQuery))
			checkStatement = conn.prepareStatement(checkQuery);
		else
			checkStatement = null;
	}

	public void setMaxStatements(int maxStatements) {
		Cache<Object, PreparedStatementHelper> statements = this.statements;
		if (statements != null) {
			synchronized (statements) {
				if (maxStatements > 0)
					statements.setCapacity(maxStatements);
				else
					closeStatements(statements);
			}
		}
		else if (maxStatements > 0) {
			statements = new LRUCache<>(maxStatements);
			statements.addCacheListener(new CacheListener<Object, PreparedStatementHelper>() {
				@Override public void entryRemoved(Map.Entry<Object, PreparedStatementHelper> entry) {
					try {
						entry.getValue().doClose();
					}
					catch (SQLException ignored) {}
				}
			});
			this.statements = statements;
		}
	}

	void setClientInfo(Properties clientInfo) throws SQLClientInfoException {
		if (clientInfo != null)
			conn.setClientInfo(clientInfo);
	}

	void removeStatement(Object key) {
		Cache<Object, PreparedStatementHelper> statements = this.statements;
		if (statements != null) {
			synchronized (statements) {
				statements.remove(key);
			}
		}
	}

	StatementHelper getLastStatement() {
		return lastStatement;
	}

	void setLastStatement(StatementHelper lastStatement) {
		this.lastStatement = lastStatement;
	}

	private void closeStatements(Cache<Object, PreparedStatementHelper> statements) {
		for (PreparedStatementHelper st : statements.values()) {
			try {
				st.doClose();
			}
			catch (SQLException ignored) {}
		}
		this.statements = null;
	}

	@Override public String toString() {
		return String.valueOf(lastStatement);
	}

	private static final String KEY_TEMPLATE = "{0}; TYPE={1}";
	private static final String RICH_KEY_TEMPLATE = "{0}; TYPE={1}; RST={2}; RSC={3}; RSH={4}";
	private static final String AUTO_INC_KEY_TEMPLATE = "{0}; AIC={1}";
	private static final int PREPARED_STATEMENT = 0;
	private static final int CALLABLE_STATEMENT = 1;

	private static final class StatementKey {
		
		private String sql;
		private int type;

		private StatementKey(String sql, int type) {
			super();
			this.sql = sql;
			this.type = type;
		}

		@Override public boolean equals(Object obj) {
			if (obj instanceof StatementKey) {
				StatementKey key = (StatementKey)obj;
				return key.sql.equals(sql) && key.type == type;
			}
			else
				return false;
		}

		@Override public int hashCode() {
			return sql.hashCode();
		}

		@Override public String toString() {
			return MessageFormat.format(KEY_TEMPLATE, sql, type);
		}
	}

	
	private static final class RichStatementKey {
		
		private String sql;
		private int type, resultSetType, resultSetConcurrency, resultSetHoldability;

		private RichStatementKey(String sql, int type, int resultSetType, int resultSetConcurrency) {
			this(sql, type, resultSetType, resultSetConcurrency, -1);
		}

		private RichStatementKey(String sql, int type, int resultSetType, int resultSetConcurrency, int resultSetHoldability) {
			super();
			this.sql = sql;
			this.type = type;
			this.resultSetType = resultSetType;
			this.resultSetConcurrency = resultSetConcurrency;
			this.resultSetHoldability = resultSetHoldability;
		}

		@Override public boolean equals(Object obj) {
			if (obj instanceof RichStatementKey) {
				RichStatementKey key = (RichStatementKey)obj;
				return key.sql.equals(sql)
					 && key.type == type
					 && key.resultSetType == resultSetType
					 && key.resultSetConcurrency == resultSetConcurrency
					 && key.resultSetHoldability == resultSetHoldability;
			}
			else
				return false;
		}

		@Override public int hashCode() {
			return sql.hashCode();
		}

		@Override public String toString() {
			return MessageFormat.format(RICH_KEY_TEMPLATE, sql, type, resultSetType, resultSetConcurrency, resultSetHoldability);
		}
	}


	private static final class AutoIncStatementKey {
		
		private String sql;
		private int autoGeneratedKeys;
		private int[] columnIndexes;
		private String[] columnNames;

		private AutoIncStatementKey(String sql, int autoGeneratedKeys) {
			super();
			this.sql = sql;
			this.autoGeneratedKeys = autoGeneratedKeys;
		}

		private AutoIncStatementKey(String sql, int[] columnIndexes) {
			super();
			this.sql = sql;
			this.columnIndexes = columnIndexes;
		}

		private AutoIncStatementKey(String sql, String[] columnNames) {
			super();
			this.sql = sql;
			this.columnNames = columnNames;
		}

		@Override public boolean equals(Object obj) {
			if (obj instanceof AutoIncStatementKey) {
				AutoIncStatementKey key = (AutoIncStatementKey)obj;
				return key.sql.equals(sql)
					 && key.autoGeneratedKeys == autoGeneratedKeys
					 && Arrays.equals(key.columnIndexes, columnIndexes)
					 && Arrays.equals(key.columnNames, columnNames);
			}
			else
				return false;
		}

		@Override public int hashCode() {
			return sql.hashCode();
		}

		@Override public String toString() {
			return MessageFormat.format(AUTO_INC_KEY_TEMPLATE, sql, columnNames != null ? Arrays.toString(columnNames) : (columnIndexes != null ? Arrays.toString(columnIndexes) : autoGeneratedKeys));
		}
	}
}
