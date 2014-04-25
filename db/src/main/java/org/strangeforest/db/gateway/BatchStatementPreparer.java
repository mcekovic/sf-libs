package org.strangeforest.db.gateway;

import java.sql.*;

import org.strangeforest.db.*;

public interface BatchStatementPreparer extends StatementPreparer {
	
	boolean hasMore();
	default void prepareOnce(PreparedStatementHelper st) throws SQLException {}
}
