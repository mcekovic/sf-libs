package org.strangeforest.db;

import java.io.*;
import java.sql.*;
import java.util.*;
import javax.sql.*;

import org.strangeforest.cache.*;
import org.strangeforest.pool.*;
import org.strangeforest.transaction.*;
import org.strangeforest.util.*;

/**
 * <p><tt>ConnectionPool</tt> pools database connections, or more precisely, JDBC <tt>Connection</tt> objects.
 * <tt>ConnectionPool</tt> inherits all the features of <tt>ResourcePool</tt> and adds prepared statement caching.</p>
 * <p>Typical usage will be:</p>
 * <blockquote><pre>
 * Connection conn = pool.getConnection();
 * try {
 *    ... // do some JDBC stuff
 * }
 * finally {
 *    conn.close();
 * }
 * </pre></blockquote>
 * <p>Typical usage with explicit transaction control will be:</p>
 * <blockquote><pre>
 * Connection conn = pool.getConnection();
 * try {
 *    conn.setAutoCommit(false);
 *    ... // do some JDBC stuff
 *    conn.commit();
 * }
 * catch (SQLException ex) {
 *    conn.rollback();
 *    throw ex;
 * }
 * finally {
 *    conn.close();
 * }
 * </pre></blockquote>
 * @see org.strangeforest.pool.ResourcePool
 */
public class ConnectionPool extends ResourcePool<PooledConnection> {

	private String initQuery, checkQuery;
	private long checkTimeout;
	private int maxStatements;
	private Properties clientInfo;
	private Cache<String, StatementStatistics> statementStats = new LRUCache<>(DEFAULT_TOP_STATEMENTS);
	private int peakStatementCacheSize;
	private volatile boolean collectStatementStatistics;

	public static final String DEFAULT_INIT_QUERY = null;
	public static final String DEFAULT_CHECK_QUERY = null;
	public static final long DEFAULT_CHECK_TIMEOUT = 30*1000L;
	public static final int DEFAULT_MAX_STATEMENTS = 0;
	public static final int DEFAULT_TOP_STATEMENTS = 100;

	/**
	 * Creates <tt>ConnectionPool</tt> and initializes it.
	 * @param driverClass class name of the JDBC driver class.
	 * @param dbURL database URL to connect to.
	 * @param username database username.
	 * @param password database password.
	 */
	public ConnectionPool(String driverClass, String dbURL, String username, String password) {
		this(driverClass, dbURL, username, password,
			  DEFAULT_INITIAL_POOL_SIZE, DEFAULT_MIN_POOL_SIZE, DEFAULT_MAX_POOL_SIZE,
			  DEFAULT_INIT_QUERY, DEFAULT_CHECK_QUERY);
	}


	/**
	 * Creates <tt>ConnectionPool</tt> and initializes it.
	 * @param driverClass class name of the JDBC driver class.
	 * @param dbURL database URL to connect to.
	 * @param username database username.
	 * @param password database password.
	 * @param initialPoolSize number of connection to allocate when initializing the pool.
	 * @param minPoolSize minimum number of connections under which the pool will not shrink.
	 * @param maxPoolSize maximum number of connections.
	 * @param initQuery SQL query to perform after connection is allocated.
	 * @param checkQuery SQL query to perform on a connection to check its connectivity to the database.
	 * <p>See <tt>ResourcePool</tt> documentation for detailed description of pool parameters.</p>
	 */
	public ConnectionPool(String driverClass, String dbURL, String username, String password,
								 int initialPoolSize, int minPoolSize, int maxPoolSize,
								 String initQuery, String checkQuery) {
		this(driverClass, dbURL, username, password, null,
			  initialPoolSize, minPoolSize, maxPoolSize, DEFAULT_MIN_IDLE_COUNT, DEFAULT_MAX_PENDING_COUNT,
			  initQuery, checkQuery, DEFAULT_CHECK_TIMEOUT, DEFAULT_CHECK_TIME,
			  DEFAULT_MAX_WAIT_TIME, DEFAULT_MAX_IDLE_TIME, DEFAULT_MAX_BUSY_TIME, DEFAULT_MAX_LIVE_TIME, DEFAULT_PROPERTY_CYCLE,
			  DEFAULT_MAX_STATEMENTS);
	}

	/**
	 * Creates <tt>ConnectionPool</tt> and initializes it.
	 * @param driverClass class name of the JDBC driver class.
	 * @param dbURL database URL to connect to.
	 * @param username database username.
	 * @param password database password.
	 * @param connectionProperties optional connection properties.
	 * @param initialPoolSize number of connection to allocate when initializing the pool.
	 * @param minPoolSize minimum number of connections under which the pool will not shrink.
	 * @param maxPoolSize maximum number of connections.
	 * @param minIdleCount minimum number of idle connections ready to be served.
	 * @param maxPendingCount maximum number of pending connections.
	 * @param initQuery SQL query to perform after connection is allocated.
	 * @param checkQuery SQL query to perform on a connection to check its connectivity to the database. null means JDBC method Connection.isValid() is used.
	 * @param checkTimeout check timeout.
	 * @param checkTime check time.
	 * @param maxWaitTime maximum time waiting for connection.
	 * @param maxIdleTime maximum connection idle time.
	 * @param maxBusyTime maximum connection busy time.
	 * @param maxLiveTime maximum connection live time.
	 * @param propertyCycle property cycle.
	 * @param maxStatements prepared statement cache size (per pooled connection).
	 * <p>See <tt>ResourcePool</tt> documentation for detailed description of pool parameters.</p>
	 */
	public ConnectionPool(String driverClass, String dbURL, String username, String password, Properties connectionProperties,
								 int initialPoolSize, int minPoolSize, int maxPoolSize, int minIdleCount, int maxPendingCount,
								 String initQuery, String checkQuery, long checkTimeout, long checkTime,
								 long maxWaitTime, long maxIdleTime, long maxBusyTime, long maxLiveTime, long propertyCycle,
								 int maxStatements) {
		super(initialPoolSize, minPoolSize, maxPoolSize, minIdleCount, maxPendingCount,
				checkTime, maxWaitTime, maxIdleTime, maxBusyTime, maxLiveTime, propertyCycle);
		this.initQuery = initQuery;
		this.checkQuery = checkQuery;
		this.checkTimeout = checkTimeout;
		this.maxStatements = maxStatements;
		setResourceManager(new DriverConnectionManager(this, driverClass, dbURL, username, password, connectionProperties));
	}

	/**
	 * Creates <tt>ConnectionPool</tt> and initializes it.
	 * @param dataSource specifies <i>DataSource</i> to pool connections from.
	 */
	public ConnectionPool(DataSource dataSource) {
		this(dataSource, null, null,
				DEFAULT_INITIAL_POOL_SIZE, DEFAULT_MIN_POOL_SIZE, DEFAULT_MAX_POOL_SIZE,
				DEFAULT_INIT_QUERY, DEFAULT_CHECK_QUERY);
	}

	/**
	 * Creates <tt>ConnectionPool</tt> and initializes it.
	 * @param dataSource specifies <i>DataSource</i> to pool connections from.
	 * @param username database username
	 * @param password database password
	 * @param initialPoolSize number of connection to allocate when initializing the pool.
	 * @param minPoolSize minimum number of connections under which the pool will not shrink.
	 * @param maxPoolSize maximum number of connections.
	 * @param initQuery SQL query to perform after connection is allocated.
	 * @param checkQuery SQL query to perform on a connection to check its connectivity to the database.
	 * <p>See <tt>ResourcePool</tt> documentation for detailed description of pool parameters.</p>
	 */
	public ConnectionPool(DataSource dataSource, String username, String password,
								 int initialPoolSize, int minPoolSize, int maxPoolSize,
								 String initQuery, String checkQuery) {
		super(initialPoolSize, minPoolSize, maxPoolSize);
		this.initQuery = initQuery;
		this.checkQuery = checkQuery;
		this.maxStatements = DEFAULT_MAX_STATEMENTS;
		setResourceManager(new DataSourceConnectionManager(this, dataSource, username, password));
	}

	@Override protected void preallocate(int count) {
		try {
			super.preallocate(count);
		}
		catch (Throwable th) {
			logError("Error pre-allocating connections.", th);
		}
	}

	ConnectionManager getConnectionManager() {
		return (ConnectionManager)getResourceManager();
	}

	/**
	 * Returns connection from the pool.
	 * @return database connection.
	 */
	public Connection getConnection() {
		Transaction tran = TransactionManager.getTransaction();
		if (tran != null) {
			TransactionalResource resource = tran.getResource();
			if (resource == null) {
				ConnectionProxy proxy = doGetConnection();
				try {
					proxy.setAutoCommit(false);
				}
				catch (SQLException ex) {
					throw new PoolException("Can not start transaction.", ex);
				}
				tran.enlistResource(new PooledConnectionResource(this, proxy));
				return proxy;
			}
			else if (resource instanceof PooledConnectionResource) {
				PooledConnectionResource poolResource = (PooledConnectionResource)resource;
				if (poolResource.isForConnectionPool(this))
					return poolResource.getConnectionProxy();
			}
			throw new IllegalStateException("Distributed transactions are not supported.");
		}
		else
			return doGetConnection();
	}

	/**
	 * Returns connection from the pool.
	 * @param ignoreTransaction if <tt>true</tt> ignores transaction
	 * @return database connection.
	 */
	public Connection getConnection(boolean ignoreTransaction) {
		return ignoreTransaction ? doGetConnection() : getConnection();
	}

	private ConnectionProxy doGetConnection() {
		return new ConnectionProxy(getResource());
	}

	/**
	 * Drops all connections from the pool gracefully.
	 */
	public void dropConnections() {
		releaseResources(true);
	}

	/**
	 * Removes bad connection from the pool. Use this method instead of Connection.close()
	 * to remove bad connections from the pool.
	 * @param conn bad connection to remove from the pool.
	 */
	public void dropConnection(Connection conn) {
		try {
			if (conn instanceof ConnectionProxy)
				((ConnectionProxy)conn).markBad();
			conn.close();
		}
		catch (Throwable th) {
			logError("Error dropping connection.", th);
		}
	}

	/**
	 * Drops all connections from the pool forcefully.
	 */
	public void killConnections() {
		releaseResources(false);
	}

	void returnConnection(org.strangeforest.db.PooledConnection conn) {
		returnResource(conn, false);
	}

	void removeConnection(org.strangeforest.db.PooledConnection conn) {
		removeResource(conn, false);
	}


	// Logging

	public synchronized void setLogger(ConnectionPoolLogger logger) {
		super.setLogger(logger);
		if (isInitialized()) {
			for (PooledResource<org.strangeforest.db.PooledConnection> resource : getPooledResources())
				resource.resource().setLogger(logger);
		}
	}


	// Configuration

	public synchronized Properties getConnectionProperties() {
		ConnectionManager connManager = getConnectionManager();
		return connManager instanceof DriverConnectionManager ? ((DriverConnectionManager)connManager).getProperties() : null;
	}

	public synchronized void setConnectionProperties(Properties properties) {
		ConnectionManager connManager = getConnectionManager();
		if (connManager instanceof DriverConnectionManager)
			((DriverConnectionManager)connManager).setProperties(properties);
	}

	/**
	 * Returns connection initialization query.
	 * @return connection initialization query string.
	 */
	public synchronized String getInitQuery() {
		return initQuery;
	}

	/**
	 * Sets new connection initialization query.
	 * @param newInitQuery connection initialization query string.
	 */
	public synchronized void setInitQuery(String newInitQuery) {
		initQuery = newInitQuery;
	}

	/**
	 * Returns check (ping) query.
	 * @return check query string.
	 */
	public synchronized String getCheckQuery() {
		return checkQuery;
	}

	/**
	 * Sets new check query.
	 * @param newCheckQuery check query string.
	 * @throws SQLException if error occur creating check statements 
	 */
	public synchronized void setCheckQuery(String newCheckQuery) throws SQLException {
		if (!Objects.equals(newCheckQuery, checkQuery)) {
			checkQuery = newCheckQuery;
			if (isInitialized()) {
				for (PooledResource<org.strangeforest.db.PooledConnection> resource : getPooledResources())
					resource.resource().setCheckQuery(checkQuery);
			}
		}
	}

	/**
	 * Returns check timeout in milliseconds.
	 * @return check timeout in milliseconds.
	 */
	public long getCheckTimeout() {
		return checkTimeout;
	}

	/**
	 * Sets new check timeout in milliseconds.
	 * @param checkTimeout check timeout in milliseconds.
	 */
	public void setCheckTimeout(long checkTimeout) {
		this.checkTimeout = checkTimeout;
	}

	/**
	 * Returns maximum number of cached prepared statements per database connection.
	 * @return maximum number of cached prepared statements.
	 */
	public synchronized int getMaxStatements() {
		return maxStatements;
	}

	/**
	 * Sets maximum number of prepared statements in cache per database connection.
	 * @param newMaxStatements maximum number of prepared statements in cache.
	 */
	public synchronized void setMaxStatements(int newMaxStatements) {
		if (newMaxStatements != maxStatements) {
			maxStatements = newMaxStatements;
			if (isInitialized()) {
				for (PooledResource<org.strangeforest.db.PooledConnection> resource : getPooledResources())
					resource.resource().setMaxStatements(maxStatements);
			}
		}
	}

	/**
	 * Gets connection client info.
	 * @return connection client info.
	 */
	public Properties getClientInfo() {
		return clientInfo;
	}

	/**
	 * Sets connection client info.
	 * @param clientInfo connection client info.
	 */
	public void setClientInfo(Properties clientInfo) {
		this.clientInfo = clientInfo;
	}


	// Statistics

	public boolean isCollectStatementStatistics() {
		return collectStatementStatistics;
	}

	public void setCollectStatementStatistics(boolean collectStatementStatistics) {
		this.collectStatementStatistics = collectStatementStatistics;
	}

	public int getTopStatements() {
		return statementStats.getCapacity();
	}

	public void setTopStatements(int count) {
		statementStats.setCapacity(count);
	}

	@Override public synchronized CPStatistics getStatistics() {
		return new CPStatistics(this);
	}

	@Override public synchronized void resetStatistics() {
		super.resetStatistics();
		if (isInitialized()) {
			for (PooledResource<org.strangeforest.db.PooledConnection> resource : getPooledResources()) {
				Cache<Object, PreparedStatementHelper> stCache = resource.resource().getStatementCache();
				if (stCache != null)
					stCache.resetStatistics();
			}
		}
		statementStats.clear();
	}

	final void updateStatementStats(StatementHelper st) {
		if (collectStatementStatistics) {
			synchronized (this) {
				String sql = st.sql;
				StatementStatistics stat = statementStats.get(sql);
				if (stat == null) {
					stat = new StatementStatistics(sql);
					statementStats.put(sql, stat);
				}
				stat.update(st.end - st.start);
			}
		}
	}

	final void updateStatementStats(Cache<Object, PreparedStatementHelper> statements) {
		if (collectStatementStatistics) {
			synchronized (this) {
				int statementCacheSize = statements.size();
				if (statementCacheSize > peakStatementCacheSize)
					peakStatementCacheSize = statementCacheSize;
			}
		}
	}

	public static final class CPStatistics extends Statistics<org.strangeforest.db.PooledConnection> {

		private List<StatementStatistics> statementStats;
		private int peakStatementCacheSize;
		private double statementCacheHitRatio;

		private CPStatistics(ConnectionPool pool) {
			super(pool);
			statementStats = new ArrayList<>(pool.statementStats.values());
			Collections.sort(statementStats);
			peakStatementCacheSize = pool.peakStatementCacheSize;
			long gets = 0, hits = 0;
			for (PooledResource<org.strangeforest.db.PooledConnection> pooledResource : pool.getPooledResources()) {
				Cache<Object, PreparedStatementHelper> stCache = pooledResource.resource().getStatementCache();
				if (stCache != null) {
					CacheStatistics stCacheStats = stCache.getStatistics();
					gets += stCacheStats.gets();
					hits += stCacheStats.hits();
				}
			}
			statementCacheHitRatio = gets != 0L ? ((double)hits)/gets : 0.0;
		}

		@Override protected ResourceInfo createResourceInfo(PooledResource<org.strangeforest.db.PooledConnection> pooledRes, boolean isBusy) {
			return new ConnectionInfo(pooledRes, isBusy);
		}

		public List<StatementStatistics> getStatementStats() {
			return statementStats;
		}

		public int getPeakStatementCacheSize() {
			return peakStatementCacheSize;
		}

		public double getStatementCacheHitRatio() {
			return statementCacheHitRatio;
		}
	}

	public static final class ConnectionInfo extends ResourceInfo {

		private int stCacheSize;
		private int stCacheCapacity;
		private float stCacheHitRatio;
		private float stCacheUsage;

		private ConnectionInfo(PooledResource<org.strangeforest.db.PooledConnection> pooledRes, boolean isBusy) {
			super(pooledRes, isBusy);
			Cache stCache = pooledRes.resource().getStatementCache();
			if (stCache != null) {
				CacheStatistics stCacheStats = stCache.getStatistics();
				stCacheSize = stCacheStats.size();
				stCacheCapacity = stCacheStats.capacity();
				stCacheHitRatio = stCacheStats.hitRatio();
				stCacheUsage = stCacheStats.fillRatio();
			}
		}

		public int getStatementCacheSize() {
			return stCacheSize;
		}

		public int getStatementCacheCapacity() {
			return stCacheCapacity;
		}

		public float getStatementCacheHitRatio() {
			return stCacheHitRatio;
		}

		public float getStatementCacheUsage() {
			return stCacheUsage;
		}
	}

	public final class StatementStatistics implements Serializable, Comparable<StatementStatistics> {

		private String sql;
		private volatile long count;
		private volatile long time;

		public StatementStatistics(String sql) {
			this.sql = sql;
		}

		public String getSql() {
			return sql;
		}

		public long getCount() {
			return count;
		}

		public long getTime() {
			return time;
		}

		public long getAverageTime() {
			return count != 0L ? time/count : 0L;
		}

		public void update(long time) {
			count++;
			this.time += time;
		}

		@Override public int compareTo(StatementStatistics ss) {
			return -PrimitiveUtil.compare(time, ss.time);
		}
	}
}
