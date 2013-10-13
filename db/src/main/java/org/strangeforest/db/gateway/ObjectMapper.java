package org.strangeforest.db.gateway;

import java.sql.*;

import org.strangeforest.db.*;

public interface ObjectMapper<T> extends ObjectReader<T> {
	
	void map(T obj, PreparedStatementHelper st) throws SQLException;
}
