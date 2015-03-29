package test.orm;

import org.strangeforest.db.*;
import org.strangeforest.db.gateway.*;
import org.strangeforest.db.logging.*;
import org.strangeforest.orm.*;
import org.testng.annotations.*;

import test.orm.db.*;

import static org.testng.Assert.*;

@Test
public class ORMTest {

	private ConnectionPoolDataSource dataSource;
	private DBGateway db;
	private LocalRepository<Long, TestAggregate> aggregateManager;
	private LocalRepository<Long, TestEntity> entityManager;
	private LocalRepository<Long, SimpleTestEntity> simpleEntityManager;

	private Long aggregate1Id;
	private Long aggregate2Id;
	private Long entityId, entity2Id;
	private Long simpleEntityId;

	private static final String DRIVER_CLASS = "org.h2.Driver";
	private static final String DB_URL = "jdbc:h2:mem:test";
	private static final String USERNAME = "SA";
	private static final String PASSWORD = "SA";

	@BeforeClass
	public void setUp() {
		dataSource = new ConnectionPoolDataSource(DRIVER_CLASS, DB_URL, USERNAME, PASSWORD);
		dataSource.setLogger(new DBConnectionPoolLogger("org.strangeforest.db"));
		dataSource.init();
		db = new DBGateway(dataSource, new SQLs(ORMTest.class, "Schema.sqls"));
		try {
			db.executeDDL("Create_TSAggregate");
			db.executeDDL("Create_TSEntity");
			db.executeDDL("Create_TSDetail");
			db.executeDDL("Create_TSSimpleEntity");
			db.executeDDL("Create_XTSAggregate_IDTSAggregate");
			db.executeDDL("Create_XTSAggregate_IDTSEntity");
			db.executeDDL("Create_XTSEntity_IDTSAggregate");
			db.executeDDL("Create_XTSDetail_IDTSEntity");
			db.executeDDL("Create_FKTSAggregate_IDTSEntity");
			db.executeDDL("Create_FKTSAggregate_IDTSAggregate");
			db.executeDDL("Create_FKTSEntity_IDTSAggregate");
			db.executeDDL("Create_FKTSDetail_IDTSEntity");
		}
		catch (Throwable th) {
			th.printStackTrace();
			try {
				dropDBObjects();
			}
			catch (Throwable ignored) {}
		}

		LocalDomainContext context = new LocalDomainContext();
		aggregateManager = new LocalRepository<>(context, new TestAggregateDAO(dataSource));
		entityManager = new LocalRepository<>(context, new TestEntityDAO(dataSource));
		entityManager.setUsePredicatedQueryCache(true);
		simpleEntityManager = new LocalRepository<>(context, new SimpleTestEntityDAO(dataSource));
		aggregateManager.init();
		entityManager.init();
		simpleEntityManager.init();
	}

	@AfterSuite
	public void tearDown() {
		dropDBObjects();
//		System.out.println(dataSource.getStatistics().get("GetCount"));
		if (dataSource != null)
			dataSource.close();
	}

	private void dropDBObjects() {
		db.executeDDL("Drop_TSAggregate");
		db.executeDDL("Drop_TSEntity");
		db.executeDDL("Drop_TSDetail");
		db.executeDDL("Drop_TSSimpleEntity");
	}

	@Test
	public void testCreateAggregate1() {
		TestAggregate aggregate1 = new TestAggregate();
		aggregate1.setName("Aggregate1");
		aggregateManager.create(aggregate1);
		aggregate1Id = aggregate1.getId();
		assertNotNull(aggregate1Id);
	}

	@Test(dependsOnMethods = "testCreateAggregate1")
	public void testCreateAggregate2() {
		TestAggregate aggregate2 = new TestAggregate();
		aggregate2.setName("Aggregate2");
		aggregate2.setAggregateId(aggregate1Id);
		aggregateManager.create(aggregate2);
		aggregate2Id = aggregate2.getId();
		assertNotNull(aggregate2Id);
	}

	@Test(dependsOnMethods = "testCreateAggregate2")
	public void testAddEntity1() {
		TestAggregate aggregate1 = aggregateManager.get(aggregate1Id);
		TestEntity entity1 = new TestEntity();
		entity1.setName("Entity1");
		aggregate1.addEntity(entity1);
		aggregateManager.save(aggregate1);
		entityId = entity1.getId();
		assertNotNull(entityId);
	}

	@Test(dependsOnMethods = "testAddEntity1")
	public void updateEntity1() {
		TestEntity entity1 = entityManager.get(entityId);
		entity1.setName("Entity1a");
		entity1.setDescription("Trla baba lan da joj prodje dan...");
		entityManager.save(entity1);
	}

	@Test(dependsOnMethods = "updateEntity1")
	public void lockedUpdateEntity1() {
		entityManager.lockedUpdate(entityId, entity -> entity.setName("Entity1b"));
		assertEquals(entityManager.get(entityId).getName(), "Entity1b");
	}

	@Test(dependsOnMethods = "lockedUpdateEntity1")
	public void testAddEntity2() {
		TestEntity entity2 = new TestEntity();
		entity2.setAggregateId(aggregate1Id);
		entity2.setName("Entity2");
		entityManager.save(entity2);
		entity2Id = entity2.getId();
		assertNotNull(entity2Id);
		assertEquals(aggregateManager.get(aggregate1Id).getEntities().size(), 2);
	}

	@Test(dependsOnMethods = "testAddEntity2")
	public void updateEntity2() {
		TestEntity entity2 = entityManager.get(entity2Id);
		entity2.setName("Entity2a");
		TestDetail detail1 = new TestDetail(1);
		detail1.setName("Detail1");
		entity2.addDetail(detail1);
		TestDetail detail2 = new TestDetail(2);
		detail2.setName("Detail2");
		entity2.addDetail(detail2);
		entityManager.save(entity2);
		assertEquals(entity2.getDetails().size(), 2);
	}

	@Test(dependsOnMethods = "updateEntity2")
	public void queryAggregateByName() {
		TestAggregate aggregate = aggregateManager.get(TestAggregate.QUERY_BY_NAME("Aggregate1"));
		assertEquals(aggregate.getName(), "Aggregate1");
	}

	@Test(dependsOnMethods = "updateEntity2")
	public void queryEntityByName() {
		assertEquals(entityManager.getList(TestEntity.QUERY_FOR_NAME("Entity1b")).size(), 1);
		assertEquals(entityManager.getList(TestEntity.QUERY_FOR_NAME("Pera")).size(), 0);
	}

	@Test(dependsOnMethods = "updateEntity2")
	public void queryEntityByNameGetDetails() {
		TestEntity entity = entityManager.get(TestEntity.QUERY_FOR_NAME("Entity2a"));
		assertEquals(entity.getDetails().size(), 2);
		assertEquals(entityManager.get(entity.getId()).getDetails().size(), 2);
	}

	@Test(dependsOnMethods = "updateEntity2")
	public void queryEntityByAggregate() {
		assertEquals(entityManager.getList(TestEntity.QUERY_FOR_AGGREGATE(aggregate1Id)).size(), 2);
	}

	@Test
	public void testCreateSimpleEntity() {
		SimpleTestEntity entity = new SimpleTestEntity();
		entity.setName("Entity");
		simpleEntityManager.save(entity);
		simpleEntityId = entity.getId();
		assertNotNull(simpleEntityId);
	}

	@Test(dependsOnMethods = "testCreateSimpleEntity")
	public void updateSimpleEntity() {
		SimpleTestEntity entity = simpleEntityManager.get(simpleEntityId);
		entity.setName("Entity2");
		simpleEntityManager.save(entity);
		assertEquals(entity.getId(), simpleEntityId);
	}
}