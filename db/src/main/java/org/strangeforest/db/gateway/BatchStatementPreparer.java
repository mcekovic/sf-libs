package org.strangeforest.db.gateway;

import java.sql.*;

import org.strangeforest.db.*;

public abstract class BatchStatementPreparer implements StatementPreparer {
	
	public abstract boolean hasMore();
	public void prepareOnce(PreparedStatementHelper st) throws SQLException {}
}
