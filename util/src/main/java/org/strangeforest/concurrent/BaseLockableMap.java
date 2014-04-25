package org.strangeforest.concurrent;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;
import java.util.function.*;

/**
 * <p>This is base <tt>LockableMap</tt> implementation.</p>
 * <p>It uses <tt>ReentrantLock</tt> <tt>Lock</tt> implementation for underlying locking mechanism.</p>
 * <p>This class <i>is</i> thread-safe.</p>
 */
public class BaseLockableMap<K, V> implements LockableMap<K, V> {

	private final Map<K, V> map;
	private final Map<K, EntryLock> locks;

	public BaseLockableMap(Map<K, V> map) {
		super();
		this.map = map;
		locks = new HashMap<>();
	}

	protected BaseLockableMap(Map<K, V> map, Map<K, EntryLock> locks) {
		super();
		this.map = map;
		this.locks = locks;
	}

	@Override public void lock(K key) {
		getLock(key).lock();
	}

	@Override public void lockInterruptibly(K key) throws InterruptedException {
		getLock(key).lockInterruptibly();
	}

	@Override public boolean tryLock(K key) {
		EntryLock lock = getLock(key);
		boolean isLocked = lock.tryLock();
		if (!isLocked)
			returnLock(lock);
		return isLocked;
	}

	@Override public boolean tryLock(K key, long timeout, TimeUnit unit) throws InterruptedException {
		EntryLock lock = getLock(key);
		boolean isLocked = lock.tryLock(timeout, unit);
		if (!isLocked)
			returnLock(lock);
		return isLocked;
	}

	private synchronized EntryLock getLock(K key) {
		EntryLock lock = locks.get(key);
		if (lock == null) {
			lock = new EntryLock();
			locks.put(key, lock);
		}
		lock.incRefCount();
		return lock;
	}

	private synchronized void returnLock(EntryLock lock) {
		lock.decRefCount();
	}

	protected final synchronized void removeLock(K key) {
		EntryLock lock = locks.get(key);
		if (lock != null && !lock.isLocked() && lock.getRefCount() <= 0)
			locks.remove(key);
	}

	protected final Map<K, EntryLock> getLockMap() {
		return locks;
	}

	@Override public synchronized void unlock(K key) {
		EntryLock lock = checkLocked(key);
		if (lock.isDirty()) {
			map.remove(key);
			if (lock.decRefCount() <= 0)
				locks.remove(key);
		}
		else if (lock.decRefCount() <= 0 && !map.containsKey(key))
			locks.remove(key);
		lock.unlock();
	}

	@Override public synchronized boolean isLocked(K key) {
		EntryLock lock = locks.get(key);
		return lock != null && lock.isLocked();
	}

	@Override public V lockedGet(K key, Function<K, V> function) {
		lock(key);
		try {
			V value = get(key);
			if (value == null && !containsKey(key)) {
				value = function.apply(key);
				doPut(key, value);
			}
			return value;
		}
		finally {
			unlock(key);
		}
	}

	@Override public V lockedPut(K key, V value) {
		lock(key);
		try {
			return doPut(key, value);
		}
		finally {
			unlock(key);
		}
	}

	private synchronized V doPut(K key, V value) {
		return map.put(key, value);
	}

	@Override public V lockedRemove(K key) {
		lock(key);
		try {
			synchronized (this) {
				return map.remove(key);
			}
		}
		finally {
			unlock(key);
		}
	}

	private V tryLockedRemove(K key) {
		EntryLock lock = getLock(key);
		if (lock.tryLock()) {
			try {
				synchronized (this) {
					return map.remove(key);
				}
			}
			finally {
				unlock(key);
			}
		}
		else {
			synchronized (this) {
				lock.setDirty();
				lock.decRefCount();
			}
			return null;
		}
	}

	@Override public synchronized Set<K> keySetSnapshot() {
		return new HashSet<>(map.keySet());
	}

	@Override public synchronized Collection<V> valuesSnapshot() {
		return new ArrayList<>(map.values());
	}

	@Override public synchronized Set<Entry<K, V>> entrySetSnapshot() {
		return new HashSet<>(map.entrySet());
	}

	@Override public synchronized Set<K> lockedKeySetSnapshot() {
		Set<K> lockedKeys = new HashSet<>();
		for (Map.Entry<K, EntryLock> entry : locks.entrySet())
			if (entry.getValue().isLocked())
				lockedKeys.add(entry.getKey());
		return lockedKeys;
	}

	private EntryLock checkLocked(Object key) {
		EntryLock lock = locks.get(key);
		if (lock == null || !lock.isLocked())
			throw new IllegalStateException("Key not locked: " + key);
		return lock;
	}


	// Map interface

	@Override public synchronized V get(Object key) {
		return map.get(key);
	}

	@Override public synchronized V put(K key, V value) {
		checkLocked(key);
		return map.put(key, value);
	}

	@Override public synchronized boolean containsKey(Object key) {
		return map.containsKey(key);
	}

	@Override public synchronized boolean containsValue(Object value) {
		return map.containsValue(value);
	}

	@Override public synchronized V remove(Object key) {
		checkLocked(key);
		return map.remove(key);
	}

	@Override public void clear() {
		for (K key : locksKeySnapshot())
			lockedRemove(key);
	}

	@Override public void tryClear() {
		for (K key : locksKeySnapshot())
			tryLockedRemove(key);
	}

	private synchronized K[] locksKeySnapshot() {
		return (K[])locks.keySet().toArray();
	}

	@Override public synchronized int size() {
		return map.size();
	}

	@Override public synchronized boolean isEmpty() {
		return map.isEmpty();
	}

	@Override public synchronized Set<Entry<K, V>> entrySet() {
		return map.entrySet();
	}

	@Override public synchronized Set<K> keySet() {
		return map.keySet();
	}

	@Override public synchronized Collection<V> values() {
		return map.values();
	}

	@Override public void putAll(Map<? extends K, ? extends V> m) {
		for (Entry<? extends K, ? extends V> e : m.entrySet())
			lockedPut(e.getKey(), e.getValue());
	}

	protected static final class EntryLock extends ReentrantLock {
		
		private int refCount;
		private boolean dirty;

		public int getRefCount() {
			return refCount;
		}

		public void incRefCount() {
			++refCount;
		}

		public int decRefCount() {
			return --refCount;
		}

		public boolean isDirty() {
			return dirty;
		}

		public void setDirty() {
			dirty = true;
		}
	}


	// Object

	@Override public synchronized boolean equals(Object obj) {
		return map.equals(obj);
	}

	@Override public synchronized int hashCode() {
		return map.hashCode();
	}

	@Override public synchronized String toString() {
		return map.toString();
	}
}
