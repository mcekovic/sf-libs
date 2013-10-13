package org.strangeforest.db.gateway;

import java.sql.*;

public interface AutoGenColumnsReader extends AutoGenColumnsProvider {

	void readAutoGenColumns(ResultSet rs) throws SQLException;
}
