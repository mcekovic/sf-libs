package org.strangeforest.orm;

import java.util.*;
import java.util.function.*;

import org.strangeforest.concurrent.*;

public interface Repository<I, E extends DomainEntity<I, E>> extends LockManager<I> {

	String getEntityName();

	boolean exists(I id);
	E find(I id);
	E get(I id);
	boolean exists(Query query);
	E find(Query query);
	E get(Query query);
	List<E> getList(Query query);
	void iterate(Query query, Consumer<E> callback);
	<D> List<D> getDetails(String detailName, I id, FieldAccessor<E, List<D>> accessor);

	void create(E entity);
	void create(Iterable<E> entities);
	void create(Iterable<E> entities, Query query);
	void save(E entity) throws OptimisticLockingException;
	void save(E entity, E optLockedEntity) throws OptimisticLockingException;
	void save(Iterable<E> entities) throws OptimisticLockingException;
	void save(Iterable<E> entities, Iterable<E> oldEntities) throws OptimisticLockingException;
	void save(Iterable<E> entities, Iterable<E> oldEntities, Query query) throws OptimisticLockingException;
	void delete(I id);
	void delete(E entity, E optLockedEntity) throws OptimisticLockingException;
	void deleteAll();

	E lockedUpdate(I id, Consumer<E> callback) throws OptimisticLockingException;

	void evict(I id);
	void evict(Query query);
	void evictEntities();
	void evictQueries();
	void evictAll();
}
