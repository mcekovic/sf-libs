package org.strangeforest.db.gateway;

import java.io.*;
import java.math.*;
import java.sql.*;
import java.util.*;
import javax.xml.parsers.*;
import javax.xml.transform.Transformer;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;

import org.strangeforest.concurrent.*;
import org.strangeforest.db.*;
import org.strangeforest.util.*;
import org.w3c.dom.*;
import org.xml.sax.*;

public abstract class DataHelper {

	// ResultSet reading

	public static String getString(ResultSet rs, String columnName) throws SQLException {
		return rs.getString(columnName);
	}

	public static String getNullableString(ResultSet rs, String columnName) throws SQLException {
		String s = rs.getString(columnName);
		return rs.wasNull() ? null : s;
	}

	public static <E extends Enum<E>> E getEnum(ResultSet rs, String columnName, Class<E> cls) throws SQLException {
		return Enum.valueOf(cls, rs.getString(columnName));
	}

	public static <E extends Enum<E>> E getNullableEnum(ResultSet rs, String columnName, Class<E> cls) throws SQLException {
		String s = rs.getString(columnName);
		return rs.wasNull() ? null : Enum.valueOf(cls, s);
	}

	public static long getLong(ResultSet rs, String columnName) throws SQLException {
		return rs.getLong(columnName);
	}

	public static Long getNullableLong(ResultSet rs, String columnName) throws SQLException {
		long l = rs.getLong(columnName);
		return rs.wasNull() ? null : l;
	}

	public static int getInt(ResultSet rs, String columnName) throws SQLException {
		return rs.getInt(columnName);
	}

	public static Integer getNullableInteger(ResultSet rs, String columnName) throws SQLException {
		int i = rs.getInt(columnName);
		return rs.wasNull() ? null : i;
	}

	public static short getShort(ResultSet rs, String columnName) throws SQLException {
		return rs.getShort(columnName);
	}

	public static Short getNullableShort(ResultSet rs, String columnName) throws SQLException {
		short s = rs.getShort(columnName);
		return rs.wasNull() ? null : s;
	}

	public static byte getByte(ResultSet rs, String columnName) throws SQLException {
		return rs.getByte(columnName);
	}

	public static Byte getNullableByte(ResultSet rs, String columnName) throws SQLException {
		byte b = rs.getByte(columnName);
		return rs.wasNull() ? null : b;
	}

	public static boolean getBoolean(ResultSet rs, String columnName) throws SQLException {
		return rs.getInt(columnName) != 0;
	}

	public static Boolean getNullableBoolean(ResultSet rs, String columnName) throws SQLException {
		int b = rs.getInt(columnName);
		return rs.wasNull() ? null : b != 0;
	}

	public static double getDouble(ResultSet rs, String columnName) throws SQLException {
		return rs.getDouble(columnName);
	}

	public static Double getNullableDouble(ResultSet rs, String columnName) throws SQLException {
		double d = rs.getDouble(columnName);
		return rs.wasNull() ? null : d;
	}

	public static float getFloat(ResultSet rs, String columnName) throws SQLException {
		return rs.getFloat(columnName);
	}

	public static Float getNullableFloat(ResultSet rs, String columnName) throws SQLException {
		float f = rs.getFloat(columnName);
		return rs.wasNull() ? null : f;
	}

	public static BigDecimal getBigDecimal(ResultSet rs, String columnName) throws SQLException {
		return rs.getBigDecimal(columnName);
	}

	public static BigDecimal getNullableBigDecimal(ResultSet rs, String columnName) throws SQLException {
		BigDecimal d = rs.getBigDecimal(columnName);
		return rs.wasNull() ? null : d;
	}

	public static java.util.Date getTimestamp(ResultSet rs, String columnName) throws SQLException {
		Timestamp ts = rs.getTimestamp(columnName);
		return ts != null ? new java.util.Date(ts.getTime()) : null;
	}

	public static java.util.Date getNullableTimestamp(ResultSet rs, String columnName) throws SQLException {
		Timestamp ts = rs.getTimestamp(columnName);
		return rs.wasNull() ? null : new java.util.Date(ts.getTime());
	}

	public static Calendar getCalendar(ResultSet rs, String columnName) throws SQLException {
		Timestamp ts = rs.getTimestamp(columnName);
		if (ts != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(ts);
			return cal;
		}
		else
			return null;
	}

	public static Calendar getNullableCalendar(ResultSet rs, String columnName) throws SQLException {
		Timestamp ts = rs.getTimestamp(columnName);
		if (rs.wasNull())
			return null;
		else {
			Calendar cal = Calendar.getInstance();
			cal.setTime(ts);
			return cal;
		}
	}

	public static java.util.Date getDate(ResultSet rs, String columnName) throws SQLException {
		java.sql.Date d = rs.getDate(columnName);
		return d != null ? new java.util.Date(d.getTime()) : null;
	}

	public static java.util.Date getNullableDate(ResultSet rs, String columnName) throws SQLException {
		java.sql.Date d = rs.getDate(columnName);
		return rs.wasNull() ? null : new java.util.Date(d.getTime());
	}

	public static java.util.Date getTime(ResultSet rs, String columnName) throws SQLException {
		Time t = rs.getTime(columnName);
		return t != null ? new java.util.Date(t.getTime()) : null;
	}

	public static java.util.Date getNullableTime(ResultSet rs, String columnName) throws SQLException {
		Time t = rs.getTime(columnName);
		return rs.wasNull() ? null : new java.util.Date(t.getTime());
	}

	public static Object getArray(ResultSet rs, String columnName) throws SQLException {
		Array a = rs.getArray(columnName);
		return a != null ? a.getArray() : null;
	}

	public static Object getNullableArray(ResultSet rs, String columnName) throws SQLException {
		Array a = rs.getArray(columnName);
		return rs.wasNull() ? null : a.getArray();
	}

	public static Document getXML(ResultSet rs, String columnName) throws SQLException {
		Clob c = rs.getClob(columnName);
		return c != null ? parse(c) : null;
	}

	public static Document getNullableXML(ResultSet rs, String columnName) throws SQLException {
		Clob c = rs.getClob(columnName);
		return rs.wasNull() ? null : parse(c);
	}

	public static Node getSQLXML(ResultSet rs, String columnName) throws SQLException {
		SQLXML sqlxml = rs.getSQLXML(columnName);
		return sqlxml != null ? sqlxml.getSource(DOMSource.class).getNode() : null;
	}

	public static Node getNullableSQLXML(ResultSet rs, String columnName) throws SQLException {
		SQLXML sqlxml = rs.getSQLXML(columnName);
		return rs.wasNull() ? null : sqlxml.getSource(DOMSource.class).getNode();
	}

	public static <T> List<T> getList(ResultSet rs, String paramName, ObjectReader<T> reader) throws SQLException {
		return getList((ResultSet)rs.getObject(paramName), reader);
	}

	public static void readResultSet(ResultSet rs, String paramName, ResultSetReader reader) throws SQLException {
		readResultSet((ResultSet)rs.getObject(paramName), reader);
	}

	public static <T> void streamResultSet(ResultSet rs, String paramName, ObjectReader<T> reader, StreamingIterator<T> iterator) throws SQLException {
		streamResultSet((ResultSet)rs.getObject(paramName), reader, iterator);
	}

	public static boolean hasColumn(ResultSet rs, String columnName) throws SQLException {
		ResultSetMetaData metadata = rs.getMetaData();
		int count = metadata.getColumnCount();
		for (int i = 1; i <= count; i++) {
			if (metadata.getColumnName(i).equalsIgnoreCase(columnName))
				return true;
		}
		return false;
	}


	// Statement preparing

	public static void setNull(PreparedStatementHelper st, String paramName) throws SQLException {
		st.setNull(paramName, st.getType(paramName));
	}

	public static void setString(PreparedStatementHelper st, String paramName, String paramValue) throws SQLException {
		if (paramValue != null)
			st.setString(paramName, paramValue);
		else
			st.setNull(paramName, Types.VARCHAR);
	}

	public static void setString(PreparedStatementHelper st, String paramName, String paramValue, int maxLength) throws SQLException {
		if (paramValue != null)
			st.setString(paramName, paramValue.length() <= maxLength ?  paramValue : paramValue.substring(0, maxLength));
		else
			st.setNull(paramName, Types.VARCHAR);
	}

	public static void setEnum(PreparedStatementHelper st, String paramName, Enum paramValue) throws SQLException {
		if (paramValue != null)
			st.setString(paramName, paramValue.toString());
		else
			st.setNull(paramName, Types.VARCHAR);
	}

	public static void setLong(PreparedStatementHelper st, String paramName, Long paramValue) throws SQLException {
		if (paramValue != null)
			st.setLong(paramName, paramValue);
		else
			st.setNull(paramName, Types.NUMERIC);
	}

	public static void setLong(PreparedStatementHelper st, String paramName, long paramValue) throws SQLException {
		st.setLong(paramName, paramValue);
	}

	public static void setInteger(PreparedStatementHelper st, String paramName, Integer paramValue) throws SQLException {
		if (paramValue != null)
			st.setInt(paramName, paramValue);
		else
			st.setNull(paramName, Types.NUMERIC);
	}

	public static void setInt(PreparedStatementHelper st, String paramName, int paramValue) throws SQLException {
		st.setInt(paramName, paramValue);
	}

	public static void setShort(PreparedStatementHelper st, String paramName, Short paramValue) throws SQLException {
		if (paramValue != null)
			st.setShort(paramName, paramValue);
		else
			st.setNull(paramName, Types.NUMERIC);
	}

	public static void setShort(PreparedStatementHelper st, String paramName, short paramValue) throws SQLException {
		st.setShort(paramName, paramValue);
	}

	public static void setByte(PreparedStatementHelper st, String paramName, Byte paramValue) throws SQLException {
		if (paramValue != null)
			st.setByte(paramName, paramValue);
		else
			st.setNull(paramName, Types.NUMERIC);
	}

	public static void setByte(PreparedStatementHelper st, String paramName, byte paramValue) throws SQLException {
		st.setByte(paramName, paramValue);
	}

	public static void setBoolean(PreparedStatementHelper st, String paramName, Boolean paramValue) throws SQLException {
		if (paramValue != null)
			st.setInt(paramName, paramValue ? -1 : 0);
		else
			st.setNull(paramName, Types.NUMERIC);
	}

	public static void setBoolean(PreparedStatementHelper st, String paramName, boolean paramValue) throws SQLException {
		st.setInt(paramName, paramValue ? -1 : 0);
	}

	public static void setDouble(PreparedStatementHelper st, String paramName, Double paramValue) throws SQLException {
		if (paramValue != null)
			st.setDouble(paramName, paramValue);
		else
			st.setNull(paramName, Types.NUMERIC);
	}

	public static void setDouble(PreparedStatementHelper st, String paramName, double paramValue) throws SQLException {
		st.setDouble(paramName, paramValue);
	}

	public static void setFloat(PreparedStatementHelper st, String paramName, Float paramValue) throws SQLException {
		if (paramValue != null)
			st.setFloat(paramName, paramValue);
		else
			st.setNull(paramName, Types.NUMERIC);
	}

	public static void setFloat(PreparedStatementHelper st, String paramName, float paramValue) throws SQLException {
		st.setFloat(paramName, paramValue);
	}

	public static void setDecimal(PreparedStatementHelper st, String paramName, BigDecimal paramValue) throws SQLException {
		if (paramValue != null)
			st.setBigDecimal(paramName, paramValue);
		else
			st.setNull(paramName, Types.NUMERIC);
	}

	public static void setTimestamp(PreparedStatementHelper st, String paramName, java.util.Date paramValue) throws SQLException {
		if (paramValue != null)
			st.setTimestamp(paramName, new Timestamp(paramValue.getTime()));
		else
			st.setNull(paramName, Types.TIMESTAMP);
	}

	public static void setTimestamp(PreparedStatementHelper st, String paramName, Calendar paramValue) throws SQLException {
		if (paramValue != null)
			st.setTimestamp(paramName, new Timestamp(paramValue.getTimeInMillis()), paramValue);
		else
			st.setNull(paramName, Types.TIMESTAMP);
	}

	public static void setDate(PreparedStatementHelper st, String paramName, java.util.Date paramValue) throws SQLException {
		if (paramValue != null)
			st.setDate(paramName, new java.sql.Date(paramValue.getTime()));
		else
			st.setNull(paramName, Types.DATE);
	}

	public static void setDate(PreparedStatementHelper st, String paramName, Calendar paramValue) throws SQLException {
		if (paramValue != null)
			st.setDate(paramName, new java.sql.Date(paramValue.getTimeInMillis()), paramValue);
		else
			st.setNull(paramName, Types.DATE);
	}

	public static void setTime(PreparedStatementHelper st, String paramName, java.util.Date paramValue) throws SQLException {
		if (paramValue != null)
			st.setTime(paramName, new Time(paramValue.getTime()));
		else
			st.setNull(paramName, Types.TIME);
	}

	public static void setTime(PreparedStatementHelper st, String paramName, Calendar paramValue) throws SQLException {
		if (paramValue != null)
			st.setTime(paramName, new Time(paramValue.getTimeInMillis()), paramValue);
		else
			st.setNull(paramName, Types.TIME);
	}

	public static void setArray(PreparedStatementHelper st, String paramName, String typeName, Object[] paramValue) throws SQLException {
		if (paramValue != null)
			st.setArray(paramName, st.getConnection().createArrayOf(typeName, paramValue));
		else
			st.setNull(paramName, Types.ARRAY);
	}

	public static void setXML(PreparedStatementHelper st, String paramName, Node paramValue) throws SQLException {
		if (paramValue != null)
			st.setClob(paramName, serialize(st.getConnection(), paramValue));
		else
			st.setNull(paramName, Types.CLOB);
	}

	public static void setSQLXML(PreparedStatementHelper st, String paramName, Node paramValue) throws SQLException {
		if (paramValue != null) {
			SQLXML sqlxml = st.getConnection().createSQLXML();
			sqlxml.setResult(DOMResult.class).setNode(paramValue);
			st.setSQLXML(paramName, sqlxml);
		}
		else
			st.setNull(paramName, Types.SQLXML);
	}

	public static void setObject(PreparedStatementHelper st, String paramName, Object paramValue) throws SQLException {
		if (!trySetObject(st, paramName, paramValue))
			st.setString(paramName, paramValue.toString());
	}

	static boolean trySetObject(PreparedStatementHelper st, String paramName, Object paramValue) throws SQLException {
		if (paramValue == null) {
			st.setNull(paramName, st.getType(paramName));
			return true;
		}
		else if (paramValue instanceof String) {
			st.setString(paramName, (String)paramValue);
			return true;
		}
		else if (paramValue instanceof Long) {
			st.setLong(paramName, (Long)paramValue);
			return true;
		}
		else if (paramValue instanceof Integer) {
			st.setInt(paramName, (Integer)paramValue);
			return true;
		}
		else if (paramValue instanceof Short) {
			st.setShort(paramName, (Short)paramValue);
			return true;
		}
		else if (paramValue instanceof Byte) {
			st.setByte(paramName, (Byte)paramValue);
			return true;
		}
		else if (paramValue instanceof Boolean) {
			st.setInt(paramName, (Boolean)paramValue ? -1 : 0);
			return true;
		}
		else if (paramValue instanceof BigDecimal) {
			st.setBigDecimal(paramName, (BigDecimal)paramValue);
			return true;
		}
		else if (paramValue instanceof Double) {
			st.setDouble(paramName, (Double)paramValue);
			return true;
		}
		else if (paramValue instanceof Float) {
			st.setFloat(paramName, (Float)paramValue);
			return true;
		}
		else if (paramValue instanceof java.util.Date) {
			st.setTimestamp(paramName, new Timestamp(((java.util.Date)paramValue).getTime()));
			return true;
		}
		else if (paramValue instanceof Calendar) {
			st.setTimestamp(paramName, new Timestamp(((Calendar)paramValue).getTimeInMillis()));
			return true;
		}
		else if (paramValue instanceof Enum) {
			st.setString(paramName, paramValue.toString());
			return true;
		}
		else
			return false;
	}

	public static void setObject(PreparedStatementHelper st, int index, Object value) throws SQLException {
		if (!trySetObject(st, index, value))
			st.setString(index, value.toString());
	}

	static boolean trySetObject(PreparedStatementHelper st, int index, Object value) throws SQLException {
		if (value == null) {
			st.setNull(index, st.getMetaData().getColumnType(index));
			return true;
		}
		else if (value instanceof String) {
			st.setString(index, (String)value);
			return true;
		}
		else if (value instanceof Long) {
			st.setLong(index, (Long)value);
			return true;
		}
		else if (value instanceof Integer) {
			st.setInt(index, (Integer)value);
			return true;
		}
		else if (value instanceof Short) {
			st.setShort(index, (Short)value);
			return true;
		}
		else if (value instanceof Byte) {
			st.setByte(index, (Byte)value);
			return true;
		}
		else if (value instanceof Boolean) {
			st.setInt(index, (Boolean)value ? -1 : 0);
			return true;
		}
		else if (value instanceof BigDecimal) {
			st.setBigDecimal(index, (BigDecimal)value);
			return true;
		}
		else if (value instanceof Double) {
			st.setDouble(index, (Double)value);
			return true;
		}
		else if (value instanceof Float) {
			st.setFloat(index, (Float)value);
			return true;
		}
		else if (value instanceof java.util.Date) {
			st.setTimestamp(index, new Timestamp(((java.util.Date)value).getTime()));
			return true;
		}
		else if (value instanceof Calendar) {
			st.setTimestamp(index, new Timestamp(((Calendar)value).getTimeInMillis()));
			return true;
		}
		else if (value instanceof Enum) {
			st.setString(index, value.toString());
			return true;
		}
		else
			return false;
	}


	// Registering output parameters

	public static void registerString(CallableStatement call, String paramName) throws SQLException {
		call.registerOutParameter(paramName, Types.VARCHAR);
	}

	public static void registerNumber(CallableStatement call, String paramName) throws SQLException {
		call.registerOutParameter(paramName, Types.NUMERIC);
	}

	public static void registerBoolean(CallableStatement call, String paramName) throws SQLException {
		call.registerOutParameter(paramName, Types.NUMERIC);
	}

	public static void registerTimestamp(CallableStatement call, String paramName) throws SQLException {
		call.registerOutParameter(paramName, Types.TIMESTAMP);
	}

	public static void registerDate(CallableStatement call, String paramName) throws SQLException {
		call.registerOutParameter(paramName, Types.DATE);
	}

	public static void registerTime(CallableStatement call, String paramName) throws SQLException {
		call.registerOutParameter(paramName, Types.TIME);
	}

	public static void registerArray(CallableStatement call, String paramName) throws SQLException {
		call.registerOutParameter(paramName, Types.ARRAY);
	}

	public static void registerXML(CallableStatement call, String paramName) throws SQLException {
		call.registerOutParameter(paramName, Types.VARCHAR);
	}

	public static void registerSQLXML(CallableStatement call, String paramName) throws SQLException {
		call.registerOutParameter(paramName, Types.SQLXML);
	}


	// Reading output parameters

	public static String getString(CallableStatement call, String paramName) throws SQLException {
		return call.getString(paramName);
	}

	public static String getNullableString(CallableStatement call, String paramName) throws SQLException {
		String s = call.getString(paramName);
		return call.wasNull() ? null : s;
	}

	public static long getLong(CallableStatement call, String paramName) throws SQLException {
		return call.getLong(paramName);
	}

	public static Long getNullableLong(CallableStatement call, String paramName) throws SQLException {
		long l = call.getLong(paramName);
		return call.wasNull() ? null : l;
	}

	public static int getInt(CallableStatement call, String paramName) throws SQLException {
		return call.getInt(paramName);
	}

	public static Integer getNullableInteger(CallableStatement call, String paramName) throws SQLException {
		int i = call.getInt(paramName);
		return call.wasNull() ? null : i;
	}

	public static short getShort(CallableStatement call, String paramName) throws SQLException {
		return call.getShort(paramName);
	}

	public static Short getNullableShort(CallableStatement call, String paramName) throws SQLException {
		short s = call.getShort(paramName);
		return call.wasNull() ? null : s;
	}

	public static byte getByte(CallableStatement call, String paramName) throws SQLException {
		return call.getByte(paramName);
	}

	public static Byte getNullableByte(CallableStatement call, String paramName) throws SQLException {
		byte b = call.getByte(paramName);
		return call.wasNull() ? null : b;
	}

	public static boolean getBoolean(CallableStatement call, String paramName) throws SQLException {
		return call.getInt(paramName) != 0;
	}

	public static Boolean getNullableBoolean(CallableStatement call, String paramName) throws SQLException {
		int b = call.getInt(paramName);
		return call.wasNull() ? null : b != 0;
	}

	public static double getDouble(CallableStatement call, String paramName) throws SQLException {
		return call.getDouble(paramName);
	}

	public static Double getNullableDouble(CallableStatement call, String paramName) throws SQLException {
		double d = call.getDouble(paramName);
		return call.wasNull() ? null : d;
	}

	public static float getFloat(CallableStatement call, String paramName) throws SQLException {
		return call.getFloat(paramName);
	}

	public static Float getNullableFloat(CallableStatement call, String paramName) throws SQLException {
		float f = call.getFloat(paramName);
		return call.wasNull() ? null : f;
	}

	public static BigDecimal getBigDecimal(CallableStatement call, String paramName) throws SQLException {
		return call.getBigDecimal(paramName);
	}

	public static BigDecimal getNullableBigDecimal(CallableStatement call, String paramName) throws SQLException {
		BigDecimal d = call.getBigDecimal(paramName);
		return call.wasNull() ? null : d;
	}

	public static java.util.Date getTimestamp(CallableStatement call, String paramName) throws SQLException {
		Timestamp ts = call.getTimestamp(paramName);
		return ts != null ? new java.util.Date(ts.getTime()) : null;
	}

	public static java.util.Date getNullableTimestamp(CallableStatement call, String paramName) throws SQLException {
		Timestamp ts = call.getTimestamp(paramName);
		return call.wasNull() ? null : new java.util.Date(ts.getTime());
	}

	public static java.util.Date getDate(CallableStatement call, String paramName) throws SQLException {
		java.sql.Date d = call.getDate(paramName);
		return d != null ? new java.util.Date(d.getTime()) : null;
	}

	public static java.util.Date getNullableDate(CallableStatement call, String paramName) throws SQLException {
		java.sql.Date d = call.getDate(paramName);
		return call.wasNull() ? null : new java.util.Date(d.getTime());
	}

	public static java.util.Date getTime(CallableStatement call, String paramName) throws SQLException {
		Time t = call.getTime(paramName);
		return t != null ? new java.util.Date(t.getTime()) : null;
	}

	public static java.util.Date getNullableTime(CallableStatement call, String paramName) throws SQLException {
		Time t = call.getTime(paramName);
		return call.wasNull() ? null : new java.util.Date(t.getTime());
	}

	public static Object getArray(CallableStatement call, String paramName) throws SQLException {
		Array a = call.getArray(paramName);
		return a != null ? a.getArray() : null;
	}

	public static Object getNullableArray(CallableStatement call, String paramName) throws SQLException {
		Array a = call.getArray(paramName);
		return call.wasNull() ? null : a.getArray();
	}

	public static Document getXML(CallableStatement call, String columnName) throws SQLException {
		Clob c = call.getClob(columnName);
		return c != null ? parse(c) : null;
	}

	public static Document getNullableXML(CallableStatement call, String columnName) throws SQLException {
		Clob c = call.getClob(columnName);
		return call.wasNull() ? null : parse(c);
	}

	public static Node getSQLXML(CallableStatement call, String paramName) throws SQLException {
		SQLXML sqlxml = call.getSQLXML(paramName);
		return sqlxml != null ? sqlxml.getSource(DOMSource.class).getNode() : null;
	}

	public static Node getNullableSQLXML(CallableStatement call, String paramName) throws SQLException {
		SQLXML sqlxml = call.getSQLXML(paramName);
		return call.wasNull() ? null : sqlxml.getSource(DOMSource.class).getNode();
	}

	public static <T> List<T> getList(CallableStatement call, String paramName, ObjectReader<T> reader) throws SQLException {
		return getList((ResultSet)call.getObject(paramName), reader);
	}

	public static void readResultSet(CallableStatementHelper call, String paramName, ResultSetReader reader) throws SQLException {
		readResultSet((ResultSet)call.getObject(paramName), reader);
	}

	public static <T> void streamResultSet(CallableStatementHelper call, String paramName, ObjectReader<T> reader, StreamingIterator<T> iterator) throws SQLException {
		streamResultSet((ResultSet)call.getObject(paramName), reader, iterator);
	}

	public static <T> void streamResultSet(CallableStatementHelper call, String paramName, ObjectReader<T> reader, StreamingIterator<T> iterator, int fetchSize) throws SQLException {
		ResultSet rs = (ResultSet)call.getObject(paramName);
		rs.setFetchSize(fetchSize);
		streamResultSet(rs, reader, iterator);
	}


	// Util

	private static DocumentBuilderFactory documentBuilderFactory;
	private static TransformerFactory transformerFactory;

	private static synchronized DocumentBuilderFactory getDocumentBuilderFactory() {
		if (documentBuilderFactory == null)
			documentBuilderFactory = DocumentBuilderFactory.newInstance();
		return documentBuilderFactory;
	}

	private static synchronized TransformerFactory getTransformerFactory() {
		if (transformerFactory == null)
			transformerFactory = TransformerFactory.newInstance();
		return transformerFactory;
	}

	private static Document parse(Clob c) throws SQLException {
		try {
			try (Reader r = c.getCharacterStream()) {
				DocumentBuilder docBuilder = getDocumentBuilderFactory().newDocumentBuilder();
				return docBuilder.parse(new InputSource(r));
			}
		}
		catch (Exception ex) {
			throw ExceptionUtil.wrap(SQLException.class, ex);
		}
	}

	private static Clob serialize(Connection conn, Node n) throws SQLException {
		Clob c = conn.createClob();
		try {
			Transformer transformer = getTransformerFactory().newTransformer();
			transformer.transform(new DOMSource(n), new StreamResult(c.setCharacterStream(1L)));
			return c;
		}
		catch (TransformerException ex) {
			throw new SQLException(ex);
		}
	}

	private static <T> List<T> getList(ResultSet rs, ObjectReader<T> reader) throws SQLException {
		if (rs != null) {
			try {
				List<T> list = new ArrayList<>();
				while (rs.next()) {
					T obj = reader.read(rs);
					if (obj != null)
						list.add(obj);
				}
				return list;
			}
			finally {
				rs.close();
			}
		}
		else
			return null;
	}

	private static void readResultSet(ResultSet rs, ResultSetReader reader) throws SQLException {
		if (rs != null) {
			try {
				while (rs.next())
					reader.readResults(rs);
			}
			finally {
				rs.close();
			}
		}
	}

	private static <T> void streamResultSet(ResultSet rs, ObjectReader<T> reader, StreamingIterator<T> iterator) throws SQLException {
		if (rs != null) {
			try {
				while (rs.next()) {
					T obj = reader.read(rs);
					if (obj != null)
						iterator.put(obj);
				}
			}
			catch (InterruptedException ignored) {}
			finally {
				rs.close();
			}
		}
	}
}
