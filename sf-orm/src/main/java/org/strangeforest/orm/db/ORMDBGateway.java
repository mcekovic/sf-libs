package org.strangeforest.orm.db;

import java.sql.*;
import java.util.*;
import javax.sql.*;

import org.strangeforest.db.*;
import org.strangeforest.db.gateway.*;
import org.strangeforest.orm.*;
import org.strangeforest.util.*;

public class ORMDBGateway extends DBGateway {

	public ORMDBGateway(DataSource dataSource, SQLs sqls) {
		super(dataSource, sqls);
	}

	public ORMDBGateway(ConnectionPool pool, SQLs sqls) {
		super(pool, sqls);
	}


	// Entity fetchers

	public <I, E extends Entity<I>> E fetchOne(String sql, I id, EntityMapper<I, E> mapper) {
		PreparedStatementHelper st = null;
		try {
			sql = getSQL(sql);
			E entity = null;
			Connection conn = getConnection();
			try {
				st = wrapPreparedStatement(conn, sql);
				try {
					mapper.mapId(id, st);
					ResultSet rs = st.executeQuery();
					if (rs.next())
						entity = mapper.read(rs);
				}
				finally {
					close(st);
				}
			}
			catch (SQLRecoverableException ex) {
				drop(conn);
				throw ex;
			}
			finally {
				close(conn);
			}
			return entity;
		}
		catch (SQLException ex) {
			throw wrapSQLException(ex, st);
		}
	}

	public <I, E extends Entity<I>> Map<I, E> fetchAll(String sql, EntityReader<I, E> reader) {
		PreparedStatementHelper st = null;
		try {
			sql = getSQL(sql);
			Map<I, E> map = new OrderedHashMap<>();
			Connection conn = getConnection();
			try {
				st = wrapPreparedStatement(conn, sql);
				try {
					ResultSet rs = st.executeQuery();
					while (rs.next()) {
						E entity = reader.read(rs);
						if (entity != null)
							map.put(reader.readId(rs), entity);
					}
				}
				finally {
					close(st);
				}
			}
			catch (SQLRecoverableException ex) {
				drop(conn);
				throw ex;
			}
			finally {
				close(conn);
			}
			return map;
		}
		catch (SQLException ex) {
			throw wrapSQLException(ex, st);
		}
	}


	// Entity ID fetchers

	public <I, E extends Entity<I>> Set<I> fetchIdSet(String sql, EntityReader<I, E> reader) {
		return fetchIdSet(sql, reader, null, null);
	}

	public <I, E extends Entity<I>> Set<I> fetchIdSet(String sql, EntityReader<I, E> reader, SQLTransformer transformer) {
		return fetchIdSet(sql, reader, transformer, null);
	}

	public <I, E extends Entity<I>> Set<I> fetchIdSet(String sql, EntityReader<I, E> reader, StatementPreparer preparer) {
		return fetchIdSet(sql, reader, null, preparer);
	}

	public <I, E extends Entity<I>> Set<I> fetchIdSet(String sql, EntityReader<I, E> reader, SQLTransformer transformer, StatementPreparer preparer) {
		Set<I> ids = new LinkedHashSet<I>();
		fetchIdCollection(ids, sql, reader, transformer, preparer);
		return ids;
	}

	public <I, E extends Entity<I>> List<I> fetchIdList(String sql, EntityReader<I, E> reader) {
		return fetchIdList(sql, reader, null, null);
	}

	public <I, E extends Entity<I>> List<I> fetchIdList(String sql, EntityReader<I, E> reader, SQLTransformer transformer) {
		return fetchIdList(sql, reader, transformer, null);
	}

	public <I, E extends Entity<I>> List<I> fetchIdList(String sql, EntityReader<I, E> reader, StatementPreparer preparer) {
		return fetchIdList(sql, reader, null, preparer);
	}

	public <I, E extends Entity<I>> List<I> fetchIdList(String sql, EntityReader<I, E> reader, SQLTransformer transformer, StatementPreparer preparer) {
		List<I> ids = new ArrayList<I>();
		fetchIdCollection(ids, sql, reader, transformer, preparer);
		return ids;
	}

	private <I, E extends Entity<I>> void fetchIdCollection(Collection<I> ids, String sql, EntityReader<I, E> reader, SQLTransformer transformer, StatementPreparer preparer) {
		PreparedStatementHelper st = null;
		try {
			sql = getSQL(sql, transformer);
			Connection conn = getConnection();
			try {
				st = wrapPreparedStatement(conn, sql);
				try {
					if (preparer != null)
						preparer.prepare(st);
					ResultSet rs = st.executeQuery();
					while (rs.next())
						ids.add(reader.readId(rs));
				}
				finally {
					close(st);
				}
			}
			catch (SQLRecoverableException ex) {
				drop(conn);
				throw ex;
			}
			finally {
				close(conn);
			}
		}
		catch (SQLException ex) {
			throw wrapSQLException(ex, st);
		}
	}


	// Entity detail fetchers

	public <I, E extends Entity<I>, D> Set<D> fetchDetailSet(String sql, I id, EntityMapper<I, E> mapper, ObjectReader<D> detailReader) {
		Set<D> details = new LinkedHashSet<>();
		fetchDetailCollection(sql, id, details, mapper, detailReader);
		return details;
	}

	public <I, E extends Entity<I>, D> List<D> fetchDetailList(String sql, I id, EntityMapper<I, E> mapper, ObjectReader<D> detailReader) {
		List<D> details = new ArrayList<>();
		fetchDetailCollection(sql, id, details, mapper, detailReader);
		return details;
	}

	private <I, E extends Entity<I>, D> void fetchDetailCollection(String sql, I id, Collection<D> details, EntityMapper<I, E> mapper, ObjectReader<D> detailReader) {
		PreparedStatementHelper st = null;
		try {
			sql = getSQL(sql);
			Connection conn = getConnection();
			try {
				st = wrapPreparedStatement(conn, sql);
				try {
					mapper.mapId(id, st);
					ResultSet rs = st.executeQuery();
					while (rs.next()) {
						D detail = detailReader.read(rs);
						if (detail != null)
							details.add(detail);
					}
				}
				finally {
					close(st);
				}
			}
			catch (SQLRecoverableException ex) {
				drop(conn);
				throw ex;
			}
			finally {
				close(conn);
			}
		}
		catch (SQLException ex) {
			throw wrapSQLException(ex, st);
		}
	}


	// Entity manipulation

	public <I, E extends Entity<I>> void createEntity(String sql, E entity, EntityMapper<I, E> mapper) {
		PreparedStatementHelper st = null;
		try {
			sql = getSQL(sql);
			Connection conn = getConnection();
			try {
				AutoGenColumnsEntityMapper<I, E> autoGenMapper = mapper instanceof AutoGenColumnsEntityMapper ? (AutoGenColumnsEntityMapper<I, E>)mapper : null;
				CSAutoGenColumnsEntityMapper<I, E> csAutoGenMapper = mapper instanceof CSAutoGenColumnsEntityMapper ? (CSAutoGenColumnsEntityMapper<I, E>)mapper : null;
				st = csAutoGenMapper != null ? wrapCallableStatement(conn, sql) : wrapPreparedStatement(conn, sql, autoGenMapper);
				try {
					mapper.map(entity, st, true);
					if (csAutoGenMapper != null)
						csAutoGenMapper.register((CallableStatementHelper)st, true);
					st.executeUpdate();
					if (autoGenMapper != null) {
						ResultSet rs = st.getGeneratedKeys();
						if (rs.next())
							autoGenMapper.readAutoGenColumns(rs, entity);
					}
					else if (csAutoGenMapper != null)
						csAutoGenMapper.read((CallableStatementHelper)st, entity, true);
				}
				finally {
					close(st);
				}
			}
			catch (SQLRecoverableException ex) {
				drop(conn);
				throw ex;
			}
			finally {
				close(conn);
			}
		}
		catch (SQLException ex) {
			throw wrapSQLException(ex, st);
		}
	}

	public <I, E extends Entity<I>> void saveEntity(String sql, E entity, EntityMapper<I, E> mapper) {
		PreparedStatementHelper st = null;
		try {
			sql = getSQL(sql);
			Connection conn = getConnection();
			try {
				CSAutoGenColumnsEntityMapper<I, E> csAutoGenMapper = mapper instanceof CSAutoGenColumnsEntityMapper ? (CSAutoGenColumnsEntityMapper<I, E>)mapper : null;
				st = csAutoGenMapper != null ? wrapCallableStatement(conn, sql) : wrapPreparedStatement(conn, sql);
				try {
					mapper.map(entity, st, false);
					if (csAutoGenMapper != null)
						csAutoGenMapper.register((CallableStatementHelper)st, false);
					st.executeUpdate();
					if (csAutoGenMapper != null)
						csAutoGenMapper.read((CallableStatementHelper)st, entity, false);
				}
				finally {
					close(st);
				}
			}
			catch (SQLRecoverableException ex) {
				drop(conn);
				throw ex;
			}
			finally {
				close(conn);
			}
		}
		catch (SQLException ex) {
			throw wrapSQLException(ex, st);
		}
	}

	public <I, E extends Entity<I>> void createEntities(String sql, Collection<E> entities, EntityMapper<I, E> mapper) {
		if (entities.isEmpty())
			return;
		PreparedStatementHelper st = null;
		try {
			sql = getSQL(sql);
			Connection conn = getConnection();
			try {
				AutoGenColumnsEntityMapper<I, E> autoGenMapper = mapper instanceof AutoGenColumnsEntityMapper ? (AutoGenColumnsEntityMapper<I, E>)mapper : null;
				st = wrapPreparedStatement(conn, sql, autoGenMapper);
				try {
					for (E entity : entities) {
						mapper.map(entity, st, true);
						st.addBatch();
					}
					st.executeBatch();
					if (autoGenMapper != null) {
						ResultSet rs = st.getGeneratedKeys();
						Iterator<E> iter = entities.iterator();
						while (rs.next())
							autoGenMapper.readAutoGenColumns(rs, iter.next());
					}
				}
				finally {
					close(st);
				}
			}
			catch (SQLRecoverableException ex) {
				drop(conn);
				throw ex;
			}
			finally {
				close(conn);
			}
		}
		catch (SQLException ex) {
			throw wrapSQLException(ex, st);
		}
	}

	public <I, E extends Entity<I>> void saveEntities(String sql, Collection<E> entities, EntityMapper<I, E> mapper) {
		if (entities.isEmpty())
			return;
		PreparedStatementHelper st = null;
		try {
			sql = getSQL(sql);
			Connection conn = getConnection();
			try {
				st = wrapPreparedStatement(conn, sql);
				try {
					for (E entity : entities) {
						mapper.map(entity, st, false);
						st.addBatch();
					}
					st.executeBatch();
				}
				finally {
					close(st);
				}
			}
			catch (SQLRecoverableException ex) {
				drop(conn);
				throw ex;
			}
			finally {
				close(conn);
			}
		}
		catch (SQLException ex) {
			throw wrapSQLException(ex, st);
		}
	}

	public <I, E extends Entity<I>> void deleteEntity(String sql, I id, EntityMapper<I, E> mapper) {
		PreparedStatementHelper st = null;
		try {
			sql = getSQL(sql);
			Connection conn = getConnection();
			try {
				st = wrapPreparedStatement(conn, sql);
				try {
					mapper.mapId(id, st);
					st.executeUpdate();
				}
				finally {
					close(st);
				}
			}
			catch (SQLRecoverableException ex) {
				drop(conn);
				throw ex;
			}
			finally {
				close(conn);
			}
		}
		catch (SQLException ex) {
			throw wrapSQLException(ex, st);
		}
	}

	public <I, E extends Entity<I>> void deleteEntities(String sql, Collection<I> ids, EntityMapper<I, E> mapper) {
		if (ids.isEmpty())
			return;
		PreparedStatementHelper st = null;
		try {
			sql = getSQL(sql);
			Connection conn = getConnection();
			try {
				st = wrapPreparedStatement(conn, sql);
				try {
					for (I id : ids) {
						mapper.mapId(id, st);
						st.addBatch();
					}
					st.executeBatch();
				}
				finally {
					close(st);
				}
			}
			catch (SQLRecoverableException ex) {
				drop(conn);
				throw ex;
			}
			finally {
				close(conn);
			}
		}
		catch (SQLException ex) {
			throw wrapSQLException(ex, st);
		}
	}


	// Entity collection manipulation

	public <I1, E1 extends Entity<I1>, I2, E2 extends Entity<I2>> void saveCollection(String sql, I1 fromId, Collection<I2> toIds, EntityMapper<I1, E1> fromMapper, EntityMapper<I2, E2> toMapper) {
		if (toIds.isEmpty())
			return;
		PreparedStatementHelper st = null;
		try {
			sql = getSQL(sql);
			Connection conn = getConnection();
			try {
				st = wrapPreparedStatement(conn, sql);
				try {
					fromMapper.mapId(fromId, st);
					for (I2 toId : toIds) {
						toMapper.mapId(toId, st);
						st.addBatch();
					}
					st.executeBatch();
				}
				finally {
					close(st);
				}
			}
			catch (SQLRecoverableException ex) {
				drop(conn);
				throw ex;
			}
			finally {
				close(conn);
			}
		}
		catch (SQLException ex) {
			throw wrapSQLException(ex, st);
		}
	}

	public <I, E extends Entity<I>> void deleteCollection(String sql, I id, EntityMapper<I, E> mapper) {
		deleteEntity(sql, id, mapper);
	}


	// Entity detail manipulation

	public <I, E extends Entity<I>, D> void saveDetailCollection(String sql, I id, Collection<D> details, EntityMapper<I, E> entityMapper, ObjectMapper<D> detailMapper) {
		if (details.isEmpty())
			return;
		PreparedStatementHelper st = null;
		try {
			sql = getSQL(sql);
			Connection conn = getConnection();
			try {
				st = wrapPreparedStatement(conn, sql);
				try {
					entityMapper.mapId(id, st);
					for (D detail : details) {
						detailMapper.map(detail, st);
						st.addBatch();
					}
					st.executeBatch();
				}
				finally {
					close(st);
				}
			}
			catch (SQLRecoverableException ex) {
				drop(conn);
				throw ex;
			}
			finally {
				close(conn);
			}
		}
		catch (SQLException ex) {
			throw wrapSQLException(ex, st);
		}
	}

	public <I, E extends Entity<I>, DI, D extends Entity<DI>> void deleteDetailCollection(String sql, I id, Collection<DI> detailIds, EntityMapper<I, E> entityMapper, EntityMapper<DI, D> detailMapper) {
		saveCollection(sql, id, detailIds, entityMapper, detailMapper);
	}

	public <I, E extends Entity<I>> void deleteAllDetails(String sql, I id, EntityMapper<I, E> mapper) {
		deleteEntity(sql, id, mapper);
	}
}
