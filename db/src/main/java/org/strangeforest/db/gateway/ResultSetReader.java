package org.strangeforest.db.gateway;

import java.sql.*;

public interface ResultSetReader {
	
	void readResults(ResultSet rs) throws SQLException;
}
