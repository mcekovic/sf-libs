package org.strangeforest.orm.db;

import java.sql.*;

import org.strangeforest.db.gateway.*;
import org.strangeforest.orm.*;

public interface AutoGenColumnsEntityMapper<I, E extends Entity<I>> extends EntityMapper<I, E>, AutoGenColumnsProvider {

	void readAutoGenColumns(ResultSet rs, E entity) throws SQLException;
}
