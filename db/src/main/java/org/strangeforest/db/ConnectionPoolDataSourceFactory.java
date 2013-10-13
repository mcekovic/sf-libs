package org.strangeforest.db;

import java.util.*;
import javax.naming.*;
import javax.naming.spi.*;

import static org.strangeforest.pool.ResourcePool.*;

public class ConnectionPoolDataSourceFactory implements ObjectFactory {

	@Override public Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable<?, ?> environment) throws Exception {
		if (obj instanceof Reference) {
			Reference reference = (Reference)obj;
			String clsName = reference.getClassName();
			if (clsName.equals(ConnectionPoolDataSource.class.getName())) {

				// Required properties
				RefAddr addr = reference.get("driverClass");
				if (addr == null)
					throw new NamingException("Unspecified 'driverClass'.");
				String driverClass = (String)addr.getContent();

				addr = reference.get("dbURL");
				if (addr == null)
					throw new NamingException("Unspecified 'dbURL'.");
				String dbURL = (String)addr.getContent();

				addr = reference.get("username");
				if (addr == null)
					throw new NamingException("Unspecified 'username'.");
				String username = (String)addr.getContent();

				addr = reference.get("password");
				if (addr == null)
					throw new NamingException("Unspecified 'password'.");
				String password = (String)addr.getContent();

				// Optional properties
				addr = reference.get("initialPoolSize");
				int initialPoolSize = addr != null ? Integer.parseInt((String)addr.getContent()) : DEFAULT_INITIAL_POOL_SIZE;
				addr = reference.get("minPoolSize");
				int minPoolSize = addr != null ? Integer.parseInt((String)addr.getContent()) : DEFAULT_MIN_POOL_SIZE;
				addr = reference.get("maxPoolSize");
				int maxPoolSize = addr != null ? Integer.parseInt((String)addr.getContent()) : DEFAULT_MAX_POOL_SIZE;
				addr = reference.get("minIdleCount");
				int minIdleCount = addr != null ? Integer.parseInt((String)addr.getContent()) : DEFAULT_MIN_IDLE_COUNT;
				addr = reference.get("maxPendingCount");
				int maxPendingCount = addr != null ? Integer.parseInt((String)addr.getContent()) : DEFAULT_MAX_PENDING_COUNT;
				addr = reference.get("initQuery");
				String initQuery = addr != null ? (String)addr.getContent() : ConnectionPool.DEFAULT_INIT_QUERY;
				addr = reference.get("checkQuery");
				String checkQuery = addr != null ? (String)addr.getContent() : ConnectionPool.DEFAULT_CHECK_QUERY;
				addr = reference.get("checkTimeout");
				long checkTimeout = addr != null ? 1000L*Integer.parseInt((String)addr.getContent()) : ConnectionPool.DEFAULT_CHECK_TIMEOUT;
				addr = reference.get("checkTime");
				long checkTime = addr != null ? 1000L*Integer.parseInt((String)addr.getContent()) : DEFAULT_CHECK_TIME;
				addr = reference.get("maxWaitTime");
				long maxWaitTime = addr != null ? 1000L*Integer.parseInt((String)addr.getContent()) : DEFAULT_MAX_WAIT_TIME;
				addr = reference.get("maxIdleTime");
				long maxIdleTime = addr != null ? 1000L*Integer.parseInt((String)addr.getContent()) : DEFAULT_MAX_IDLE_TIME;
				addr = reference.get("maxBusyTime");
				long maxBusyTime = addr != null ? 1000L*Integer.parseInt((String)addr.getContent()) : DEFAULT_MAX_BUSY_TIME;
				addr = reference.get("maxLiveTime");
				long maxLiveTime = addr != null ? 1000L*Integer.parseInt((String)addr.getContent()) : DEFAULT_MAX_LIVE_TIME;
				addr = reference.get("propertyCycle");
				long propertyCycle = addr != null ? 1000L*Integer.parseInt((String)addr.getContent()) : DEFAULT_PROPERTY_CYCLE;
				addr = reference.get("maxStatements");
				int maxStatements = addr != null ? Integer.parseInt((String)addr.getContent()) : ConnectionPool.DEFAULT_MAX_STATEMENTS;

				ConnectionPoolDataSource dataSource = new ConnectionPoolDataSource(
					driverClass, dbURL, username, password,
					initialPoolSize, minPoolSize, maxPoolSize,
					initQuery, checkQuery
				);
				dataSource.setMinIdleCount(minIdleCount);
				dataSource.setMaxPendingCount(maxPendingCount);
				dataSource.setCheckTimeout(checkTimeout);
				dataSource.setCheckTime(checkTime);
				dataSource.setMaxWaitTime(maxWaitTime);
				dataSource.setMaxIdleTime(maxIdleTime);
				dataSource.setMaxWaitTime(maxBusyTime);
				dataSource.setMaxLiveTime(maxLiveTime);
				dataSource.setPropertyCycle(propertyCycle);
				dataSource.setMaxStatements(maxStatements);
				dataSource.init();
				return dataSource;
			}
			else
				throw new ClassCastException(clsName);
		}
		else
			return null;
	}
}
