package org.strangeforest.db;

import java.sql.*;
import java.util.*;

import org.strangeforest.pool.*;

class DriverConnectionManager extends ConnectionManager {

	private final String driverClass, dbURL;
	private Properties properties;
	private Properties propertiesEx;

	DriverConnectionManager(ConnectionPool pool, String driverClass, String dbURL, String username, String password, Properties properties) {
		super(pool, username, password);
		this.driverClass = driverClass;
		this.dbURL = dbURL;
		setProperties(properties);
		try {
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			if (loader != null)
				Class.forName(driverClass, true, loader);
			else
				Class.forName(driverClass);
		}
		catch (Exception ex) {
			throw new PoolException("Error registering JDBC driver: " + driverClass, ex);
		}
	}

	public String getDriverClass() {
		return driverClass;
	}

	public String getDbURL() {
		return dbURL;
	}

	public Properties getProperties() {
		return properties;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
		if (properties != null && (username != null || password != null)) {
			propertiesEx = new Properties(properties);
			if (username != null)
				propertiesEx.setProperty("user", username);
			if (password != null)
				propertiesEx.setProperty("password", password);
		}
		else
			propertiesEx = properties;
	}

	@Override protected Connection allocateConnection() throws SQLException {
		if (username == null && password == null) {
			if (properties == null)
				return DriverManager.getConnection(dbURL);
			else
				return DriverManager.getConnection(dbURL, properties);
		}
		else {
			if (propertiesEx == null)
				return DriverManager.getConnection(dbURL, username, password);
			else
				return DriverManager.getConnection(dbURL, propertiesEx);
		}
	}
}
