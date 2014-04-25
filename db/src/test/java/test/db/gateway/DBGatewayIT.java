package test.db.gateway;

import java.sql.*;
import java.util.*;

import org.strangeforest.db.*;
import org.strangeforest.db.gateway.*;
import org.strangeforest.db.logging.*;
import org.testng.annotations.*;

import static org.strangeforest.db.gateway.DataHelper.*;
import static org.strangeforest.db.gateway.NamedParameters.*;
import static org.strangeforest.db.gateway.Parameter.*;
import static org.testng.Assert.*;

public class DBGatewayIT {

	private ConnectionPoolDataSource dataSource;
	private DBGateway db;

	private static final String DRIVER_CLASS = "org.h2.Driver";
	private static final String DB_URL = "jdbc:h2:~/.sf-db/data/test-dbgateway";
	private static final String USERNAME = "SA";
	private static final String PASSWORD = "SA";

	@BeforeClass
	public void setUp() {
		dataSource = new ConnectionPoolDataSource(DRIVER_CLASS, DB_URL, USERNAME, PASSWORD);
		dataSource.setMaxStatements(20);
		dataSource.setLogger(new DBConnectionPoolLogger("org.strangeforest.db"));
		Properties props = new Properties();
		props.setProperty("v$session.program", "DBGateway Test");
		dataSource.setConnectionProperties(props);
		dataSource.init();
		SQLs sqls = new SQLs(getClass().getResourceAsStream("test-dbgateway.sqls"));
		db = new DBGateway(dataSource, sqls);
	}

	@AfterClass
	public void tearDown() {
		if (dataSource != null)
			dataSource.close();
	}


	// Basic test

	@Test(groups = "DatabaseRequired")
	public void executeQueryTest() {
		db.executeQuery("SelectAll", rs -> assertEquals(getInt(rs, "X"), 1));
	}

	@Test(groups = "DatabaseRequired", expectedExceptions = DBException.class)
	public void wrongSyntaxTest() {
		db.executeQuery("WrongSyntax", rs -> fail());
	}

	@Test(groups = "DatabaseRequired")
	public void fetchScalarTest() {
		long x = db.fetchScalar("SelectAll");
		assertEquals(x, 1);
	}

	@Test(groups = "DatabaseRequired")
	public void fetchOneTest() {
		Dual dual = db.fetchOne("SelectWhere", DualReader,	(PreparedStatementHelper st) -> {
			st.setMasked("x", true);
			setInt(st, "x", 1);
		});
		assertEquals(dual.x, 1);
	}

	@Test(groups = "DatabaseRequired")
	public void fetchOneNotFound() {
		Dual dual = db.fetchOne("SelectWhere", DualReader, (PreparedStatementHelper st) -> setInt(st, "x", 2));
		assertNull(dual);
	}

	@Test(groups = "DatabaseRequired")
	public void fetchListTest() {
		List<Dual> duals = db.fetchList("SelectAll", DualReader);
		assertEquals(duals.size(), 1);
		assertEquals(duals.get(0).x, 1);
	}

	@Test(groups = "DatabaseRequired")
	public void fetchListTest2() {
		List<Dual> duals = db.fetchList("SelectWhere", DualReader, (PreparedStatementHelper st) -> setInt(st, "x", 2));
		assertEquals(duals.size(), 0);
	}



	// Insert

	@BeforeGroups(groups = "InsertTest")
	public void createTable() {
		try {
			db.executeDDL("DropTable");
		}
		catch (DBException ignored) {}
		db.executeDDL("CreateTable");
	}

	@Test(groups = {"DatabaseRequired", "InsertTest"})
	public void insertTest() {
		db.executeUpdate("InsertInto", (PreparedStatementHelper st) -> setString(st, "name", "Pera"));
	}

	@Test(groups = {"DatabaseRequired", "InsertTest"}, expectedExceptions = DBException.class)
	public void insertTest2() {
		db.executeUpdate("InsertInto", (PreparedStatementHelper st) -> setString(st, "name", "Pera je veliki konj koji ne moze da stane u stalu!"));
	}

	@Test(groups = {"DatabaseRequired", "InsertTest"})
	public void insertWithAutoGenColumnsTest() {
		db.executeUpdate("InsertInto", st -> setString(st, "name", "PeraAutoGen"),
			new AutoGenColumnsReader() {
				public String[] getAutoGenColumns() {
					return new String[] {"Created"};
				}
				public void readAutoGenColumns(ResultSet rs) throws SQLException {
					assertNotNull(rs.getTimestamp(1), "Auto-Gen field Created not populated.");
				}
			}
		);
	}

	@Test(groups = {"DatabaseRequired", "InsertTest"}, dependsOnMethods = "insertWithAutoGenColumnsTest")
	public void updateWithAutoGenColumnsTest() {
		db.executeUpdate("Update", st -> setString(st, "name", "PeraAutoGen"),
			new AutoGenColumnsReader() {
				public String[] getAutoGenColumns() {
					return new String[] {"Modified"};
				}
				public void readAutoGenColumns(ResultSet rs) throws SQLException {
					assertNotNull(rs.getTimestamp(1), "Auto-Gen field Modified not populated.");
				}
			}
		);
	}

	@Test(groups = {"DatabaseRequired", "InsertTest"})
	public void batchedInsertTest() {
		db.executeBatchUpdate("InsertInto",
			new BatchStatementPreparer() {
				private int i;
				public boolean hasMore() {
					return ++i <= 5;
				}
				public void prepare(PreparedStatementHelper st) throws SQLException {
					setString(st, "name", "Pera" + i);
				}
			}
		);
	}

	@Test(groups = "DatabaseRequired")
	public void arrayParametersTest() {
		Dual dual = db.fetchOne("ArrayParameters", DualReader, ArrayParameters.params(1));
		assertEquals(dual.x, 1);

		List<Dual> duals = db.fetchList("ArrayParameters", DualReader, ArrayParameters.params(null));
		assertTrue(duals.isEmpty());
	}

	@Test(groups = "DatabaseRequired")
	public void namedParametersTest() {
		Dual dual = db.fetchOne("NamedParameters", DualReader, params("x", 1));
		assertEquals(dual.x, 1);

		List<Dual> duals = db.fetchList("NamedParameters", DualReader, params(param("x", null)));
		assertTrue(duals.isEmpty());
	}

	@AfterGroups(groups = "InsertTest")
	public void dropTable() {
		db.executeDDL("DropTable");
	}


	private static ObjectReader<Dual> DualReader = rs -> {
		Dual dual = new Dual();
		dual.x = getInt(rs, "X");
		return dual;
	};

	public static class Dual {
		private int x;
	}
}

