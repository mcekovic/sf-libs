package org.strangeforest.orm.remote;

import java.rmi.*;
import java.rmi.registry.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;

import org.strangeforest.orm.*;

public class RemoteRepository<I, E extends DomainEntity<I, E>> implements Repository<I, E> {

	private final DomainContext context;
	private final Class<E> entityClass;
	private final Registry registry;
	private RepositoryRemote<I, E> remoteRepository;
	private AttributeCopier<E> attributeCopier;

	public interface RemoteCallable<V, Ex extends Exception> extends Callable<V> {
		@Override V call() throws RemoteException, Ex;
	}

	public RemoteRepository(DomainContext context, Registry registry, Class<E> entityClass) {
		super();
		this.context = context;
		this.entityClass = entityClass;
		this.registry = registry;
		context.registerRepository(entityClass, this);
	}
	
	public RemoteRepository(DomainContext context, Registry registry, Class<E> entityClass, AttributeCopier<E> attributeCopier) {
		this(context, registry, entityClass);
		this.attributeCopier = attributeCopier;
	}

	public synchronized void ensureRemoteRepository() {
		if (remoteRepository == null)
			lookup();
	}

	private synchronized void lookup() {
		try {
			remoteRepository = (RepositoryRemote)registry.lookup(getEntityName() + "Repository");
		}
		catch (Exception ex) {
			throw new RemoteRepositoryException(ex);
		}
	}

	private <V, Ex extends Exception> V retriedCall(RemoteCallable<V, Ex> callable) throws RemoteRepositoryException, Ex {
		ensureRemoteRepository();
		try {
			return callable.call();
		}
		catch (RemoteException ex) {
			lookup();
			try {
				return callable.call();
			}
			catch (RemoteException ex2) {
				throw new RemoteRepositoryException(ex2);
			}
		}
	}

	@Override public String getEntityName() {
		return entityClass.getName();
	}

	@Override public boolean exists(final I id) {
		return retriedCall(new RemoteCallable<Boolean, RemoteRepositoryException>() {
			@Override public Boolean call() throws RemoteException {
				return remoteRepository.exists(id);
			}
		});
	}

	@Override public E find(final I id) {
		return retriedCall(new RemoteCallable<E, RemoteRepositoryException>() {
			@Override public E call() throws RemoteException {
				E entity = remoteRepository.find(id);
				context.attach(entity);
				return entity;
			}
		});
	}

	@Override public E get(final I id) {
		return retriedCall(new RemoteCallable<E, RemoteRepositoryException>() {
			@Override public E call() throws RemoteException {
				E entity = remoteRepository.get(id);
				context.attach(entity);
				return entity;
			}
		});
	}

	@Override public boolean exists(final Query query) {
		return retriedCall(new RemoteCallable<Boolean, RemoteRepositoryException>() {
			@Override public Boolean call() throws RemoteException {
				return remoteRepository.exists(query);
			}
		});
	}

	@Override public E find(final Query query) {
		return retriedCall(new RemoteCallable<E, RemoteRepositoryException>() {
			@Override public E call() throws RemoteException {
				E entity = remoteRepository.find(query);
				context.attach(entity);
				return entity;
			}
		});
	}

	@Override public E get(final Query query) {
		return retriedCall(new RemoteCallable<E, RemoteRepositoryException>() {
			@Override public E call() throws RemoteException {
				E entity = remoteRepository.get(query);
				context.attach(entity);
				return entity;
			}
		});
	}

	@Override public List<E> getList(final Query query) {
		return retriedCall(new RemoteCallable<List<E>, RemoteRepositoryException>() {
			@Override public List<E> call() throws RemoteException {
				List<E> entities = remoteRepository.getList(query);
				context.attach(entities);
				return entities;
			}
		});
	}

	@Override public void iterate(Query query, Consumer<E> callback) {
		throw new UnsupportedOperationException();
	}

	@Override public <D> List<D> getDetails(final String detailName, final I id, final FieldAccessor<E, List<D>> accessor) {
		return retriedCall(new RemoteCallable<List<D>, RemoteRepositoryException>() {
			@Override public List<D> call() throws RemoteException {
				List<D> details = remoteRepository.getDetails(detailName, id, accessor);
				context.attach(details);
				return details;
			}
		});
	}

	@Override public void create(final E entity) {
		retriedCall(new RemoteCallable<Void, RemoteRepositoryException>() {
			@Override public Void call() throws RemoteException {
				E newEntity = remoteRepository.create(entity);
				attachAndCopyAttributes(entity, newEntity);
				return null;
			}
		});
	}

	@Override public void create(final Iterable<E> entities) {
		retriedCall(new RemoteCallable<Void, RemoteRepositoryException>() {
			@Override public Void call() throws RemoteException {
				Iterable<E> newEntities = remoteRepository.create(entities);
				attachAndCopyAttributes(entities, newEntities);
				return null;
			}
		});
	}

	@Override public void create(final Iterable<E> entities, final Query query) {
		retriedCall(new RemoteCallable<Void, RemoteRepositoryException>() {
			@Override public Void call() throws RemoteException {
				Iterable<E> newEntities = remoteRepository.create(entities, query);
				attachAndCopyAttributes(entities, newEntities);
				return null;
			}
		});
	}

	@Override public void save(final E entity) throws OptimisticLockingException {
		retriedCall(new RemoteCallable<Void, OptimisticLockingException>() {
			@Override public Void call() throws RemoteException {
				E newEntity = remoteRepository.save(entity);
				attachAndCopyAttributes(entity, newEntity);
				return null;
			}
		});
	}

	@Override public void save(final E entity, final E optLockedEntity) throws OptimisticLockingException {
		retriedCall(new RemoteCallable<Void, OptimisticLockingException>() {
			@Override public Void call() throws RemoteException {
				E newEntity = remoteRepository.save(entity, optLockedEntity);
				attachAndCopyAttributes(entity, newEntity);
				return null;
			}
		});
	}

	@Override public void save(final Iterable<E> entities) throws OptimisticLockingException {
		retriedCall(new RemoteCallable<Void, OptimisticLockingException>() {
			@Override public Void call() throws RemoteException {
				Iterable<E> newEntities = remoteRepository.save(entities);
				attachAndCopyAttributes(entities, newEntities);
				return null;
			}
		});
	}

	@Override public void save(final Iterable<E> entities, final Iterable<E> oldEntities) throws OptimisticLockingException {
		retriedCall(new RemoteCallable<Void, OptimisticLockingException>() {
			@Override public Void call() throws RemoteException {
				Iterable<E> newEntities = remoteRepository.save(entities, oldEntities);
				attachAndCopyAttributes(entities, newEntities);
				return null;
			}
		});
	}

	@Override public void save(final Iterable<E> entities, final Iterable<E> oldEntities, final Query query) throws OptimisticLockingException {
		retriedCall(new RemoteCallable<Void, OptimisticLockingException>() {
			@Override public Void call() throws RemoteException {
				Iterable<E> newEntities = remoteRepository.save(entities, oldEntities, query);
				attachAndCopyAttributes(entities, newEntities);
				return null;
			}
		});
	}

	private void attachAndCopyAttributes(E entity, E newEntity) {
		context.attach(entity);
		if (attributeCopier != null)
			attributeCopier.copyAttributes(newEntity, entity);
	}

	private void attachAndCopyAttributes(Iterable<E> entities, Iterable<E> newEntities) {
		for (Iterator<E> iter = entities.iterator(), newIter = newEntities.iterator(); iter.hasNext() && newIter.hasNext(); ) {
			E entity = iter.next();
			attachAndCopyAttributes(entity, newIter.next());
		}
	}

	@Override public void delete(final I id) {
		retriedCall(new RemoteCallable<Void, RemoteRepositoryException>() {
			@Override public Void call() throws RemoteException {
				remoteRepository.delete(id);
				return null;
			}
		});
	}

	@Override public void delete(final E entity, final E optLockedEntity) throws OptimisticLockingException {
		retriedCall(new RemoteCallable<Void, OptimisticLockingException>() {
			@Override public Void call() throws RemoteException {
				remoteRepository.delete(entity, optLockedEntity);
				return null;
			}
		});
	}

	@Override public void deleteAll() {
		retriedCall(new RemoteCallable<Void, RemoteRepositoryException>() {
			@Override public Void call() throws RemoteException {
				remoteRepository.deleteAll();
				return null;
			}
		});
	}

	@Override public E lockedUpdate(I id, Consumer<E> callback) throws OptimisticLockingException {
		throw new UnsupportedOperationException();
	}

	@Override public void lock(final I id) {
		retriedCall(new RemoteCallable<Void, RemoteRepositoryException>() {
			@Override public Void call() throws RemoteException {
				remoteRepository.lock(id);
				return null;
			}
		});
	}

	@Override public void lockInterruptibly(final I id) throws InterruptedException {
		retriedCall(new RemoteCallable<Void, InterruptedException>() {
			@Override public Void call() throws RemoteException, InterruptedException {
				remoteRepository.lockInterruptibly(id);
				return null;
			}
		});
	}

	@Override public boolean tryLock(final I id) {
		return retriedCall(new RemoteCallable<Boolean, RemoteRepositoryException>() {
			@Override public Boolean call() throws RemoteException {
				return remoteRepository.tryLock(id);
			}
		});
	}

	@Override public boolean tryLock(final I id, final long timeout, final TimeUnit unit) throws InterruptedException {
		return retriedCall(new RemoteCallable<Boolean, InterruptedException>() {
			@Override public Boolean call() throws RemoteException, InterruptedException {
				return remoteRepository.tryLock(id, timeout, unit);
			}
		});
	}

	@Override public void unlock(final I id) {
		retriedCall(new RemoteCallable<Void, RemoteRepositoryException>() {
			@Override public Void call() throws RemoteException {
				remoteRepository.unlock(id);
				return null;
			}
		});
	}

	@Override public boolean isLocked(final I id) {
		return retriedCall(new RemoteCallable<Boolean, RemoteRepositoryException>() {
			@Override public Boolean call() throws RemoteException {
				return remoteRepository.isLocked(id);
			}
		});
	}

	@Override public void evict(final I id) {
		retriedCall(new RemoteCallable<Void, RemoteRepositoryException>() {
			@Override public Void call() throws RemoteException {
				remoteRepository.evict(id);
				return null;
			}
		});
	}

	@Override public void evict(final Query query) {
		retriedCall(new RemoteCallable<Void, RemoteRepositoryException>() {
			@Override public Void call() throws RemoteException {
				remoteRepository.evict(query);
				return null;
			}
		});
	}

	@Override public void evictEntities() {
		retriedCall(new RemoteCallable<Void, RemoteRepositoryException>() {
			@Override public Void call() throws RemoteException {
				remoteRepository.evictEntities();
				return null;
			}
		});
	}

	@Override public void evictQueries() {
		retriedCall(new RemoteCallable<Void, RemoteRepositoryException>() {
			@Override public Void call() throws RemoteException {
				remoteRepository.evictQueries();
				return null;
			}
		});
	}

	@Override public void evictAll() {
		retriedCall(new RemoteCallable<Void, RemoteRepositoryException>() {
			@Override public Void call() throws RemoteException {
				remoteRepository.evictAll();
				return null;
			}
		});
	}
}
