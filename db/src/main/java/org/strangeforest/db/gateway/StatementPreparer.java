package org.strangeforest.db.gateway;

import java.sql.*;

import org.strangeforest.db.*;

public interface StatementPreparer {
	
	void prepare(PreparedStatementHelper st) throws SQLException;
}
