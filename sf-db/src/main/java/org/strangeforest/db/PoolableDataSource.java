package org.strangeforest.db;

import java.sql.*;
import javax.sql.*;

public interface PoolableDataSource extends DataSource {

	void dropConnection(Connection conn);
}