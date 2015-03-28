package test.orm;

import java.util.*;

import org.strangeforest.orm.*;

import static org.strangeforest.orm.CloneUtil.*;
import static org.strangeforest.orm.DomainContextUtil.*;

public class TestEntity extends DomainEntity<Long, TestEntity> {

	private EntityReference<Long, TestAggregate> aggregateRef;
	private String name;
	private String description;
	private List<TestDetail> details;

	public TestEntity() {
		this(null);
	}

	public TestEntity(Long id) {
		super(id);
		aggregateRef = new EntityReference<>(TestAggregate.class);
	}

	@Override public void setContext(DomainContext context) {
		super.setContext(context);
		if (context != null)
			context.attach(aggregateRef);
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	private static final FieldAccessor<TestEntity, List<TestDetail>> DETAILS_ACCESSOR = new ReflectionFieldAccessor<>(TestEntity.class, "details");

	public List<TestDetail> getDetails() {
		if (details == null)
			details = getDetailList(context, TestEntity.class, TestDetail.class.getSimpleName(), id, DETAILS_ACCESSOR);
		return details;
	}

	public void addDetail(TestDetail detail) {
		getDetails().add(detail);
	}

	public void setDetails(List<TestDetail> details) {
		this.details = details;
	}


	// Queries

	public static Query QUERY_FOR_NAME(String name) {
		return new Query("ForName", name);
	}

	public static Query QUERY_FOR_AGGREGATE(final long aggregateId) {
		return new OrderedByIdPredicatedQuery<Long, TestEntity>("ForAggregate", aggregateId) {
			@Override public boolean test(TestEntity entity) {
				return Objects.equals(entity.getAggregateId(), aggregateId);
			}
		};
	}


	// Object methods

	@Override protected TestEntity clone() throws CloneNotSupportedException {
		TestEntity entity = super.clone();
		entity.aggregateRef = aggregateRef.clone();
		entity.details = cloneList(details);
		return entity;
	}
}
