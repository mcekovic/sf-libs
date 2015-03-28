package org.strangeforest.orm;

import java.util.function.*;

import org.strangeforest.annotation.*;

public class TransactionalRepository<I, E extends DomainEntity<I, E>> extends LocalRepository<I, E> {

	public TransactionalRepository(LocalDomainContext context, EntityDAO<I, E> dao) {
		super(context, dao);
	}

	public TransactionalRepository(LocalDomainContext context, EntityDAO<I, E> dao, boolean useCache, boolean useQueryCache, boolean usePredicatedQueryCache) {
		super(context, dao, useCache, useQueryCache, usePredicatedQueryCache);
	}

	@Transactional
	@Override public void create(E entity) {
		super.create(entity);
	}

	@Transactional
	@Override public void create(Iterable<E> entities) {
		super.create(entities);
	}

	@Transactional
	@Override public void save(E entity) throws OptimisticLockingException {
		super.save(entity);
	}

	@Transactional
	@Override public void save(E entity, E optLockedEntity) throws OptimisticLockingException {
		super.save(entity, optLockedEntity);
	}

	@Transactional
	@Override public void save(Iterable<E> entities, Iterable<E> oldEntities) throws OptimisticLockingException {
		super.save(entities, oldEntities);
	}

	@Transactional
	@Override public void delete(I id) {
		super.delete(id);
	}

	@Transactional
	@Override public void delete(E entity, E optLockedEntity) throws OptimisticLockingException {
		super.delete(entity, optLockedEntity);
	}

	@Transactional
	@Override public void deleteAll() {
		super.deleteAll();
	}

	@Transactional
	@Override protected E doLockedUpdate(I id, Consumer<E> callback) throws OptimisticLockingException {
		return super.doLockedUpdate(id, callback);
	}
}
