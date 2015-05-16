package test.orm;

import org.junit.*;
import org.strangeforest.orm.*;

import static org.assertj.core.api.Assertions.*;
import static org.strangeforest.orm.EqualsByValueUtil.*;
import static test.orm.TestMakers.*;

public class EqualsByValueTest {

	@Test
	public void sameInstancesAreEqualByValue() {
		TestEntity e1 = makeEntity();
		assertThat(equalByValue(e1, e1)).isTrue();
	}

	@Test
	public void nullsAreEqualByValue() {
		assertThat(equalByValue((EquatableByValue)null, null)).isTrue();
	}

	@Test
	public void nullIsNotEqualByValueWithAnInstance() {
		TestEntity e1 = makeEntity();
		assertThat(equalByValue(e1, null)).isFalse();
		assertThat(equalByValue(null, e1)).isFalse();
	}

	@Test
	public void sameEntityValuesAreEqualByValue() {
		TestEntity e1 = makeEntity();
		TestEntity e2 = makeEntity();
		assertThat(equalByValue(e1, e2)).isTrue();
	}

	@Test
	public void differentEntityValuesAreNotEqualByValue() {
		TestEntity e1 = makeEntity();
		TestEntity e2 = makeEntity();
		TestEntity e3 = makeEntity();
		e2.setDescription("Different");
		e3.getDetails().get(0).setName("Different");
		assertThat(equalByValue(e1, e2)).isFalse();
		assertThat(equalByValue(e1, e3)).isFalse();
	}

	@Test
	public void sameAggregateValuesAreEqualByValue() {
		TestAggregate a1 = makeAggregate();
		TestAggregate a2 = makeAggregate();
		assertThat(equalByValue(a1, a2)).isTrue();
	}

	@Test
	public void differentAggregateValuesAreNotEqualByValue() {
		TestAggregate a1 = makeAggregate();
		TestAggregate a2 = makeAggregate();
		TestAggregate a3 = makeAggregate();
		TestAggregate a4 = makeAggregate();
		a2.setName("Different");
		TestEntity de = makeEntity();
		de.setId(de.getId() + 1L);
		a3.setEntity(de);
		a4.getEntities().clear();
		assertThat(equalByValue(a1, a2)).isFalse();
		assertThat(equalByValue(a1, a3)).isFalse();
		assertThat(equalByValue(a1, a4)).isFalse();
	}
}
