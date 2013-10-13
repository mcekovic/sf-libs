package org.strangeforest.db.gateway;

import java.sql.*;
import java.util.*;
import javax.sql.*;

import org.strangeforest.db.*;

public class DBGateway {

	private DataSource dataSource;
	private ConnectionPool pool;
	private final SQLs sqls;
	private final boolean statementsWrapped;

	protected static final String DATABASE_ERROR = "Database error.";

	public DBGateway(DataSource dataSource) {
		this(dataSource, null);
	}

	public DBGateway(DataSource dataSource, SQLs sqls) {
		super();
		this.dataSource = dataSource;
		this.sqls = sqls;
		try {
			statementsWrapped = dataSource.isWrapperFor(org.strangeforest.db.ConnectionPoolDataSource.class);
		}
		catch (SQLException ex) {
			throw wrapSQLException(ex, null);
		}
	}

	public DBGateway(ConnectionPool pool) {
		this(pool, null);
	}

	public DBGateway(ConnectionPool pool, SQLs sqls) {
		super();
		this.pool = pool;
		this.sqls = sqls;
		statementsWrapped = true;
	}

	public boolean hasProfile(String profile) {
		return sqls != null && sqls.hasProfile(profile);
	}

	public void setProfiles(String profiles) {
		if (sqls != null)
			sqls.setProfiles(profiles);
	}

	protected final Connection getConnection() throws SQLException {
		return dataSource != null ? dataSource.getConnection() : pool.getConnection();
	}

	protected final Connection getConnection(boolean ignoreTransaction) throws SQLException {
		return dataSource != null ? dataSource.getConnection() : pool.getConnection(ignoreTransaction);
	}

	protected final void close(Connection conn) {
		try {
			conn.close();
		}
		catch (Throwable ignored) {}
	}

	protected final void close(Statement st) {
		try {
			st.close();
		}
		catch (Throwable ignored) {}
	}

	protected final void drop(Connection conn) {
		try {
			if (dataSource != null) {
				if (dataSource.isWrapperFor(org.strangeforest.db.ConnectionPoolDataSource.class))
					dataSource.unwrap(org.strangeforest.db.ConnectionPoolDataSource.class).dropConnection(conn);
				else
					conn.close();
			}
			else
				pool.dropConnection(conn);
		}
		catch (Throwable ignored) {}
	}

	
	// Generic database access

	public <T> T doInDatabase(DatabaseWorker<T> worker) {
		try {
			Connection conn = getConnection();
			try {
				return worker.doInDatabase(conn);
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
			throw wrapSQLException(ex, null);
		}
	}


	// Generic queries

	public void executeQuery(String sql, ResultSetReader reader) {
		executeQuery(sql, null, null, reader);
	}

	public void executeQuery(String sql, SQLTransformer transformer, ResultSetReader reader) {
		executeQuery(sql, transformer, null, reader);
	}

	public void executeQuery(String sql, StatementPreparer preparer, ResultSetReader reader) {
		executeQuery(sql, null, preparer, reader);
	}

	public void executeQuery(String sql, SQLTransformer transformer, StatementPreparer preparer, ResultSetReader reader) {
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
						reader.readResults(rs);
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


	// Generic updates

	public int executeUpdate(String sql) {
		return executeUpdate(sql, null, null, null);
	}

	public int executeUpdate(String sql, SQLTransformer transformer) {
		return executeUpdate(sql, transformer, null, null);
	}

	public int executeUpdate(String sql, StatementPreparer preparer) {
		return executeUpdate(sql, null, preparer, null);
	}

	public int executeUpdate(String sql, StatementPreparer preparer, AutoGenColumnsReader reader) {
		return executeUpdate(sql, null, preparer, reader);
	}

	public int executeUpdate(String sql, SQLTransformer transformer, StatementPreparer preparer) {
		return executeUpdate(sql, transformer, preparer, null);
	}

	public int executeUpdate(String sql, SQLTransformer transformer, StatementPreparer preparer, AutoGenColumnsReader reader) {
		PreparedStatementHelper st = null;
		try {
			sql = getSQL(sql, transformer);
			Connection conn = getConnection();
			try {
				st = wrapPreparedStatement(conn, sql, reader);
				try {
					if (preparer != null)
						preparer.prepare(st);
					int count = st.executeUpdate();
					if (reader != null) {
						ResultSet rs = st.getGeneratedKeys();
						if (rs.next())
							reader.readAutoGenColumns(rs);
					}
					return count;
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


	// Generic batch updates

	public void executeBatchUpdate(String sql, BatchStatementPreparer preparer) {
		executeBatchUpdate(sql, null, preparer, null);
	}

	public void executeBatchUpdate(String sql, BatchStatementPreparer preparer, AutoGenColumnsReader reader) {
		executeBatchUpdate(sql, null, preparer, reader);
	}

	public void executeBatchUpdate(String sql, SQLTransformer transformer, BatchStatementPreparer preparer, AutoGenColumnsReader reader) {
		if (!preparer.hasMore())
			return;
		PreparedStatementHelper st = null;
		try {
			sql = getSQL(sql, transformer);
			Connection conn = getConnection();
			try {
				st = wrapPreparedStatement(conn, sql, reader);
				try {
					preparer.prepareOnce(st);
					do {
						preparer.prepare(st);
						st.addBatch();
					}
					while (preparer.hasMore());
					st.executeBatch();
					if (reader != null) {
						ResultSet rs = st.getGeneratedKeys();
						while (rs.next())
							reader.readAutoGenColumns(rs);
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


	// Generic stored procedures calls

	public void executeCall(String sql) {
		executeCall(sql, null, null, null);
	}

	public void executeCall(String sql, SQLTransformer transformer) {
		executeCall(sql, transformer, null, null);
	}

	public void executeCall(String sql, StatementPreparer preparer) {
		executeCall(sql, null, preparer, null);
	}

	public void executeCall(String sql, CallResultsReader reader) {
		executeCall(sql, null, null, reader);
	}

	public void executeCall(String sql, SQLTransformer transformer, StatementPreparer preparer) {
		executeCall(sql, transformer, preparer, null);
	}

	public void executeCall(String sql, SQLTransformer transformer, CallResultsReader reader) {
		executeCall(sql, transformer, null, reader);
	}

	public void executeCall(String sql, StatementPreparer preparer, CallResultsReader reader) {
		executeCall(sql, null, preparer, reader);
	}

	public void executeCall(String sql, SQLTransformer transformer, StatementPreparer preparer, CallResultsReader reader) {
		CallableStatementHelper call = null;
		try {
			sql = getSQL(sql, transformer);
			Connection conn = getConnection();
			try {
				call = wrapCallableStatement(conn, sql);
				try {
					if (preparer != null)
						preparer.prepare(call);
					if (reader != null)
						reader.registerResults(call);
					call.execute();
					if (reader != null)
						reader.readResults(call);
				}
				finally {
					close(call);
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
			throw wrapSQLException(ex, call);
		}
	}


	// Generic batched stored procedures calls

	public void executeBatchedCalls(String sql, BatchStatementPreparer preparer) {
		executeBatchedCalls(sql, null, preparer);
	}

	public void executeBatchedCalls(String sql, SQLTransformer transformer, BatchStatementPreparer preparer) {
		if (!preparer.hasMore())
			return;
		CallableStatementHelper call = null;
		try {
			sql = getSQL(sql, transformer);
			Connection conn = getConnection();
			try {
				call = wrapCallableStatement(conn, sql);
				try {
					preparer.prepareOnce(call);
					do {
						preparer.prepare(call);
						call.addBatch();
					}
					while (preparer.hasMore());
					call.executeBatch();
				}
				finally {
					close(call);
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
			throw wrapSQLException(ex, call);
		}
	}


	// DDL

	public void executeDDL(String sql) {
		executeDDL(sql, (SQLTransformer)null);
	}

	public void executeDDL(String sql, SQLTransformer transformer) {
		StatementHelper st = null;
		try {
			sql = getSQL(sql, transformer);
			Connection conn = getConnection(true);
			try {
				st = wrapStatement(conn);
				try {
					st.executeUpdate(sql);
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

	public void executeDDL(String sql, StatementPreparer preparer) {
		executeDDL(sql, null, preparer);
	}

	public void executeDDL(String sql, SQLTransformer transformer, StatementPreparer preparer) {
		PreparedStatementHelper st = null;
		try {
			sql = getSQL(sql, transformer);
			Connection conn = getConnection(true);
			try {
				st = wrapPreparedStatement(conn, sql);
				try {
					if (preparer != null)
						preparer.prepare(st);
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


	// Scalar fetchers

	public <T> T fetchScalar(String sql) {
		return this.<T>fetchScalar(sql, null, null);
	}

	public <T> T fetchScalar(String sql, SQLTransformer transformer) {
		return this.<T>fetchScalar(sql, transformer, null);
	}

	public <T> T fetchScalar(String sql, StatementPreparer preparer) {
		return this.<T>fetchScalar(sql, null, preparer);
	}

	public <T> T fetchScalar(String sql, SQLTransformer transformer, StatementPreparer preparer) {
		PreparedStatementHelper st = null;
		try {
			sql = getSQL(sql, transformer);
			Object scalar = null;
			Connection conn = getConnection();
			try {
				st = wrapPreparedStatement(conn, sql);
				try {
					if (preparer != null)
						preparer.prepare(st);
					ResultSet rs = st.executeQuery();
					if (rs.next())
						scalar = rs.getObject(1);
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
			return (T)scalar;
		}
		catch (SQLException ex) {
			throw wrapSQLException(ex, st);
		}
	}


	// Object fetchers

	public <T> T fetchOne(String sql, ObjectReader<T> reader) {
		return fetchObject(sql, reader, null, null, true);
	}

	public <T> T fetchOne(String sql, ObjectReader<T> reader, SQLTransformer transformer) {
		return fetchObject(sql, reader, transformer, null, true);
	}

	public <T> T fetchOne(String sql, ObjectReader<T> reader, StatementPreparer preparer) {
		return fetchObject(sql, reader, null, preparer, true);
	}

	public <T> T fetchOne(String sql, ObjectReader<T> reader, SQLTransformer transformer, StatementPreparer preparer) {
		return fetchObject(sql, reader, transformer, preparer, true);
	}

	public <T> T fetchFirst(String sql, ObjectReader<T> reader) {
		return fetchObject(sql, reader, null, null, false);
	}

	public <T> T fetchFirst(String sql, ObjectReader<T> reader, SQLTransformer transformer) {
		return fetchObject(sql, reader, transformer, null, false);
	}

	public <T> T fetchFirst(String sql, ObjectReader<T> reader, StatementPreparer preparer) {
		return fetchObject(sql, reader, null, preparer, false);
	}

	public <T> T fetchFirst(String sql, ObjectReader<T> reader, SQLTransformer transformer, StatementPreparer preparer) {
		return fetchObject(sql, reader, transformer, preparer, false);
	}

	private <T> T fetchObject(String sql, ObjectReader<T> reader, SQLTransformer transformer, StatementPreparer preparer, boolean failIfMultipleFound) {
		PreparedStatementHelper st = null;
		try {
			sql = getSQL(sql, transformer);
			T obj = null;
			Connection conn = getConnection();
			try {
				st = wrapPreparedStatement(conn, sql);
				try {
					if (preparer != null)
						preparer.prepare(st);
					ResultSet rs = st.executeQuery();
					if (rs.next())
						obj = reader.read(rs);
					if (failIfMultipleFound && rs.next())
						throw new DBException("Multiple results found.", st.toString());
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
			return obj;
		}
		catch (SQLException ex) {
			throw wrapSQLException(ex, st);
		}
	}

	public <T> Set<T> fetchSet(String sql, ObjectReader<T> reader) {
		return fetchSet(sql, reader, null, null);
	}

	public <T> Set<T> fetchSet(String sql, ObjectReader<T> reader, SQLTransformer transformer) {
		return fetchSet(sql, reader, transformer, null);
	}

	public <T> Set<T> fetchSet(String sql, ObjectReader<T> reader, StatementPreparer preparer) {
		return fetchSet(sql, reader, null, preparer);
	}

	public <T> Set<T> fetchSet(String sql, ObjectReader<T> reader, SQLTransformer transformer, StatementPreparer preparer) {
		Set<T> set = new LinkedHashSet<>();
		fetchCollection(set, sql, reader, transformer, preparer);
		return set;
	}

	public <T> List<T> fetchList(String sql, ObjectReader<T> reader) {
		return fetchList(sql, reader, null, null);
	}

	public <T> List<T> fetchList(String sql, ObjectReader<T> reader, SQLTransformer transformer) {
		return fetchList(sql, reader, transformer, null);
	}

	public <T> List<T> fetchList(String sql, ObjectReader<T> reader, StatementPreparer preparer) {
		return fetchList(sql, reader, null, preparer);
	}

	public <T> List<T> fetchList(String sql, ObjectReader<T> reader, SQLTransformer transformer, StatementPreparer preparer) {
		List<T> list = new ArrayList<>();
		fetchCollection(list, sql, reader, transformer, preparer);
		return list;
	}

	private <T> void fetchCollection(Collection<T> col, String sql, ObjectReader<T> reader, SQLTransformer transformer, StatementPreparer preparer) {
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
					while (rs.next()) {
						T obj = reader.read(rs);
						if (obj != null)
							col.add(obj);
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


	// Object manipulation

	public <T> void updateObject(String sql, T obj, ObjectMapper<T> mapper) {
		updateObject(sql, obj, mapper, null);
	}

	public <T> void updateObject(String sql, T obj, ObjectMapper<T> mapper, SQLTransformer transformer) {
		PreparedStatementHelper st = null;
		try {
			sql = getSQL(sql, transformer);
			Connection conn = getConnection();
			try {
				st = wrapPreparedStatement(conn, sql);
				try {
					mapper.map(obj, st);
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


	// Object collection manipulation

	public <T> void updateCollection(String sql, Collection<T> objs, ObjectMapper<T> mapper) {
		updateCollection(sql, objs, mapper, null);
	}

	public <T> void updateCollection(String sql, Collection<T> objs, ObjectMapper<T> mapper, SQLTransformer transformer) {
		if (objs.isEmpty())
			return;
		PreparedStatementHelper st = null;
		try {
			sql = getSQL(sql, transformer);
			Connection conn = getConnection();
			try {
				st = wrapPreparedStatement(conn, sql);
				try {
					for (T obj : objs) {
						mapper.map(obj, st);
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


	// SQL

	protected String getSQL(String sql) {
		return sqls.getSQL(sql);
	}

	protected String getSQL(String sql, SQLTransformer transformer) {
		if (sqls != null)
			return sqls.getSQL(sql, transformer);
		else {
			if (transformer != null)
				throw new IllegalArgumentException("Cannot have SQLTransformer without SQLs.");
			return sql;
		}
	}


	// Wrapping

	protected final StatementHelper wrapStatement(Connection conn) throws SQLException {
		if (statementsWrapped)
			return (StatementHelper)conn.createStatement();
		else
			return new StatementHelper(conn.createStatement());
	}

	protected final PreparedStatementHelper wrapPreparedStatement(Connection conn, String sql) throws SQLException {
		if (statementsWrapped)
			return (PreparedStatementHelper)conn.prepareStatement(sql);
		else
			return new PreparedStatementHelper(conn, sql);
	}

	protected final PreparedStatementHelper wrapPreparedStatement(Connection conn, String sql, AutoGenColumnsProvider provider) throws SQLException {
		if (provider != null) {
			if (statementsWrapped)
				return (PreparedStatementHelper)conn.prepareStatement(sql, provider.getAutoGenColumns());
			else
				return new PreparedStatementHelper(conn, sql, provider.getAutoGenColumns());
		}
		else
			return wrapPreparedStatement(conn, sql);
	}

	protected final CallableStatementHelper wrapCallableStatement(Connection conn, String sql) throws SQLException {
		if (statementsWrapped)
			return (CallableStatementHelper)conn.prepareCall(sql);
		else
			return new CallableStatementHelper(conn, sql);
	}

	protected static DBException wrapSQLException(SQLException ex, Statement st) {
		return DBException.create(DATABASE_ERROR, st != null ? st.toString() : null, ex);
	}


	// Misc

	public static String getIdsAsString(long[] ids) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < ids.length; i++) {
			if (i > 0)
				sb.append(',');
			sb.append(ids[i]);
		}
		return sb.toString();
	}

	public static <I> String getIdsAsString(I[] ids) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < ids.length; i++) {
			if (i > 0)
				sb.append(',');
			I id = ids[i];
			if (id instanceof String || id instanceof Enum) {
				sb.append('\'');
				sb.append(id);
				sb.append('\'');
			}
			else
				sb.append(id);
		}
		return sb.toString();
	}
}
