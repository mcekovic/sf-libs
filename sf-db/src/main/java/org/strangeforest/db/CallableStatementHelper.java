package org.strangeforest.db;

import java.util.*;
import java.io.*;
import java.math.*;
import java.net.*;
import java.sql.*;

/**
 * <p>This is a helper class that allows setting <tt>CallableStatement</tt> parameters by name.</p>
 * <p>In SQL query text, parameter names are added as suffixes ':' or '?'.
 * <p>For example:</p>
 * <blockquote><pre>
 * Connection conn = ...
 * String sql = "CALL MAKE_DEPOSIT(:accountId, :amount, :newLevel)";
 * CallableStatementHelper st = new CallableStatementHelper(conn, sql);
 * // or CallableStatementHelper st = (CallableStatementHelper)conn.prepareCall(sql); // if conn is from ConnectionPool
 * st.setInt("accountId", accountId);
 * st.setDouble("amount", amount);
 * st.registerOutParameter("newLevel", Types.NUMERIC);
 * st.executeUpdate();
 * double newLevel = st.getDouble("newLevel");
 * st.close();
 * ...
 * </pre></blockquote>
 */
public class CallableStatementHelper extends PreparedStatementHelper implements CallableStatement {

	private CallableStatement cst;

	public CallableStatementHelper(Connection conn, String sql) throws SQLException {
		super(conn, sql);
	}

	public CallableStatementHelper(Connection conn, String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
		super(conn, sql, resultSetType, resultSetConcurrency);
	}

	public CallableStatementHelper(Connection conn, String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
		super(conn, sql, resultSetType, resultSetConcurrency, resultSetHoldability);
	}

	CallableStatementHelper(PooledConnection conn, Object key, String sql) throws SQLException {
		super(conn, key, sql);
	}

	CallableStatementHelper(PooledConnection conn, Object key, String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
		super(conn, key, sql, resultSetType, resultSetConcurrency);
	}

	CallableStatementHelper(PooledConnection conn, Object key, String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
		super(conn, key, sql, resultSetType, resultSetConcurrency, resultSetHoldability);
	}

	@Override protected void createStatement(Connection conn, String sql) throws SQLException {
		this.sql = sql;
		start = System.currentTimeMillis();
		end = 0L;
		cst = conn.prepareCall(parse(sql));
		st = pst = cst;
	}

	@Override protected void createStatement(Connection conn, String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
		this.sql = sql;
		start = System.currentTimeMillis();
		end = 0L;
		cst = conn.prepareCall(parse(sql), resultSetType, resultSetConcurrency);
		st = pst = cst;
	}

	@Override protected void createStatement(Connection conn, String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
		this.sql = sql;
		start = System.currentTimeMillis();
		end = 0L;
		cst = conn.prepareCall(parse(sql), resultSetType, resultSetConcurrency, resultSetHoldability);
		st = pst = cst;
	}


	// CallableStatement methods

	@Override public Array getArray(int index) throws SQLException {
		return cst.getArray(index);
	}

	@Override public BigDecimal getBigDecimal(int index) throws SQLException {
		return cst.getBigDecimal(index);
	}

	@Override public BigDecimal getBigDecimal(int index, int scale) throws SQLException {
		return cst.getBigDecimal(index, scale);
	}

	@Override public Blob getBlob(int index) throws SQLException {
		return cst.getBlob(index);
	}

	@Override public boolean getBoolean(int index) throws SQLException {
		return cst.getBoolean(index);
	}

	@Override public byte getByte(int index) throws SQLException {
		return cst.getByte(index);
	}

	@Override public byte[] getBytes(int index) throws SQLException {
		return cst.getBytes(index);
	}

	@Override public Reader getCharacterStream(int index) throws SQLException {
		return cst.getCharacterStream(index);
	}

	@Override public Clob getClob(int index) throws SQLException {
		return cst.getClob(index);
	}

	@Override public java.sql.Date getDate(int index) throws SQLException {
		return cst.getDate(index);
	}

	@Override public java.sql.Date getDate(int index, Calendar cal) throws SQLException {
		return cst.getDate(index, cal);
	}

	@Override public double getDouble(int index) throws SQLException {
		return cst.getDouble(index);
	}

	@Override public float getFloat(int index) throws SQLException {
		return cst.getFloat(index);
	}

	@Override public int getInt(int index) throws SQLException {
		return cst.getInt(index);
	}

	@Override public long getLong(int index) throws SQLException {
		return cst.getLong(index);
	}

	@Override public Reader getNCharacterStream(int index) throws SQLException {
		return cst.getNCharacterStream(index);
	}

	@Override public NClob getNClob(int index) throws SQLException {
		return cst.getNClob(index);
	}

	@Override public String getNString(int index) throws SQLException {
		return cst.getNString(index);
	}

	@Override public Object getObject(int index) throws SQLException {
		return cst.getObject(index);
	}

	@Override public <T> T getObject(int index, Class<T> type) throws SQLException {
		return cst.getObject(index, type);
	}

	@Override public Object getObject(int index, Map<String, Class<?>> map) throws SQLException {
		return cst.getObject(index, map);
	}

	@Override public Ref getRef(int index) throws SQLException {
		return cst.getRef(index);
	}

	@Override public RowId getRowId(int index) throws SQLException {
		return cst.getRowId(index);
	}

	@Override public short getShort(int index) throws SQLException {
		return cst.getShort(index);
	}

	@Override public String getString(int index) throws SQLException {
		return cst.getString(index);
	}

	@Override public SQLXML getSQLXML(int index) throws SQLException {
		return cst.getSQLXML(index);
	}

	@Override public Time getTime(int index) throws SQLException {
		return cst.getTime(index);
	}

	@Override public Time getTime(int index, Calendar cal) throws SQLException {
		return cst.getTime(index, cal);
	}

	@Override public Timestamp getTimestamp(int index) throws SQLException {
		return cst.getTimestamp(index);
	}

	@Override public Timestamp getTimestamp(int index, Calendar cal) throws SQLException {
		return cst.getTimestamp(index, cal);
	}

	@Override public URL getURL(int index) throws SQLException {
		return cst.getURL(index);
	}

	@Override public void registerOutParameter(int index, int sqlType) throws SQLException {
		cst.registerOutParameter(index, sqlType);
	}

	@Override public void registerOutParameter(int index, int sqlType, String typeName) throws SQLException {
		cst.registerOutParameter(index, sqlType, typeName);
	}

	@Override public void registerOutParameter(int index, int sqlType, int scale) throws SQLException {
		cst.registerOutParameter(index, sqlType, scale);
	}

	@Override public boolean wasNull() throws SQLException {
		return cst.wasNull();
	}

	@Override public void setAsciiStream(String paramName, InputStream x) throws SQLException {
		if (hasParam(paramName))
			super.setAsciiStream(paramName, x);
		else
			cst.setAsciiStream(paramName, x);
	}

	@Override public void setAsciiStream(String paramName, InputStream x, int length) throws SQLException {
		if (hasParam(paramName))
			super.setAsciiStream(paramName, x, length);
		else
			cst.setAsciiStream(paramName, x, length);
	}

	@Override public void setAsciiStream(String paramName, InputStream x, long length) throws SQLException {
		if (hasParam(paramName))
			super.setAsciiStream(paramName, x, length);
		else
			cst.setAsciiStream(paramName, x, length);
	}

	@Override public void setBigDecimal(String paramName, BigDecimal x) throws SQLException {
		if (hasParam(paramName))
			super.setBigDecimal(paramName, x);
		else
			cst.setBigDecimal(paramName, x);
	}


	@Override public void setBinaryStream(String paramName, InputStream x) throws SQLException {
		if (hasParam(paramName))
			super.setBinaryStream(paramName, x);
		else
			cst.setBinaryStream(paramName, x);
	}

	@Override public void setBinaryStream(String paramName, InputStream x, int length) throws SQLException {
		if (hasParam(paramName))
			super.setBinaryStream(paramName, x, length);
		else
			cst.setBinaryStream(paramName, x, length);
	}

	@Override public void setBinaryStream(String paramName, InputStream x, long length) throws SQLException {
		if (hasParam(paramName))
			super.setBinaryStream(paramName, x, length);
		else
			cst.setBinaryStream(paramName, x, length);
	}

	@Override public void setBlob(String paramName, InputStream x) throws SQLException {
		if (hasParam(paramName))
			super.setBlob(paramName, x);
		else
			cst.setBlob(paramName, x);
	}

	@Override public void setBlob(String paramName, InputStream x, long length) throws SQLException {
		if (hasParam(paramName))
			super.setBlob(paramName, x, length);
		else
			cst.setBlob(paramName, x, length);
	}

	@Override public void setBoolean(String paramName, boolean x) throws SQLException {
		if (hasParam(paramName))
			super.setBoolean(paramName, x);
		else
			cst.setBoolean(paramName, x);
	}

	@Override public void setByte(String paramName, byte x) throws SQLException {
		if (hasParam(paramName))
			super.setByte(paramName, x);
		else
			cst.setByte(paramName, x);
	}

	@Override public void setBytes(String paramName, byte[] x) throws SQLException {
		if (hasParam(paramName))
			super.setBytes(paramName, x);
		else
			cst.setBytes(paramName, x);
	}

	@Override public void setCharacterStream(String paramName, Reader x) throws SQLException {
		if (hasParam(paramName))
			super.setCharacterStream(paramName, x);
		else
			cst.setCharacterStream(paramName, x);
	}

	@Override public void setCharacterStream(String paramName, Reader x, int length) throws SQLException {
		if (hasParam(paramName))
			super.setCharacterStream(paramName, x, length);
		else
			cst.setCharacterStream(paramName, x, length);
	}

	@Override public void setCharacterStream(String paramName, Reader x, long length) throws SQLException {
		if (hasParam(paramName))
			super.setCharacterStream(paramName, x, length);
		else
			cst.setCharacterStream(paramName, x, length);
	}

	@Override public void setClob(String paramName, Reader x) throws SQLException {
		if (hasParam(paramName))
			super.setClob(paramName, x);
		else
			cst.setClob(paramName, x);
	}

	@Override public void setClob(String paramName, Reader x, long length) throws SQLException {
		if (hasParam(paramName))
			super.setClob(paramName, x, length);
		else
			cst.setClob(paramName, x, length);
	}

	@Override public void setDate(String paramName, java.sql.Date x) throws SQLException {
		if (hasParam(paramName))
			super.setDate(paramName, x);
		else
			cst.setDate(paramName, x);
	}

	@Override public void setDate(String paramName, java.sql.Date x, Calendar cal) throws SQLException {
		if (hasParam(paramName))
			super.setDate(paramName, x, cal);
		else
			cst.setDate(paramName, x, cal);
	}

	@Override public void setDouble(String paramName, double x) throws SQLException {
		if (hasParam(paramName))
			super.setDouble(paramName, x);
		else
			cst.setDouble(paramName, x);
	}

	@Override public void setFloat(String paramName, float x) throws SQLException {
		if (hasParam(paramName))
			super.setFloat(paramName, x);
		else
			cst.setFloat(paramName, x);
	}

	@Override public void setInt(String paramName, int x) throws SQLException {
		if (hasParam(paramName))
			super.setInt(paramName, x);
		else
			cst.setInt(paramName, x);
	}

	@Override public void setLong(String paramName, long x) throws SQLException {
		if (hasParam(paramName))
			super.setLong(paramName, x);
		else
			cst.setLong(paramName, x);
	}

	@Override public void setNCharacterStream(String paramName, Reader x) throws SQLException {
		if (hasParam(paramName))
			super.setNCharacterStream(paramName, x);
		else
			cst.setNCharacterStream(paramName, x);
	}

	@Override public void setNCharacterStream(String paramName, Reader x, long length) throws SQLException {
		if (hasParam(paramName))
			super.setNCharacterStream(paramName, x, length);
		else
			cst.setNCharacterStream(paramName, x, length);
	}

	@Override public void setNClob(String paramName, NClob x) throws SQLException {
		if (hasParam(paramName))
			super.setNClob(paramName, x);
		else
			cst.setNClob(paramName, x);
	}

	@Override public void setNClob(String paramName, Reader x) throws SQLException {
		if (hasParam(paramName))
			super.setNClob(paramName, x);
		else
			cst.setNClob(paramName, x);
	}

	@Override public void setNClob(String paramName, Reader x, long length) throws SQLException {
		if (hasParam(paramName))
			super.setNClob(paramName, x, length);
		else
			cst.setNClob(paramName, x, length);
	}

	@Override public void setNString(String paramName, String x) throws SQLException {
		if (hasParam(paramName))
			super.setNString(paramName, x);
		else
			cst.setNString(paramName, x);
	}

	@Override public void setNull(String paramName, int sqlType) throws SQLException {
		if (hasParam(paramName))
			super.setNull(paramName, sqlType);
		else
			cst.setNull(paramName, sqlType);
	}

	@Override public void setNull(String paramName, int sqlType, String typeName) throws SQLException {
		if (hasParam(paramName))
			super.setNull(paramName, sqlType, typeName);
		else
			cst.setNull(paramName, sqlType, typeName);
	}

	@Override public void setObject(String paramName, Object x) throws SQLException {
		if (hasParam(paramName))
			super.setObject(paramName, x);
		else
			cst.setObject(paramName, x);
	}

	@Override public void setObject(String paramName, Object x, int targetSqlType) throws SQLException {
		if (hasParam(paramName))
			super.setObject(paramName, x, targetSqlType);
		else
			cst.setObject(paramName, x, targetSqlType);
	}

	@Override public void setObject(String paramName, Object x, int targetSqlType, int scale) throws SQLException {
		if (hasParam(paramName))
			super.setObject(paramName, x, targetSqlType, scale);
		else
			cst.setObject(paramName, x, targetSqlType, scale);
	}

	@Override public void setRowId(String paramName, RowId x) throws SQLException {
		if (hasParam(paramName))
			super.setRowId(paramName, x);
		else
			cst.setRowId(paramName, x);
	}

	@Override public void setShort(String paramName, short x) throws SQLException {
		if (hasParam(paramName))
			super.setShort(paramName, x);
		else
			cst.setShort(paramName, x);
	}

	@Override public void setString(String paramName, String x) throws SQLException {
		if (hasParam(paramName))
			super.setString(paramName, x);
		else
			cst.setString(paramName, x);
	}

	@Override public void setSQLXML(String paramName, SQLXML x) throws SQLException {
		if (hasParam(paramName))
			super.setSQLXML(paramName, x);
		else
			cst.setSQLXML(paramName, x);
	}

	@Override public void setTime(String paramName, Time x) throws SQLException {
		if (hasParam(paramName))
			super.setTime(paramName, x);
		else
			cst.setTime(paramName, x);
	}

	@Override public void setTime(String paramName, Time x, Calendar cal) throws SQLException {
		if (hasParam(paramName))
			super.setTime(paramName, x, cal);
		else
			cst.setTime(paramName, x, cal);
	}

	@Override public void setTimestamp(String paramName, Timestamp x) throws SQLException {
		if (hasParam(paramName))
			super.setTimestamp(paramName, x);
		else
			cst.setTimestamp(paramName, x);
	}

	@Override public void setTimestamp(String paramName, Timestamp x, Calendar cal) throws SQLException {
		if (hasParam(paramName))
			super.setTimestamp(paramName, x, cal);
		else
			cst.setTimestamp(paramName, x, cal);
	}

	@Override public void setURL(String paramName, URL x) throws SQLException {
		if (hasParam(paramName))
			super.setURL(paramName, x);
		else
			cst.setURL(paramName, x);
	}


	// Helper methods

	@Override public Array getArray (String paramName) throws SQLException {
		return cst.getArray(getParamIndex(paramName));
	}

	@Override public BigDecimal getBigDecimal(String paramName) throws SQLException {
		return cst.getBigDecimal(getParamIndex(paramName));
	}

	public BigDecimal getBigDecimal(String paramName, int scale) throws SQLException {
		return cst.getBigDecimal(getParamIndex(paramName), scale);
	}

	@Override public Blob getBlob (String paramName) throws SQLException {
		return cst.getBlob(getParamIndex(paramName));
	}

	@Override public boolean getBoolean(String paramName) throws SQLException {
		return cst.getBoolean(getParamIndex(paramName));
	}

	@Override public byte getByte(String paramName) throws SQLException {
		return cst.getByte(getParamIndex(paramName));
	}

	@Override public byte[] getBytes(String paramName) throws SQLException {
		return cst.getBytes(getParamIndex(paramName));
	}

	@Override public Reader getCharacterStream(String paramName) throws SQLException {
		return cst.getCharacterStream(getParamIndex(paramName));
	}

	@Override public Clob getClob (String paramName) throws SQLException {
		return cst.getClob(getParamIndex(paramName));
	}

	@Override public java.sql.Date getDate(String paramName) throws SQLException {
		return cst.getDate(getParamIndex(paramName));
	}

	@Override public java.sql.Date getDate(String paramName, Calendar cal) throws SQLException {
		return cst.getDate(getParamIndex(paramName), cal);
	}

	@Override public double getDouble(String paramName) throws SQLException {
		return cst.getDouble(getParamIndex(paramName));
	}

	@Override public float getFloat(String paramName) throws SQLException {
		return cst.getFloat(getParamIndex(paramName));
	}

	@Override public int getInt(String paramName) throws SQLException {
		return cst.getInt(getParamIndex(paramName));
	}

	@Override public long getLong(String paramName) throws SQLException {
		return cst.getLong(getParamIndex(paramName));
	}

	@Override public Reader getNCharacterStream(String paramName) throws SQLException {
		return cst.getNCharacterStream(getParamIndex(paramName));
	}

	@Override public NClob getNClob(String paramName) throws SQLException {
		return cst.getNClob(getParamIndex(paramName));
	}

	@Override public String getNString(String paramName) throws SQLException {
		return cst.getNString(getParamIndex(paramName));
	}

	@Override public Object getObject(String paramName) throws SQLException {
		return cst.getObject(getParamIndex(paramName));
	}

	@Override public Object getObject(String paramName, Map<String, Class<?>> map) throws SQLException {
		return cst.getObject(getParamIndex(paramName), map);
	}

	@Override public <T> T getObject(String paramName, Class<T> type) throws SQLException {
		return cst.getObject(paramName, type);
	}

	@Override public Ref getRef (String paramName) throws SQLException {
		return cst.getRef(getParamIndex(paramName));
	}

	@Override public RowId getRowId(String paramName) throws SQLException {
		return cst.getRowId(getParamIndex(paramName));
	}

	@Override public short getShort(String paramName) throws SQLException {
		return cst.getShort(getParamIndex(paramName));
	}

	@Override public String getString(String paramName) throws SQLException {
		return cst.getString(getParamIndex(paramName));
	}

	@Override public SQLXML getSQLXML(String paramName) throws SQLException {
		return cst.getSQLXML(getParamIndex(paramName));
	}

	@Override public Time getTime(String paramName) throws SQLException {
		return cst.getTime(getParamIndex(paramName));
	}

	@Override public Time getTime(String paramName, Calendar cal) throws SQLException {
		return cst.getTime(getParamIndex(paramName), cal);
	}

	@Override public Timestamp getTimestamp(String paramName) throws SQLException {
		return cst.getTimestamp(getParamIndex(paramName));
	}

	@Override public Timestamp getTimestamp(String paramName, Calendar cal) throws SQLException {
		return cst.getTimestamp(getParamIndex(paramName), cal);
	}

	@Override public URL getURL(String paramName) throws SQLException {
		return cst.getURL(getParamIndex(paramName));
	}

	@Override public void registerOutParameter(String paramName, int sqlType) throws SQLException {
		cst.registerOutParameter(getOutputParamIndex(paramName), sqlType);
	}

	@Override public void registerOutParameter(String paramName, int sqlType, String typeName) throws SQLException {
		cst.registerOutParameter(getOutputParamIndex(paramName), sqlType, typeName);
	}

	@Override public void registerOutParameter(String paramName, int sqlType, int scale) throws SQLException {
		cst.registerOutParameter(getOutputParamIndex(paramName), sqlType, scale);
	}


	// Misc

	@Override protected void formatParam(StringBuilder sb, ParamInfo info) {
		if (info.out) {
			sb.append(info.name);
			sb.append("[out]");
			sb.append('=');
			try {
				formatParamValue(sb, getObject(info.name), info.masked);
			}
			catch (SQLException ex) {
				sb.append(ex);
			}
		}
		else
			super.formatParam(sb, info);
	}

	@Override protected void logStatement() {
		if (logger != null)
			logger.logCallableStatement(this);
		updateStats();
	}
}
