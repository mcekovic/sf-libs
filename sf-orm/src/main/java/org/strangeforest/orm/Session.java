package org.strangeforest.orm;

import java.util.*;

import org.strangeforest.concurrent.*;
import org.strangeforest.transaction.*;

final class Session {

	// Factory

	private static final ThreadLocal<Session> SESSION = new ThreadLocal<>();

	public static Session getSession() {
		Session session = SESSION.get();
		if (session == null) {
			Transaction tx = TransactionManager.getTransaction();
			boolean inTx = tx != null;
			session = new Session(inTx);
			if (inTx) {
				final Session fSession = session;
				tx.registerSynchronization(new TransactionSynchronizationAdapter() {
					@Override public void afterCompletion(TransactionStatus status) {
						SESSION.remove();
						if (status == TransactionStatus.COMMITED)
							fSession.flush();
						else
							fSession.clean();
					}
				});
				SESSION.set(session);
			}
		}
		return session;
	}


	// Instance

	private final Map<String, EntityCache<?, ?>> caches;
	private final boolean inTx;

	private Session(boolean inTx) {
		caches = new HashMap<>();
		this.inTx = inTx;
	}

	public <I, E extends DomainEntity<I, E>> EntityCache<I, E> getCache(Class<E> entityClass, LockableCache<I, E> cache, boolean useCache) {
		String name = entityClass.getName();
		EntityCache<I, E> entityCache = (EntityCache<I, E>)caches.get(name);
		if (entityCache == null) {
			entityCache = new EntityCache<>(cache, useCache, inTx);
			caches.put(name, entityCache);
		}
		return entityCache;
	}

	private void flush() {
		for (EntityCache<?, ?> cache : caches.values())
			cache.flush();
	}

	private void clean() {
		for (EntityCache<?, ?> cache : caches.values())
			cache.clean();
	}
}
