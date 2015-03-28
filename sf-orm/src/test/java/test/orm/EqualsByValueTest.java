package test.orm;

import org.testng.annotations.*;

import org.strangeforest.orm.*;

import static org.testng.Assert.*;
import static test.orm.TestMakers.*;

@Test
public class EqualsByValueTest {

	@Test
	public void sameInstancesAreEqualByValue() {
		TestEntity e1 = makeEntity();
		assertTrue(EqualsByValueUtil.equalByValue(e1, e1));
	}

	@Test
	public void nullsAreEqualByValue() {
		assertTrue(EqualsByValueUtil.equalByValue((EquatableByValue)null, null));
	}

	@Test
	public void nullIsNotEqualByValueWithAnInstance() {
		TestEntity e1 = makeEntity();
		assertFalse(EqualsByValueUtil.equalByValue(e1, null));
		assertFalse(EqualsByValueUtil.equalByValue(null, e1));
	}

	@Test
	public void sameEntityValuesAreEqualByValue() {
		TestEntity e1 = makeEntity();
		TestEntity e2 = makeEntity();
		assertTrue(EqualsByValueUtil.equalByValue(e1, e2));
	}

	@Test
	public void differentEntityValuesAreNotEqualByValue() {
		TestEntity e1 = makeEntity();
		TestEntity e2 = makeEntity();
		TestEntity e3 = makeEntity();
		e2.setDescription("Different");
		e3.getDetails().get(0).setName("Different");
		assertFalse(EqualsByValueUtil.equalByValue(e1, e2));
		assertFalse(EqualsByValueUtil.equalByValue(e1, e3));
	}

	@Test
	public void sameAggregateValuesAreEqualByValue() {
		TestAggregate a1 = makeAggregate();
		TestAggregate a2 = makeAggregate();
		assertTrue(EqualsByValueUtil.equalByValue(a1, a2));
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
		assertFalse(EqualsByValueUtil.equalByValue(a1, a2));
		assertFalse(EqualsByValueUtil.equalByValue(a1, a3));
		assertFalse(EqualsByValueUtil.equalByValue(a1, a4));
	}
}
