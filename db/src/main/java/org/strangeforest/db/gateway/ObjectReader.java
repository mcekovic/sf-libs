package org.strangeforest.db.gateway;

import java.sql.*;

public interface ObjectReader<T> {
	
	T read(ResultSet rs) throws SQLException;
}
