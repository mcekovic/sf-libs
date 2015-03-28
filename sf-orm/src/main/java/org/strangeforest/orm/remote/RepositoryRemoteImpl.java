package org.strangeforest.orm.remote;

import java.rmi.*;
import java.rmi.server.*;
import java.util.*;
import java.util.concurrent.*;

import org.strangeforest.annotation.*;
import org.strangeforest.orm.*;

@Traceable(logger = "org.strangeforest.orm.remote", trackTime = true, trackPerformance = true)
public class RepositoryRemoteImpl<I, E extends DomainEntity<I, E>> extends UnicastRemoteObject implements RepositoryRemote<I, E> {

	private final DomainContext context;
	private final Repository<I, E> repository;

	public RepositoryRemoteImpl(DomainContext context, Repository<I, E> repository, int port) throws RemoteException {
		super(port);
		this.context = context;
		this.repository = repository;
	}

	@Traceable
	@Override public boolean exists(I id) {
		return repository.exists(id);
	}

	@Traceable
	@Override public E find(I id) {
		return repository.find(id);
	}

	@Traceable
	@Override public E get(I id) {
		return repository.get(id);
	}

	@Traceable
	@Override public boolean exists(Query query) {
		return repository.exists(query);
	}

	@Traceable
	@Override public E find(Query query) {
		return repository.find(query);
	}

	@Traceable
	@Override public E get(Query query) {
		return repository.get(query);
	}

	@Traceable
	@Override public List<E> getList(Query query) {
		return repository.getList(query);
	}

	@Traceable
	@Override public <D> List<D> getDetails(String detailName, I id, FieldAccessor<E, List<D>> accessor) {
		return repository.getDetails(detailName, id, accessor);
	}

	@Traceable
	@Override public E create(E entity) {
		repository.create(entity);
		return entity;
	}

	@Traceable
	@Override public Iterable<E> create(Iterable<E> entities) {
		repository.create(entities);
		return entities;
	}

	@Traceable
	@Override public Iterable<E> create(Iterable<E> entities, Query query) {
		repository.create(entities, query);
		return entities;
	}

	@Traceable
	@Override public E save(E entity) {
		context.attach(entity);
		repository.save(entity);
		return entity;
	}

	@Traceable
	@Override public E save(E entity, E optLockedEntity) throws OptimisticLockingException {
		context.attach(entity);
		repository.save(entity, optLockedEntity);
		return entity;
	}

	@Traceable
	@Override public Iterable<E> save(Iterable<E> entities) {
		context.attach(entities);
		repository.save(entities);
		return entities;
	}

	@Traceable
	@Override public Iterable<E> save(Iterable<E> entities, Iterable<E> oldEntities) {
		context.attach(entities);
		repository.save(entities, oldEntities);
		return entities;
	}

	@Traceable
	@Override public Iterable<E> save(Iterable<E> entities, Iterable<E> oldEntities, Query query) {
		context.attach(entities);
		repository.save(entities, oldEntities, query);
		return entities;
	}

	@Traceable
	@Override public void delete(I id) {
		repository.delete(id);
	}

	@Traceable
	@Override public void delete(E entity, E optLockedEntity) throws OptimisticLockingException {
		repository.delete(entity, optLockedEntity);
	}

	@Traceable
	@Override public void deleteAll() {
		repository.deleteAll();
	}

	@Traceable
	@Override public void lock(I id) {
		repository.lock(id);
	}

	@Traceable
	@Override public void lockInterruptibly(I id) throws InterruptedException {
		repository.lockInterruptibly(id);
	}

	@Traceable
	@Override public boolean tryLock(I id) {
		return repository.tryLock(id);
	}

	@Traceable
	@Override public boolean tryLock(I id, long timeout, TimeUnit unit) throws InterruptedException {
		return repository.tryLock(id, timeout, unit);
	}

	@Traceable
	@Override public void unlock(I id) {
		repository.unlock(id);
	}

	@Traceable
	@Override public boolean isLocked(I id) {
		return repository.isLocked(id);
	}

	@Traceable
	@Override public void evict(I id) {
		repository.evict(id);
	}

	@Traceable
	@Override public void evict(Query query) {
		repository.evict(query);
	}

	@Traceable
	@Override public void evictEntities() {
		repository.evictEntities();
	}

	@Traceable
	@Override public void evictQueries() {
		repository.evictQueries();
	}

	@Traceable
	@Override public void evictAll() {
		repository.evictAll();
	}
}
