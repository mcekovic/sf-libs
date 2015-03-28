package test.orm;

import java.util.*;

import org.strangeforest.orm.*;

import static org.strangeforest.orm.DomainContextUtil.*;

public class TestAggregate extends DomainEntity<Long, TestAggregate> {

	private String name;
	private List<TestEntity> entities;
	private EntityReference<Long, TestEntity> entityRef;
	private EntityReference<Long, TestAggregate> aggregateRef;

	public TestAggregate() {
		this(null);
	}

	public TestAggregate(Long id) {
		super(id);
		entityRef = new EntityReference<>(TestEntity.class);
		aggregateRef = new EntityReference<>(TestAggregate.class);
	}

	@Override public void setContext(DomainContext context) {
		super.setContext(context);
		if (context != null) {
			context.attach(entities);
			context.attach(entityRef);
			context.attach(aggregateRef);
		}
	}

	@Override public void setId(Long id) {
		super.setId(id);
		if (entities != null) {
			for (TestEntity entity : entities)
				entity.setAggregateId(id);
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	
	public List<TestEntity> getEntities() {
		if (entities == null)
			entities = id != null ? getEntityList(context, TestEntity.class, TestEntity.QUERY_FOR_AGGREGATE(id)) : getEmptyList(context, TestEntity.class);
		return entities;
	}

	public void setEntities(List<TestEntity> entities) {
		this.entities = getEntityList(context, TestEntity.class, entities);
		for (TestEntity entity : entities)
			entity.setAggregate(this);
	}

	public void addEntity(TestEntity entity) {
		getEntities().add(entity);
		entity.setAggregate(this);
	}

	public void removeEntity(TestEntity entity) {
		getEntities().remove(entity);
		entity.setAggregate(null);
	}


	public EntityReference<Long, TestEntity> getEntityRef() {
		return entityRef;
	}

	public Long getEntityId() {
		return entityRef.getId();
	}

	public void setEntityId(Long entityId) {
		entityRef.setId(entityId);
	}

	public TestEntity getEntity() {
		return entityRef.get();
	}

	public void setEntity(TestEntity entity) {
		entityRef.set(entity);
	}


	public EntityReference<Long, TestAggregate> getAggregateRef() {
		return aggregateRef;
	}

	public Long getAggregateId() {
		return aggregateRef.getId();
	}

	public void setAggregateId(Long aggregateId) {
		aggregateRef.setId(aggregateId);
	}

	public TestAggregate getAggregate() {
		return aggregateRef.get();
	}

	public void setAggregate(TestAggregate aggregate) {
		aggregateRef.set(aggregate);
	}


	// Queries

	public static Query QUERY_BY_NAME(String name) {
		return new Query("ByName", name);
	}


	// Object methods

	@Override protected TestAggregate clone() throws CloneNotSupportedException {
		TestAggregate aggregate = super.clone();
		aggregate.entities = null;
		aggregate.entityRef = entityRef.clone();
		aggregate.aggregateRef = aggregateRef.clone();
		return aggregate;
	}
}
