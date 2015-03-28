package org.strangeforest.orm.db;

import java.sql.*;

import org.strangeforest.db.*;
import org.strangeforest.orm.*;

public interface EntityMapper<I, E extends Entity<I>> extends EntityReader<I, E> {
	
	void mapId(I id, PreparedStatementHelper st) throws SQLException;
	void map(E entity, PreparedStatementHelper st, boolean forCreate) throws SQLException;
}
