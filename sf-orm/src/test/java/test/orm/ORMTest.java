package test.orm;

import org.strangeforest.db.*;
import org.strangeforest.db.gateway.*;
import org.strangeforest.db.logging.*;
import org.strangeforest.orm.*;
import org.strangeforest.transaction.*;
import org.testng.annotations.*;

import test.orm.db.*;

import static org.assertj.core.api.Assertions.*;

@Test
public class ORMTest {

	private ConnectionPoolDataSource dataSource;
	private DBGateway db;
	private LocalRepository<Long, TestAggregate> aggregateRepository;
	private LocalRepository<Long, TestEntity> entityRepository;
	private LocalRepository<Long, SimpleTestEntity> simpleEntityRepository;

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
		aggregateRepository = new TransactionalRepository<>(context, new TestAggregateDAO(dataSource));
		entityRepository = new TransactionalRepository<>(context, new TestEntityDAO(dataSource));
		entityRepository.setUsePredicatedQueryCache(true);
		simpleEntityRepository = new LocalRepository<>(context, new SimpleTestEntityDAO(dataSource));
		aggregateRepository.init();
		entityRepository.init();
		simpleEntityRepository.init();
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
		aggregateRepository.create(aggregate1);
		aggregate1Id = aggregate1.getId();
		assertThat(aggregate1Id).isNotNull();
	}

	@Test(dependsOnMethods = "testCreateAggregate1")
	public void testCreateAggregate2() {
		TestAggregate aggregate2 = new TestAggregate();
		aggregate2.setName("Aggregate2");
		aggregate2.setAggregateId(aggregate1Id);
		aggregateRepository.create(aggregate2);
		aggregate2Id = aggregate2.getId();
		assertThat(aggregate2Id).isNotNull();
	}

	@Test(dependsOnMethods = "testCreateAggregate2")
	public void testAddEntity1() {
		TestAggregate aggregate1 = aggregateRepository.get(aggregate1Id);
		TestEntity entity1 = new TestEntity();
		entity1.setName("Entity1");
		aggregate1.addEntity(entity1);
		aggregateRepository.save(aggregate1);
		entityId = entity1.getId();
		assertThat(entityId).isNotNull();
	}

	@Test(dependsOnMethods = "testAddEntity1")
	public void updateEntity1() {
		TestEntity entity1 = entityRepository.get(entityId);
		entity1.setName("Entity1a");
		entity1.setDescription("Trla baba lan da joj prodje dan...");
		entityRepository.save(entity1);
	}

	@Test(dependsOnMethods = "updateEntity1")
	public void lockedUpdateEntity1() {
		entityRepository.lockedUpdate(entityId, entity -> entity.setName("Entity1b"));
		assertThat(entityRepository.get(entityId).getName()).isEqualTo("Entity1b");
	}

	@Test(dependsOnMethods = "lockedUpdateEntity1")
	public void testAddEntity2() {
		TestEntity entity2 = new TestEntity();
		entity2.setAggregateId(aggregate1Id);
		entity2.setName("Entity2");
		entityRepository.save(entity2);
		entity2Id = entity2.getId();
		assertThat(entity2Id).isNotNull();
		assertThat(aggregateRepository.get(aggregate1Id).getEntities()).hasSize(2);
	}

	@Test(dependsOnMethods = "testAddEntity2")
	public void updateEntity2() {
		TestEntity entity2 = entityRepository.get(entity2Id);
		entity2.setName("Entity2a");
		TestDetail detail1 = new TestDetail(1);
		detail1.setName("Detail1");
		entity2.addDetail(detail1);
		TestDetail detail2 = new TestDetail(2);
		detail2.setName("Detail2");
		entity2.addDetail(detail2);
		entityRepository.save(entity2);
		assertThat(entity2.getDetails()).hasSize(2);
	}

	@Test(dependsOnMethods = "updateEntity2")
	public void queryAggregateByName() {
		TestAggregate aggregate = aggregateRepository.get(TestAggregate.QUERY_BY_NAME("Aggregate1"));
		assertThat(aggregate.getName()).isEqualTo("Aggregate1");
	}

	@Test(dependsOnMethods = "updateEntity2")
	public void queryEntityByName() {
		assertThat(entityRepository.getList(TestEntity.QUERY_FOR_NAME("Entity1b"))).hasSize(1);
		assertThat(entityRepository.getList(TestEntity.QUERY_FOR_NAME("Pera"))).hasSize(0);
	}

	@Test(dependsOnMethods = "updateEntity2")
	public void queryEntityByNameGetDetails() {
		TestEntity entity = entityRepository.get(TestEntity.QUERY_FOR_NAME("Entity2a"));
		assertThat(entity.getDetails()).hasSize(2);
		assertThat(entityRepository.get(entity.getId()).getDetails()).hasSize(2);
	}

	@Test(dependsOnMethods = "updateEntity2")
	public void queryEntityByAggregate() {
		assertThat(entityRepository.getList(TestEntity.QUERY_FOR_AGGREGATE(aggregate1Id))).hasSize(2);
	}

	@Test
	public void testCreateSimpleEntity() {
		SimpleTestEntity entity = new SimpleTestEntity();
		entity.setName("Entity");
		simpleEntityRepository.save(entity);
		simpleEntityId = entity.getId();
		assertThat(simpleEntityId).isNotNull();
	}

	@Test(dependsOnMethods = "testCreateSimpleEntity")
	public void updateSimpleEntity() {
		SimpleTestEntity entity = simpleEntityRepository.get(simpleEntityId);
		entity.setName("Entity2");
		simpleEntityRepository.save(entity);
		assertThat(simpleEntityRepository.get(simpleEntityId).getName()).isEqualTo("Entity2");
		simpleEntityRepository.evict(simpleEntityId);
		assertThat(simpleEntityRepository.get(simpleEntityId).getName()).isEqualTo("Entity2");
	}

	@Test(dependsOnMethods = "updateSimpleEntity")
	public void transactionRollbackDoesNotPolluteTheCache() {
		TransactionManager.execute(tran -> {
			SimpleTestEntity entity = simpleEntityRepository.get(simpleEntityId);
			entity.setName("Entity3");
			simpleEntityRepository.save(entity);
			tran.setRollbackOnly();
			return null;
		});
		assertThat(simpleEntityRepository.get(simpleEntityId).getName()).isEqualTo("Entity2");
		simpleEntityRepository.evict(simpleEntityId);
		assertThat(simpleEntityRepository.get(simpleEntityId).getName()).isEqualTo("Entity2");
	}
}
