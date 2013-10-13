package test.db.gateway;

import org.strangeforest.db.gateway.*;
import org.strangeforest.xml.helpers.*;
import org.testng.*;
import org.testng.annotations.*;

public class SQLsTest {

	private SQLs template;
	private SQLs templateWithProfile;

	@BeforeClass
	public void setUp() {
		template = new SQLs(getClass().getResourceAsStream("test.sqls"));
		templateWithProfile = new SQLs(getClass().getResourceAsStream("test.sqls"));
		templateWithProfile.setProfiles("Oracle");
	}

	@Test
	public void testGetSQL() {
		String sql = template.getSQL("TestGet");
		Assert.assertEquals(sql, "SELECT * FROM DUAL");
	}

	@Test
	public void testGetSQLWithInclude() {
		String sql = template.getSQL("TestGetWithInclude");
		Assert.assertEquals(sql, "SELECT DUMMY FROM DUAL");
	}

	@Test
	public void testGetTransformedSql() {
		String sql = template.getSQL("TestGetTransformed", new SQLTransformer() {
			public void transform(ElementHelper sql) {
				sql.findElement("table-name").setTextContent("DUAL");
			}
		});
		Assert.assertEquals(sql, "SELECT * FROM DUAL");
	}

	@Test
	public void testGetTransformedSqlWithInclude() {
		String sql = template.getSQL("TestGetTransformedWithInclude", new SQLTransformer() {
			public void transform(ElementHelper sql) {
				sql.findElement("table-name").setTextContent("DUAL");
			}
		});
		Assert.assertEquals(sql, "SELECT DUMMY FROM DUAL");
	}

	@Test
	public void testGetCachedTransformedSql() {
		String sql = template.getSQL("TestGetTransformed", new CachedSQLTransformer() {
			public void transform(ElementHelper sql) {
				sql.findElement("table-name").setTextContent("DUAL");
			}
			public String cacheKey() {
				return "DUAL";
			}
		});
		Assert.assertEquals(sql, "SELECT * FROM DUAL");
	}

	@Test
	public void testGetSQLElement() {
		ElementHelper sqlElement = template.getSQLElement("TestGetTransformed");
		sqlElement.findElement("table-name").setTextContent("DUAL");
		String sql = sqlElement.getTextContent().trim();
		Assert.assertEquals(sql, "SELECT * FROM DUAL");
	}

	@Test
	public void testGetSQLElementWithInclude() {
		ElementHelper sqlElement = template.getSQLElement("TestGetTransformedWithInclude");
		sqlElement.findElement("table-name").setTextContent("DUAL");
		String sql = sqlElement.getTextContent().trim();
		Assert.assertEquals(sql, "SELECT DUMMY FROM DUAL");
	}

	@Test
	public void testGetSQLWithNoProfile() {
		String sql = template.getSQL("TestGetWithProfile");
		Assert.assertEquals(sql, "SELECT 1");
	}

	@Test
	public void testGetSQLWithProfile() {
		String sql = templateWithProfile.getSQL("TestGetWithProfile");
		Assert.assertEquals(sql, "SELECT 1 FROM DUAL");
	}

	@Test
	public void testGetSQLElementWithNoProfile() {
		ElementHelper sqlElement = template.getSQLElement("TestGetWithProfile");
		String sql = sqlElement.getTextContent().trim();
		Assert.assertEquals(sql, "SELECT 1");
	}

	@Test
	public void testGetSQLElementWithProfile() {
		ElementHelper sqlElement = templateWithProfile.getSQLElement("TestGetWithProfile");
		String sql = sqlElement.getTextContent().trim();
		Assert.assertEquals(sql, "SELECT 1 FROM DUAL");
	}
}
