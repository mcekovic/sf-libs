package test.db.pool;

import java.sql.*;
import java.util.*;

import org.strangeforest.db.*;
import org.strangeforest.db.gateway.*;
import org.strangeforest.transaction.*;
import org.strangeforest.util.*;
import org.testng.annotations.*;

import static org.testng.Assert.*;

public class ConnectionPoolIT {

	private static ConnectionPoolDataSource dataSource;
	private static List<Connection> conns;

	private static final String DRIVER_CLASS = "org.h2.Driver";
	private static final String DB_URL = "jdbc:h2:~/.sf-db/data/test-dbgateway";
	private static final String USERNAME = "SA";
	private static final String PASSWORD = "SA";

	@BeforeClass
	public static void setUp() {
		dataSource = new ConnectionPoolDataSource(DRIVER_CLASS, DB_URL, USERNAME, PASSWORD);
		dataSource.setInitialPoolSize(0);
		dataSource.setMinPoolSize(2);
		dataSource.setMaxPoolSize(10);
		dataSource.init();
		conns = new ArrayList<>();
	}

	@AfterClass
	public static void tearDown() {
		if (dataSource != null)
			dataSource.close();
	}

	@Test
	public void maxPoolSizeTest() throws SQLException {
		for (int i = 1; i < 10; i++) {
			conns.add(dataSource.getConnection());
			System.out.println(i + ": " + dataSource.getStatistics());
		}
	}

	@Test
	public void testBusyTimeout() throws Exception {
		final Reference<Boolean> timedOut = new Reference<>(false);
		ConnectionPool pool = new ConnectionPool(DRIVER_CLASS, DB_URL, USERNAME, PASSWORD);
		pool.setMaxBusyTime(200L);
		pool.setPropertyCycle(50L);
		pool.setLogger(new ConnectionPoolLogger() {
			public void logMessage(String message) {}
			public void logError(String message, Throwable th) {
				System.out.println(message);
				timedOut.set(true);
			}
			public void logStatement(Statement st) {}
			public void logPreparedStatement(PreparedStatement pst) {
				System.out.println(pst);
			}
			public void logCallableStatement(CallableStatement call) {}
		});
		pool.init();

		Connection conn = pool.getConnection();
		conn.createStatement().execute("SELECT * FROM Dual");
		Thread.sleep(400L);
		conn.close();
		assertTrue(timedOut.get());
		pool.destroy();
	}

	@Test
	public void twoDataSourcesTxFails() throws Exception {
		ConnectionPoolDataSource dataSource2 = new ConnectionPoolDataSource(DRIVER_CLASS, DB_URL, "sa", "sa");
		dataSource2.init();
		SQLs sqls = new SQLs(Collections.singletonMap("GetName", "SELECT NAME FROM INFORMATION_SCHEMA.USERS"));
		DBGateway db = new DBGateway(dataSource, sqls);
		DBGateway db2 = new DBGateway(dataSource2, sqls);
		try {
			TransactionManager.begin();
			try {
				assertEquals(db.fetchScalar("GetName"), USERNAME);
				assertEquals(db2.fetchScalar("GetName"), USERNAME);
				TransactionManager.commit();
			}
			catch (Throwable th) {
				TransactionManager.rollback();
				throw ExceptionUtil.throwIt(th);
			}
			fail();
		}
		catch (Exception ex) {
			assertTrue(ExceptionUtil.getRootCause(ex) instanceof IllegalStateException);
		}
		finally {
			dataSource2.close();
		}
	}

	@Test
	public void twoConnectionsInTx() throws Exception {
		SQLs sqls = new SQLs(Collections.singletonMap("GetName", "SELECT NAME FROM INFORMATION_SCHEMA.USERS"));
		DBGateway db = new DBGateway(dataSource, sqls);
		TransactionManager.begin();
		try {
			db.executeQuery("GetName", rs -> {
				assertEquals(db.fetchScalar("GetName"), USERNAME);
				assertEquals(rs.getString(1), USERNAME);
			});
			TransactionManager.commit();
		}
		catch (Throwable th) {
			TransactionManager.rollback();
			throw ExceptionUtil.throwIt(th);
		}
	}
}
