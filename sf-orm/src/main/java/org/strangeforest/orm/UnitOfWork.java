package org.strangeforest.orm;

import java.util.*;

import org.strangeforest.annotation.*;

public class UnitOfWork {

	private final Collection<DomainEntity> forCreate;
	private final Collection<DomainEntity> forSave;
	private final Collection<DomainEntity> forDelete;
	private final Collection<DomainEntity> forUnlock;

	public UnitOfWork() {
		super();
		forCreate = new LinkedHashSet<>();
		forSave = new LinkedHashSet<>();
		forDelete = new LinkedHashSet<>();
		forUnlock = new LinkedHashSet<>();
	}

	public UnitOfWork(UnitOfWork update) {
	    this();
	    merge(update);
	}
	
    public void registerForCreate(DomainEntity entity) {
		forCreate.add(entity);
	}

	public void registerForCreate(Collection<DomainEntity> entities) {
		forCreate.addAll(entities);
	}

	public void registerForSave(DomainEntity entity) {
		forSave.add(entity);
	}

	public void registerForSave(Collection<DomainEntity> entities) {
		forSave.addAll(entities);
	}

	public void registerForDelete(DomainEntity entity) {
		if (!forCreate.remove(entity)) {
			forSave.remove(entity);
			forDelete.add(entity);
		}
	}

	public void registerForDelete(Collection<DomainEntity> entities) {
		for (DomainEntity entity : entities)
			registerForDelete(entity);
	}

	public void registerForUnlock(DomainEntity entity) {
		forUnlock.add(entity);
	}

	public void registerForUnlock(Collection<DomainEntity> entities) {
		forUnlock.addAll(entities);
	}

	public int numberOfChanges() {
		return forCreate.size() + forSave.size() + forDelete.size();
	}

	public boolean isEmpty() {
		return forCreate.isEmpty() && forSave.isEmpty() && forDelete.isEmpty() && forUnlock.isEmpty();
	}

	public Collection<DomainEntity> forCreate() {
		return forCreate;
	}

	public Collection<DomainEntity> forSave() {
		return forSave;
	}

	public Collection<DomainEntity> forDelete() {
		return forDelete;
	}

	public Collection<DomainEntity> forUnlock() {
		return forUnlock;
	}

	public <E extends DomainEntity> Collection<E> forCreate(final Class<E> cls) {
		return selectForClass((Collection<E>)forCreate, cls);
	}

	public <E extends DomainEntity> Collection<E> forSave(final Class<E> cls) {
		return selectForClass((Collection<E>)forSave, cls);
	}

	public <E extends DomainEntity> Collection<E> forDelete(final Class<E> cls) {
		return selectForClass((Collection<E>)forDelete, cls);
	}

	public <E extends DomainEntity> Collection<E> forUnlock(final Class<E> cls) {
		return selectForClass((Collection<E>)forUnlock, cls);
	}

	private <E extends DomainEntity> Collection<E> selectForClass(Collection<E> entities, Class<E> cls) {
		List<E> selected = new ArrayList<>();
		for (E entity : entities) {
			if (cls.isInstance(entity))
				selected.add(entity);
		}
		return selected;
	}

	public void merge(UnitOfWork work) {
		for (DomainEntity entity : work.forCreate)
			registerForCreate(entity);
		for (DomainEntity entity : work.forSave)
			registerForSave(entity);
		for (DomainEntity entity : work.forDelete)
			registerForDelete(entity);
		for (DomainEntity entity : work.forUnlock)
			registerForUnlock(entity);
	}

	@Transactional
	public void commit(DomainContext context) {
		try {
			for (DomainEntity entity : forCreate)
				context.getRepository(entity.getClass()).create(entity);
			for (DomainEntity entity : forSave)
				context.getRepository(entity.getClass()).save(entity);
			for (DomainEntity entity : forDelete)
				context.getRepository(entity.getClass()).delete(entity.getId());
		}
		finally {
			unlock(context);
		}
		clear();
	}

	public void rollback(DomainContext context) {
		unlock(context);
		clear();
	}

	private void unlock(DomainContext context) {
		for (DomainEntity entity : forUnlock())
			context.getRepository(entity.getClass()).unlock(entity.getId());
	}

	private void clear() {
		forCreate.clear();
		forSave.clear();
		forDelete.clear();
		forUnlock.clear();
	}
}
