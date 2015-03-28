package org.strangeforest.orm.remote;

import java.rmi.*;
import java.util.*;
import java.util.concurrent.*;

import org.strangeforest.orm.*;

public interface RepositoryRemote<I, E extends DomainEntity<I, E>> extends Remote {

	boolean exists(I id) throws RemoteException;
	E find(I id) throws RemoteException;
	E get(I id) throws RemoteException;
	boolean exists(Query query) throws RemoteException;
	E find(Query query) throws RemoteException;
	E get(Query query) throws RemoteException;
	List<E> getList(Query query) throws RemoteException;
	<D> List<D> getDetails(String detailName, I id, FieldAccessor<E, List<D>> accessor) throws RemoteException;

	E create(E entity) throws RemoteException;
	Iterable<E> create(Iterable<E> entities) throws RemoteException;
	Iterable<E> create(Iterable<E> entities, Query query) throws RemoteException;
	E save(E entity) throws RemoteException, OptimisticLockingException;
	E save(E entity, E optLockedEntity) throws RemoteException, OptimisticLockingException;
	Iterable<E> save(Iterable<E> entities) throws RemoteException, OptimisticLockingException;
	Iterable<E> save(Iterable<E> entities, Iterable<E> oldEntities) throws RemoteException, OptimisticLockingException;
	Iterable<E> save(Iterable<E> entities, Iterable<E> oldEntities, Query query) throws RemoteException, OptimisticLockingException;
	void delete(I id) throws RemoteException;
	void delete(E entity, E optLockedEntity) throws RemoteException, OptimisticLockingException;
	void deleteAll() throws RemoteException;

	void lock(I id) throws RemoteException;
	void lockInterruptibly(I id) throws RemoteException, InterruptedException;
	boolean tryLock(I id) throws RemoteException;
	boolean tryLock(I id, long timeout, TimeUnit unit) throws RemoteException, InterruptedException;
	void unlock(I id) throws RemoteException;
	boolean isLocked(I id) throws RemoteException;

	void evict(I id) throws RemoteException;
	void evict(Query query) throws RemoteException;
	void evictEntities() throws RemoteException;
	void evictQueries() throws RemoteException;
	void evictAll() throws RemoteException;
}
