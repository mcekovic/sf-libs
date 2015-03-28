package org.strangeforest.orm.db;

import java.sql.*;

import org.strangeforest.db.gateway.*;
import org.strangeforest.orm.*;

public interface EntityReader<I, E extends Entity<I>> extends ObjectReader<E> {

	I readId(ResultSet rs) throws SQLException;
}
