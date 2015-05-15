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

	public <I, E extends DomainEntity<I, E>> EntityCache<I, E> getCache(Class<E> entityClass, LockableCache<I, E> l2, boolean useCache) {
		String name = entityClass.getName();
		EntityCache<I, E> cache = (EntityCache<I, E>)caches.get(name);
		if (cache == null) {
			cache = new EntityCache<>(l2, useCache, inTx);
			caches.put(name, cache);
		}
		return cache;
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
