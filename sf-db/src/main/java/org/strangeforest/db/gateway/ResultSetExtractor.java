package org.strangeforest.db.gateway;

import java.sql.*;

public interface ResultSetExtractor<T> {

	T extractResult(ResultSet rs) throws SQLException;
}