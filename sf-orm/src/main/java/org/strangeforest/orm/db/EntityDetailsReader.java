package org.strangeforest.orm.db;

import java.sql.*;

import org.strangeforest.db.gateway.*;
import org.strangeforest.orm.*;

public abstract class EntityDetailsReader<I, E extends Entity<I>, D> extends MasterDetailReader<E, D> {

	private final EntityReader<I, E> entityReader;

	protected EntityDetailsReader(EntityReader<I, E> entityReader, ObjectReader<D> detailReader) {
		super(entityReader, detailReader);
		this.entityReader = entityReader;
	}

	@Override protected final boolean isSameMaster(E entity, ResultSet rs) throws SQLException {
		return entity.getId().equals(entityReader.readId(rs));
	}
}
