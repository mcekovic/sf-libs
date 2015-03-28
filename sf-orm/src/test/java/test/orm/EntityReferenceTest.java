package test.orm;

import org.testng.*;
import org.testng.annotations.*;

import org.strangeforest.orm.*;

@Test
public class EntityReferenceTest {

	@Test
	public void testIdsEqual() throws Exception {
		EntityReference<Long, TestEntity> ref1 = new EntityReference<>(TestEntity.class);
		ref1.setId(5L);
		EntityReference<Long, TestEntity> ref2 = new EntityReference<>(TestEntity.class);
		ref2.setId(5L);
		Assert.assertEquals(ref1, ref2);
		Assert.assertEquals(ref1.hashCode(), ref2.hashCode());
		Assert.assertTrue(ref1.isSame(ref2));

		EntityReference<Long, TestEntity> ref3 = new EntityReference<>(TestEntity.class);
		ref3.set(new TestEntity(5L));
		EntityReference<Long, TestEntity> ref4 = new EntityReference<>(TestEntity.class);
		ref4.set(new TestEntity(5L));
		Assert.assertEquals(ref3, ref4);
		Assert.assertEquals(ref3.hashCode(), ref4.hashCode());
		Assert.assertTrue(ref3.isSame(ref4));
	}

	@Test
	public void testIdsNotEqual() throws Exception {
		EntityReference<Long, TestEntity> ref1 = new EntityReference<>(TestEntity.class);
		ref1.setId(5L);
		EntityReference<Long, TestEntity> ref2 = new EntityReference<>(TestEntity.class);
		ref2.setId(6L);
		Assert.assertFalse(ref1.equals(ref2));
		Assert.assertFalse(ref1.isSame(ref2));
		
		EntityReference<Long, TestEntity> ref3 = new EntityReference<>(TestEntity.class);
		ref3.set(new TestEntity(5L));
		EntityReference<Long, TestEntity> ref4 = new EntityReference<>(TestEntity.class);
		ref4.set(new TestEntity(6L));
		Assert.assertFalse(ref3.equals(ref4));
		Assert.assertFalse(ref3.isSame(ref4));
	}

	@Test
	public void testNullsEqual() throws Exception {
		EntityReference<Long, TestEntity> ref1 = new EntityReference<>(TestEntity.class);
		EntityReference<Long, TestEntity> ref2 = new EntityReference<>(TestEntity.class);
		Assert.assertEquals(ref1, ref2);
		Assert.assertEquals(ref1.hashCode(), ref2.hashCode());
		Assert.assertFalse(ref1.isSame(ref2));
	}

	@Test
	public void testNullNotEqual() throws Exception {
		EntityReference<Long, TestEntity> ref1 = new EntityReference<>(TestEntity.class);
		ref1.setId(5L);
		EntityReference<Long, TestEntity> ref2 = new EntityReference<>(TestEntity.class);
		Assert.assertFalse(ref1.equals(ref2));
		Assert.assertFalse(ref1.isSame(ref2));
	}


	@Test
	public void testEntitiesWOIdsNotEqual() throws Exception {
		EntityReference<Long, TestEntity> ref1 = new EntityReference<>(TestEntity.class);
		ref1.set(new TestEntity());
		EntityReference<Long, TestEntity> ref2 = new EntityReference<>(TestEntity.class);
		ref2.set(new TestEntity());
		Assert.assertFalse(ref1.equals(ref2));
		Assert.assertFalse(ref1.isSame(ref2));
	}
}
