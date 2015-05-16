package test.orm;

import org.junit.*;
import org.strangeforest.orm.*;

import static org.assertj.core.api.Assertions.*;

public class EntityReferenceTest {

	@Test
	public void testIdsEqual() throws Exception {
		EntityReference<Long, TestEntity> ref1 = new EntityReference<>(TestEntity.class);
		ref1.setId(5L);
		EntityReference<Long, TestEntity> ref2 = new EntityReference<>(TestEntity.class);
		ref2.setId(5L);
		assertThat(ref1).isEqualTo(ref2);
		assertThat(ref1.hashCode()).isEqualTo(ref2.hashCode());
		assertThat(ref1.isSame(ref2)).isTrue();

		EntityReference<Long, TestEntity> ref3 = new EntityReference<>(TestEntity.class);
		ref3.set(new TestEntity(5L));
		EntityReference<Long, TestEntity> ref4 = new EntityReference<>(TestEntity.class);
		ref4.set(new TestEntity(5L));
		assertThat(ref3).isEqualTo(ref4);
		assertThat(ref3.hashCode()).isEqualTo(ref4.hashCode());
		assertThat(ref3.isSame(ref4)).isTrue();
	}

	@Test
	public void testIdsNotEqual() throws Exception {
		EntityReference<Long, TestEntity> ref1 = new EntityReference<>(TestEntity.class);
		ref1.setId(5L);
		EntityReference<Long, TestEntity> ref2 = new EntityReference<>(TestEntity.class);
		ref2.setId(6L);
		assertThat(ref1).isNotEqualTo(ref2);
		assertThat(ref1.isSame(ref2)).isFalse();

		EntityReference<Long, TestEntity> ref3 = new EntityReference<>(TestEntity.class);
		ref3.set(new TestEntity(5L));
		EntityReference<Long, TestEntity> ref4 = new EntityReference<>(TestEntity.class);
		ref4.set(new TestEntity(6L));
		assertThat(ref3).isNotEqualTo(ref4);
		assertThat(ref3.isSame(ref4)).isFalse();
	}

	@Test
	public void testNullsEqual() throws Exception {
		EntityReference<Long, TestEntity> ref1 = new EntityReference<>(TestEntity.class);
		EntityReference<Long, TestEntity> ref2 = new EntityReference<>(TestEntity.class);
		assertThat(ref1).isEqualTo(ref2);
		assertThat(ref1.hashCode()).isEqualTo(ref2.hashCode());
		assertThat(ref1.isSame(ref2)).isFalse();
	}

	@Test
	public void testNullNotEqual() throws Exception {
		EntityReference<Long, TestEntity> ref1 = new EntityReference<>(TestEntity.class);
		ref1.setId(5L);
		EntityReference<Long, TestEntity> ref2 = new EntityReference<>(TestEntity.class);
		assertThat(ref1).isNotEqualTo(ref2);
		assertThat(ref1.isSame(ref2)).isFalse();
	}

	@Test
	public void testEntitiesWOIdsNotEqual() throws Exception {
		EntityReference<Long, TestEntity> ref1 = new EntityReference<>(TestEntity.class);
		ref1.set(new TestEntity());
		EntityReference<Long, TestEntity> ref2 = new EntityReference<>(TestEntity.class);
		ref2.set(new TestEntity());
		assertThat(ref1).isNotEqualTo(ref2);
		assertThat(ref1.isSame(ref2)).isFalse();
	}
}
