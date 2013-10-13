package org.strangeforest.db;

import java.io.*;
import java.sql.*;
import java.text.*;
import java.util.*;
import java.util.logging.*;
import javax.sql.*;

import org.strangeforest.pool.*;

/**
 * <p><tt>ConnectionPoolDataSource</tt> is an <i>adapter</i> for <tt>ConnectionPool</tt> that implements <tt>DataSource</tt> interface.</p>
 * @see DataSource
 */
public class ConnectionPoolDataSource implements DataSource, Closeable {

	private ConnectionPool pool;

	public ConnectionPoolDataSource(String driverClass, String dbURL, String username, String password) {
		super();
		pool = new ConnectionPool(driverClass, dbURL, username, password);
	}

	public ConnectionPoolDataSource(String driverClass, String dbURL, String username, String password,
											  int initialPoolSize, int minPoolSize, int maxPoolSize,
											  String initQuery, String checkQuery) {
		super();
		pool = new ConnectionPool(driverClass, dbURL, username, password, initialPoolSize, minPoolSize, maxPoolSize, initQuery, checkQuery);
	}

	public void init() {
		pool.init();
	}

	@Override public Connection getConnection() throws SQLException {
		try {
			return pool.getConnection();
		}
		catch (Throwable th) {
			Throwable cause = th.getCause();
			throw new SQLException("Error getting connection.", cause instanceof SQLException ? cause : th);
		}
	}

	@Override public Connection getConnection(String username, String password) throws SQLException {
		if (pool.getConnectionManager().checkCredentials(username, password))
			return getConnection();
		else
			throw new PoolException(MessageFormat.format("Username {0} and supplied password are not supported by this pool.", username));
	}

	@Override public PrintWriter getLogWriter() throws SQLException {
		return DriverManager.getLogWriter();
	}

	@Override public void setLogWriter(PrintWriter writer) throws SQLException {
		DriverManager.setLogWriter(writer);
	}

	@Override public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override public int getLoginTimeout() throws SQLException {
		return DriverManager.getLoginTimeout();
	}

	@Override public void setLoginTimeout(int timeout) throws SQLException {
		DriverManager.setLoginTimeout(timeout);
	}

	@Override public <T> T unwrap(Class<T> iface) throws SQLException {
		if (iface.isInstance(this))
			return (T)this;
		else
			throw new SQLException("Cannot unwrap to " + iface);
	}

	@Override public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return iface.isInstance(this);
	}

	@Override public void close() {
		pool.destroy();
	}


	// Properties

	public void setConnectionProperties(Properties properties) {
		pool.setConnectionProperties(properties);
	}

	public Properties getConnectionProperties() {
		return pool.getConnectionProperties();
	}

	public int getInitialPoolSize() {
		return pool.getInitialPoolSize();
	}

	public void setInitialPoolSize(int initialPoolSize) {
		pool.setInitialPoolSize(initialPoolSize);
	}

	public int getMinPoolSize() {
		return pool.getMinPoolSize();
	}

	public void setMinPoolSize(int minPoolSize) {
		pool.setMinPoolSize(minPoolSize);
	}

	public int getMaxPoolSize() {
		return pool.getMaxPoolSize();
	}

	public void setMaxPoolSize(int maxPoolSize) {
		pool.setMaxPoolSize(maxPoolSize);
	}

	public int getMinIdleCount() {
		return pool.getMinIdleCount();
	}

	public void setMinIdleCount(int minIdleCount) {
		pool.setMinIdleCount(minIdleCount);
	}

	public int getMaxPendingCount() {
		return pool.getMaxPendingCount();
	}

	public void setMaxPendingCount(int maxPendingCount) {
		pool.setMaxPendingCount(maxPendingCount);
	}

	public String getInitQuery() {
		return pool.getInitQuery();
	}

	public void setInitQuery(String initQuery) throws SQLException {
		pool.setInitQuery(initQuery);
	}

	public String getCheckQuery() {
		return pool.getCheckQuery();
	}

	public void setCheckQuery(String checkQuery) throws SQLException {
		pool.setCheckQuery(checkQuery);
	}

	public long getCheckTimeout() {
		return pool.getCheckTimeout();
	}

	public void setCheckTimeout(long checkTimeout) {
		pool.setCheckTimeout(checkTimeout);
	}

	public long getCheckTime() {
		return pool.getCheckTime();
	}

	public void setCheckTime(long checkTime) {
		pool.setCheckTime(checkTime);
	}

	public long getMaxWaitTime() {
		return pool.getMaxWaitTime();
	}

	public void setMaxWaitTime(long maxWaitTime) {
		pool.setMaxWaitTime(maxWaitTime);
	}

	public long getMaxIdleTime() {
		return pool.getMaxIdleTime();
	}

	public void setMaxIdleTime(long maxIdleTime) {
		pool.setMaxIdleTime(maxIdleTime);
	}

	public long getMaxBusyTime() {
		return pool.getMaxBusyTime();
	}

	public void setMaxBusyTime(long maxBusyTime) {
		pool.setMaxBusyTime(maxBusyTime);
	}

	public long getMaxLiveTime() {
		return pool.getMaxLiveTime();
	}

	public void setMaxLiveTime(long maxLiveTime) {
		pool.setMaxLiveTime(maxLiveTime);
	}

	public long getPropertyCycle() {
		return (pool.getPropertyCycle());
	}

	public void setPropertyCycle(long propertyCycle) {
		pool.setPropertyCycle(propertyCycle);
	}

	public int getMaxStatements() {
		return pool.getMaxStatements();
	}

	public void setMaxStatements(int maxStatements) {
		pool.setMaxStatements(maxStatements);
	}

	public Properties getClientInfo() {
		return pool.getClientInfo();
	}

	public void setClientInfo(Properties clientInfo) {
		pool.setClientInfo(clientInfo);
	}

	public ConnectionPoolLogger getLogger() {
		return (ConnectionPoolLogger)pool.getLogger();
	}

	public void setLogger(ConnectionPoolLogger logger) {
		pool.setLogger(logger);
	}


	// Statistics

	public Map<String, Object> getStatistics() {
		ConnectionPool.CPStatistics stats = pool.getStatistics();
		Map<String, Object> statsMap = new LinkedHashMap<>();
		statsMap.put("InitializationTime", stats.getInitializationTime());
		statsMap.put("Size", stats.getSize());
		statsMap.put("PeakSize", stats.getPeakSize());
		statsMap.put("PeakTime", stats.getPeakTime());
		statsMap.put("BusyCount", stats.getBusyCount());
		statsMap.put("IdleCount", stats.getIdleCount());
		statsMap.put("AllocationCount", stats.getAllocationCount());
		statsMap.put("ReleaseCount", stats.getReleaseCount());
		statsMap.put("PendingCount", stats.getPendingCount());
		statsMap.put("AllocationTime", stats.getAllocationTime());
		statsMap.put("ReleaseTime", stats.getReleaseTime());
		statsMap.put("WaitTime", stats.getWaitTime());
		statsMap.put("GetCount", stats.getGetCount());
		statsMap.put("ReturnCount", stats.getReturnCount());
		statsMap.put("Efficiency", stats.getEfficiency());
		statsMap.put("CheckCount", stats.getCheckCount());
		statsMap.put("CheckRatio", stats.getCheckRatio());
		statsMap.put("PeakStatementCacheSize", stats.getPeakStatementCacheSize());
		statsMap.put("StatementCacheHitRatio", stats.getStatementCacheHitRatio());
		statsMap.put("FailedAllocationCount", stats.getFailedAllocationCount());
		statsMap.put("FailedCheckCount", stats.getFailedCheckCount());
		statsMap.put("FailedGetCount", stats.getFailedGetCount());
		statsMap.put("BusyTimeoutCount", stats.getBusyTimeoutCount());
		List<Map<String, Object>> connList = new ArrayList<>();
		int index = 1;
		for (Object o : stats.getResourceInfos()) {
			ConnectionPool.ConnectionInfo info = (ConnectionPool.ConnectionInfo)o;
			Map<String, Object> infoMap = new LinkedHashMap<>();
			infoMap.put("Index", index++);
			infoMap.put("IsBusy", info.isBusy());
			infoMap.put("AllocationTime", info.getAllocationTime());
			infoMap.put("LastCheckTime", info.getLastCheckTime());
			infoMap.put("LastGetTime", info.getLastGetTime());
			infoMap.put("LastReturnTime", info.getLastReturnTime());
			infoMap.put("StatementCacheCapacity", info.getStatementCacheCapacity());
			infoMap.put("StatementCacheSize", info.getStatementCacheSize());
			infoMap.put("StatementCacheUsage", info.getStatementCacheUsage());
			infoMap.put("StatementCacheHitRatio", info.getStatementCacheHitRatio());
			infoMap.put("Description", info.getDescription());
			connList.add(infoMap);
		}
		statsMap.put("Connections", connList);
		List<Map<String, Object>> stList = new ArrayList<>();
		for (ConnectionPool.StatementStatistics stStat : stats.getStatementStats()) {
			Map<String, Object> stMap = new LinkedHashMap<>();
			stMap.put("SQL", stStat.getSql());
			stMap.put("Executions", stStat.getCount());
			stMap.put("Time", stStat.getTime());
			stMap.put("AverageTime", stStat.getAverageTime());
			stList.add(stMap);
		}
		statsMap.put("Statements", stList);
		return statsMap;
	}

	public boolean isCollectStatementStatistics() {
		return pool.isCollectStatementStatistics();
	}

	public void setCollectStatementStatistics(boolean collectStatementStatistics) {
		pool.setCollectStatementStatistics(collectStatementStatistics);
	}

	public int getTopStatements() {
		return pool.getTopStatements();
	}

	public void setTopStatements(int count) {
		pool.setTopStatements(count);
	}

	public void resetStatistics() {
		pool.resetStatistics();
	}


	// Management

	public void dropConnections() {
		pool.dropConnections();
	}

	public void killConnections() {
		pool.killConnections();
	}

	public void dropConnection(Connection conn) {
		pool.dropConnection(conn);
	}
}
