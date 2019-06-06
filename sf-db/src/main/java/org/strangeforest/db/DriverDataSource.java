package org.strangeforest.db;

import java.io.*;
import java.sql.*;
import java.util.logging.*;
import javax.sql.*;

/**
 * <p><tt>DriverPoolDataSource</tt> is an <i>adapter</i> for <tt>DriverManager</tt> that implements <tt>DataSource</tt> interface.</p>
 * @see DataSource
 */
public class DriverDataSource implements DataSource {

	private String url;
	private String username;
	private String password;

	public DriverDataSource(String driverClassName, String url, String username, String password) throws ClassNotFoundException {
		super();
		Class.forName(driverClassName);
		this.url = url;
		this.username = username;
		this.password = password;
	}

	@Override public Connection getConnection() throws SQLException {
		return DriverManager.getConnection(url, username, password);
	}

	@Override public Connection getConnection(String username, String password) throws SQLException {
		return DriverManager.getConnection(url, username, password);
	}

	@Override public PrintWriter getLogWriter() {
		return DriverManager.getLogWriter();
	}

	@Override public void setLogWriter(PrintWriter writer) {
		DriverManager.setLogWriter(writer);
	}

	@Override public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override public int getLoginTimeout() {
		return DriverManager.getLoginTimeout();
	}

	@Override public void setLoginTimeout(int timeout) {
		DriverManager.setLoginTimeout(timeout);
	}

	@Override public <T> T unwrap(Class<T> iface) throws SQLException {
		throw new SQLException("Cannot unwrap to " + iface.getName());
	}

	@Override public boolean isWrapperFor(Class<?> iface) {
		return false;
	}
}