package org.strangeforest.orm.db;

import java.sql.*;
import java.time.*;

import org.strangeforest.db.*;
import org.strangeforest.db.gateway.*;
import org.strangeforest.orm.*;

public abstract class ORMDataHelper extends DataHelper {

	// ResultSet reading

	public static LocalDateTime getDateTime(ResultSet rs, String columnName) throws SQLException {
		Timestamp ts = rs.getTimestamp(columnName);
		return ts != null ? ts.toLocalDateTime() : null;
	}

	public static LocalDateTime getNullableDateTime(ResultSet rs, String columnName) throws SQLException {
		Timestamp ts = rs.getTimestamp(columnName);
		return rs.wasNull() ? null : ts.toLocalDateTime();
	}

	public static LocalDate getLocalDate(ResultSet rs, String columnName) throws SQLException {
		Date date = rs.getDate(columnName);
		return date != null ? date.toLocalDate() : null;
	}

	public static LocalDate getNullableLocalDate(ResultSet rs, String columnName) throws SQLException {
		Date date = rs.getDate(columnName);
		return rs.wasNull() ? null : date.toLocalDate();
	}

	public static Duration getDuration(ResultSet rs, String columnName) throws SQLException {
		return Duration.ofMillis(rs.getLong(columnName));
	}

	public static Duration getNullableDuration(ResultSet rs, String columnName) throws SQLException {
		long millis = rs.getLong(columnName);
		return rs.wasNull() ? null : Duration.ofMillis(millis);
	}


	// Statement preparing

	public static void setDateTime(PreparedStatementHelper st, String paramName, LocalDateTime paramValue) throws SQLException {
		if (paramValue != null)
			st.setTimestamp(paramName, Timestamp.valueOf(paramValue));
		else
			st.setNull(paramName, Types.TIMESTAMP);
	}

	public static void setLocalDate(PreparedStatementHelper st, String paramName, LocalDate paramValue) throws SQLException {
		if (paramValue != null)
			st.setDate(paramName, Date.valueOf(paramValue));
		else
			st.setNull(paramName, Types.TIMESTAMP);
	}

	public static void setDuration(PreparedStatementHelper st, String paramName, Duration paramValue) throws SQLException {
		if (paramValue != null)
			st.setLong(paramName, paramValue.toMillis());
		else
			st.setNull(paramName, Types.NUMERIC);
	}

	public static <E extends DomainEntity<Long, E>> void setLongRef(PreparedStatementHelper st, String paramName, EntityReference<Long, E> paramValue) throws SQLException {
		setLong(st, paramName, paramValue != null && !paramValue.isNull() ? paramValue.getMappedId() : null);
	}

	public static <E extends DomainEntity<String, E>> void setStringRef(PreparedStatementHelper st, String paramName, EntityReference<String, E> paramValue) throws SQLException {
		setString(st, paramName, paramValue != null && !paramValue.isNull() ? paramValue.getMappedId() : null);
	}


	// Reading output parameters

	public static LocalDateTime getDateTime(CallableStatement call, String paramName) throws SQLException {
		Timestamp ts = call.getTimestamp(paramName);
		return ts != null ? ts.toLocalDateTime() : null;
	}

	public static LocalDateTime getNullableDateTime(CallableStatement call, String paramName) throws SQLException {
		Timestamp ts = call.getTimestamp(paramName);
		return call.wasNull() ? null : ts.toLocalDateTime();
	}

	public static LocalDate getLocalDate(CallableStatement call, String paramName) throws SQLException {
		Date date = call.getDate(paramName);
		return date != null ? date.toLocalDate() : null;
	}

	public static LocalDate getNullableLocalDate(CallableStatement call, String paramName) throws SQLException {
		Date date = call.getDate(paramName);
		return call.wasNull() ? null : date.toLocalDate();
	}

	public static Duration getDuration(CallableStatement call, String columnName) throws SQLException {
		return Duration.ofMillis(call.getLong(columnName));
	}

	public static Duration getNullableDuration(CallableStatement call, String columnName) throws SQLException {
		long millis = call.getLong(columnName);
		return call.wasNull() ? null : Duration.ofMillis(millis);
	}
}
