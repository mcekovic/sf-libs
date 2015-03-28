package org.strangeforest.orm.db;

import java.sql.*;

import org.strangeforest.db.*;
import org.strangeforest.orm.*;

public interface CSAutoGenColumnsEntityMapper<I, E extends Entity<I>> extends EntityMapper<I, E> {

	void register(CallableStatementHelper call, boolean forCreate) throws SQLException;
	void read(CallableStatementHelper call, E entity, boolean forCreate) throws SQLException;
}
