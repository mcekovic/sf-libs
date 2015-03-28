package test.orm;

public class TestMakers {

	public static TestEntity makeEntity() {
		TestEntity e1 = new TestEntity(10L);
		e1.setName("Name");
		e1.setDescription("Description");
		e1.addDetail(makeDetail());
		e1.addDetail(makeDetail());
		return e1;
	}

	public static TestDetail makeDetail() {
		TestDetail detail = new TestDetail(1);
		detail.setName("Detail Name");
		return detail;
	}

	public static TestAggregate makeAggregate() {
		return makeAggregate(5L);
	}

	public static TestAggregate makeAggregate(Long id) {
		TestAggregate a1 = new TestAggregate(id);
		a1.setName("Name");
		a1.setEntity(makeEntity());
		a1.addEntity(makeEntity());
		a1.addEntity(makeEntity());
		return a1;
	}
}
