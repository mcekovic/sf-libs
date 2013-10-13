package org.strangeforest.db.gateway;

import java.sql.*;

public interface DatabaseWorker<T> {
	
	T doInDatabase(Connection conn) throws SQLException;
}
