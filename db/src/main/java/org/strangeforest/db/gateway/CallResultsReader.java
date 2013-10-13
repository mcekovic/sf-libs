package org.strangeforest.db.gateway;

import java.sql.*;

import org.strangeforest.db.*;

public interface CallResultsReader {
	
	void registerResults(CallableStatementHelper call) throws SQLException;
	void readResults(CallableStatementHelper call) throws SQLException;
}
