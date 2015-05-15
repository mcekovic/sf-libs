package org.strangeforest.orm;

import java.util.*;
import java.util.function.*;

import org.strangeforest.concurrent.*;

final class EntityCache<I, E extends DomainEntity<I, E>> {

	private final Map<I, E> l1;
	private final LockableCache<I, E> l2;
	private final boolean useCache;
	private final boolean inTx;
	private Map<I, E> changed;
	private Set<I> deleted;
	private Set<I> locked;

	public EntityCache(LockableCache<I, E> l2, boolean useCache, boolean inTx) {
		l1 = new HashMap<>();
		this.l2 = l2;
		this.useCache = useCache;
		this.inTx = inTx;
	}

	public E get(I id) {
		E entity = l1.get(id);
		if (useCache && entity == null) {
			entity = l2.get(id);
			if (entity != null) {
				entity = entity.lazyDeepClone();
				l1.put(id, entity);
			}
		}
		return entity;
	}

	public E lockedGet(I id, Function<I, E> function) {
		E entity = l1.get(id);
		if (useCache && entity == null) {
			entity = l2.lockedGet(id, function);
			if (entity != null) {
				entity = entity.lazyDeepClone();
				l1.put(id, entity);
			}
		}
		return entity;
	}

	public void put(E entity) {
		I id = entity.getId();
		l1.put(id, entity);
		if (useCache)
			l2.put(id, entity.lazyDeepClone());
	}

	public void tryLockedPut(E entity) {
		I id = entity.getId();
		l1.put(id, entity);
		if (useCache)
			l2.tryLockedPut(id, entity.lazyDeepClone());
	}

	public void changed(E entity) {
		I id = entity.getId();
		l1.put(id, entity);
		if (useCache) {
			if (inTx)
				changed().put(id, entity);
			else
				l2.put(id, entity.deepClone());
		}
	}

	public void lockedChanged(E entity) {
		I id = entity.getId();
		l1.put(id, entity);
		if (useCache) {
			if (inTx) {
				changed().put(id, entity);
				l2.lock(id);
				locked().add(id);
			}
			else
				l2.lockedPut(id, entity.deepClone());
		}
	}

	public void deleted(I id) {
		l1.remove(id);
		if (useCache) {
			if (inTx)
				deleted().add(id);
			else
				l2.remove(id);
		}
	}

	public void evict(I id) {
		l1.remove(id);
		if (useCache)
			l2.lockedRemove(id);
	}

	public void lock(I id) {
		if (useCache)
			l2.lock(id);
	}

	public void unlock(I id) {
		if (useCache) {
			if (inTx)
				locked().add(id);
			else
				l2.unlock(id);
		}
	}

	public void clear() {
		l1.clear();
		if (useCache)
			l2.tryClear();
	}

	public void flush() {
		if (!useCache)
			return;
		try {
			if (changed != null) {
				for (Map.Entry<I, E> entry : changed.entrySet()) {
					I id = entry.getKey();
					E entity = entry.getValue();
					if (locked.contains(id))
						l2.put(id, entity.deepClone());
					else
						l2.lockedPut(id, entity.deepClone());
				}
			}
			if (deleted != null) {
				for (I id : deleted) {
					if (locked.contains(id))
						l2.remove(id);
					else
						l2.lockedRemove(id);
				}
			}
		}
		finally {
			unlock();
		}
	}

	public void clean() {
		if (!useCache)
			return;
		unlock();
	}

	private void unlock() {
		if (locked != null) {
			for (I id : locked)
				l2.unlock(id);
		}
	}

	private Map<I, E> changed() {
		if (changed == null)
			changed = new HashMap<>();
		return changed;
	}

	private Set<I> deleted() {
		if (deleted == null)
			deleted = new HashSet<>();
		return deleted;
	}

	private Set<I> locked() {
		if (locked == null)
			locked = new HashSet<>();
		return locked;
	}
}
