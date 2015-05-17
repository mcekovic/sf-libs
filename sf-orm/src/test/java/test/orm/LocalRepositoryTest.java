package test.orm;

import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;
import org.mockito.runners.*;
import org.strangeforest.orm.*;
import org.strangeforest.transaction.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class LocalRepositoryTest {

	private LocalRepository<Long, SimpleTestEntity> repository;
	@Mock private EntityDAO<Long, SimpleTestEntity> dao;

	@Before
	public void setUp() {
		when(dao.entityClass()).thenReturn(SimpleTestEntity.class);
		repository = new TransactionalRepository<>(new LocalDomainContext(), dao);
		repository.init();

		verify(dao).entityClass();
	}


	// Get entity

	@Test
	public void entityIsFetchedAndGet() {
		SimpleTestEntity entity = new SimpleTestEntity(1L);
		entity.setName("Entity");

		when(dao.fetch(1L)).thenReturn(entity);

		SimpleTestEntity entity2 = repository.get(1L);

		verify(dao).fetch(1L);
		assertThat(entity2).isNotSameAs(entity);

		SimpleTestEntity entity3 = repository.get(entity.getId());

		verifyNoMoreInteractions(dao);
		assertThat(entity3).isNotSameAs(entity);
		assertThat(entity3).isNotSameAs(entity2);
	}

	@Test
	public void entityIsFetchedAndGetInTheSameTransaction() {
		TransactionManager.execute(tran -> {
			SimpleTestEntity entity = new SimpleTestEntity(1L);
			entity.setName("Entity");

			when(dao.fetch(1L)).thenReturn(entity);

			SimpleTestEntity entity2 = repository.get(1L);

			verify(dao).fetch(1L);
			assertThat(entity2).isNotSameAs(entity);

			SimpleTestEntity entity3 = repository.get(entity.getId());

			verifyNoMoreInteractions(dao);
			assertThat(entity3).isNotSameAs(entity);
			assertThat(entity3).isSameAs(entity2);
			return null;
		});
	}


	// Create new Entity

	@Test
	public void newEntityWithNoIdIsCreatedAndGet() {
		SimpleTestEntity entity = new SimpleTestEntity();
		entity.setName("Entity");

		doAnswer(invocationOnMock -> {
			invocationOnMock.getArgumentAt(0, SimpleTestEntity.class).setId(1L);
			return null;
		}).when(dao).create(entity);

		repository.create(entity);

		verify(dao).create(entity);
		assertThat(entity.getId()).isEqualTo(1L);

		SimpleTestEntity entity2 = repository.get(entity.getId());

		verifyNoMoreInteractions(dao);
		assertThat(entity2.getName()).isEqualTo(entity.getName());
		assertThat(entity2).isNotSameAs(entity);
	}

	@Test
	public void newEntityWithNoIdIsCreatedAndGetInTheSameTransaction() {
		TransactionManager.execute(tran -> {
			SimpleTestEntity entity = new SimpleTestEntity();
			entity.setName("Entity");

			doAnswer(invocationOnMock -> {
				invocationOnMock.getArgumentAt(0, SimpleTestEntity.class).setId(1L);
				return null;
			}).when(dao).create(entity);

			repository.create(entity);

			verify(dao).create(entity);
			assertThat(entity.getId()).isEqualTo(1L);

			SimpleTestEntity entity2 = repository.get(entity.getId());

			verifyNoMoreInteractions(dao);
			assertThat(entity2.getName()).isEqualTo(entity.getName());
			assertThat(entity2).isSameAs(entity);
			return null;
		});
	}

	@Test
	public void newEntityWithIdIsCreatedAndGet() {
		SimpleTestEntity entity = new SimpleTestEntity(2L);
		entity.setName("Entity2");

		repository.create(entity);

		verify(dao).create(entity);

		SimpleTestEntity entity2 = repository.get(entity.getId());

		verifyNoMoreInteractions(dao);
		assertThat(entity2.getName()).isEqualTo(entity.getName());
		assertThat(entity2).isNotSameAs(entity);
	}

	@Test
	public void newEntityWithIdIsCreatedAndGetInTheSameTransaction() {
		TransactionManager.execute(tran -> {
			SimpleTestEntity entity = new SimpleTestEntity(2L);
			entity.setName("Entity2");

			repository.create(entity);

			verify(dao).create(entity);

			SimpleTestEntity entity2 = repository.get(entity.getId());

			verifyNoMoreInteractions(dao);
			assertThat(entity2.getName()).isEqualTo(entity.getName());
			assertThat(entity2).isSameAs(entity);
			return null;
		});
	}


	// Save new Entity

	@Test
	public void newEntityWithNoIdIsSavedAndGet() {
		SimpleTestEntity entity = new SimpleTestEntity();
		entity.setName("Entity");

		doAnswer(invocationOnMock -> {
			invocationOnMock.getArgumentAt(0, SimpleTestEntity.class).setId(1L);
			return null;
		}).when(dao).create(entity);

		repository.save(entity);

		verify(dao).create(entity);
		assertThat(entity.getId()).isEqualTo(1L);

		SimpleTestEntity entity2 = repository.get(entity.getId());

		verifyNoMoreInteractions(dao);
		assertThat(entity2.getName()).isEqualTo(entity.getName());
		assertThat(entity2).isNotSameAs(entity);
	}

	@Test
	public void newEntityWithNoIdIsSavedAndGetInTheSameTransaction() {
		TransactionManager.execute(tran -> {
			SimpleTestEntity entity = new SimpleTestEntity();
			entity.setName("Entity");

			doAnswer(invocationOnMock -> {
				invocationOnMock.getArgumentAt(0, SimpleTestEntity.class).setId(1L);
				return null;
			}).when(dao).create(entity);

			repository.save(entity);

			verify(dao).create(entity);
			assertThat(entity.getId()).isEqualTo(1L);

			SimpleTestEntity entity2 = repository.get(entity.getId());

			verifyNoMoreInteractions(dao);
			assertThat(entity2.getName()).isEqualTo(entity.getName());
			assertThat(entity2).isSameAs(entity);
			return null;
		});
	}

	@Test
	public void newEntityWithIdIsSavedAndGet() {
		SimpleTestEntity entity = new SimpleTestEntity(2L);
		entity.setName("Entity2");

		repository.save(entity);

		verify(dao).fetch(2L);
		verify(dao).save(entity, null);

		SimpleTestEntity entity2 = repository.get(entity.getId());

		verifyNoMoreInteractions(dao);
		assertThat(entity2.getName()).isEqualTo(entity.getName());
		assertThat(entity2).isNotSameAs(entity);
	}

	@Test
	public void newEntityWithIdIsSavedAndGetInTheSameTransaction() {
		TransactionManager.execute(tran -> {
			SimpleTestEntity entity = new SimpleTestEntity(2L);
			entity.setName("Entity2");

			repository.save(entity);

			verify(dao).fetch(2L);
			verify(dao).save(entity, null);

			SimpleTestEntity entity2 = repository.get(entity.getId());

			verifyNoMoreInteractions(dao);
			assertThat(entity2.getName()).isEqualTo(entity.getName());
			assertThat(entity2).isSameAs(entity);
			return null;
		});
	}


	// Save existing Entity

	@Test
	public void exitingEntityIsSavedAndGet() {
		SimpleTestEntity oldEntity = new SimpleTestEntity(3L);
		oldEntity.setName("Entity2");
		SimpleTestEntity entity = new SimpleTestEntity(3L);
		entity.setName("Entity3");

		when(dao.fetch(3L)).thenReturn(oldEntity);

		repository.save(entity);

		verify(dao).fetch(3L);
		verify(dao).save(entity, oldEntity);

		SimpleTestEntity entity2 = repository.get(entity.getId());

		verifyNoMoreInteractions(dao);
		assertThat(entity2.getName()).isEqualTo(entity.getName());
		assertThat(entity2).isNotSameAs(entity);
	}

	@Test
	public void exitingEntityIsSavedAndGetInTheSameTransaction() {
		TransactionManager.execute(tran -> {
			SimpleTestEntity oldEntity = new SimpleTestEntity(3L);
			oldEntity.setName("Entity2");
			SimpleTestEntity entity = new SimpleTestEntity(3L);
			entity.setName("Entity3");

			when(dao.fetch(3L)).thenReturn(oldEntity);

			repository.save(entity);

			verify(dao).fetch(3L);
			verify(dao).save(entity, oldEntity);

			SimpleTestEntity entity2 = repository.get(entity.getId());

			verifyNoMoreInteractions(dao);
			assertThat(entity2.getName()).isEqualTo(entity.getName());
			assertThat(entity2).isSameAs(entity);
			return null;
		});
	}


	// Delete existing Entity

	@Test
	public void exitingEntityIsDeletedAndTriedToFind() {
		SimpleTestEntity entity = new SimpleTestEntity(4L);
		entity.setName("Entity4");

		repository.create(entity);

		verify(dao).create(entity);

		repository.delete(4L);

		verify(dao).delete(4L);

		SimpleTestEntity entity2 = repository.find(4L);

		verify(dao).fetch(4L);
		verifyNoMoreInteractions(dao);
		assertThat(entity2).isNull();
	}

	@Test
	public void exitingEntityIsDeletedAndTriedToFindInTheSameTransaction() {
		SimpleTestEntity entity = new SimpleTestEntity(4L);
		entity.setName("Entity4");

		repository.create(entity);

		verify(dao).create(entity);

		TransactionManager.execute(tran -> {
			repository.delete(4L);

			verify(dao).delete(4L);

			SimpleTestEntity entity2 = repository.find(4L);

			verify(dao).fetch(4L);
			verifyNoMoreInteractions(dao);
			assertThat(entity2).isNull();
			return null;
		});
	}
}
