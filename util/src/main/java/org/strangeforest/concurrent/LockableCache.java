package org.strangeforest.concurrent;

import java.util.*;
import java.util.concurrent.*;

import org.strangeforest.cache.*;

/**
 * <p><tt>LockableCache</tt> is a <tt>Cache</tt> that supports fine grained locking of cache entries defined by <tt>LockableMap</tt></p>
 */
public class LockableCache<K, V> extends BaseLockableMap<K, V> implements Cache<K, V> {

	private Cache<K, V> cache;

	/**
	 * Creates <tt>LockableCache</tt> based on specified cache.
	 * @param cache cache that this concurrent cache will be based upon.
	 */
	public LockableCache(Cache<K, V> cache) {
		super(cache);
		this.cache = cache;
		cache.addCacheListener(new CacheListener<K, V>() {
			@Override public void entryRemoved(Entry<K, V> entry) {
				removeLock(entry.getKey());
			}
		});
	}

	@Override public Set<Entry<K, V>> entrySet() {
		return Collections.synchronizedSet(super.entrySet());
	}

	@Override public Set<K> keySet() {
		return Collections.synchronizedSet(super.keySet());
	}

	@Override public Collection<V> values() {
		return Collections.synchronizedCollection(super.values());
	}

	@Override public synchronized int getCapacity() {
		return cache.getCapacity();
	}

	@Override public synchronized void setCapacity(int capacity) {
		cache.setCapacity(capacity);
	}

	@Override public synchronized long getExpiryPeriod() {
		return cache.getExpiryPeriod();
	}

	@Override public synchronized void setExpiryPeriod(long expiryPeriod) {
		cache.setExpiryPeriod(expiryPeriod);
	}

	@Override public synchronized long getCheckExpiryPeriod() {
		return cache.getCheckExpiryPeriod();
	}

	@Override public synchronized void setCheckExpiryPeriod(long checkExpiryPeriod) {
		cache.setCheckExpiryPeriod(checkExpiryPeriod);
	}

	@Override public synchronized ScheduledExecutorService getCheckExpiryExecutor() {
		return cache.getCheckExpiryExecutor();
	}

	@Override public synchronized void setCheckExpiryExecutor(ScheduledExecutorService executor) {
		cache.setCheckExpiryExecutor(executor);
	}

	@Override public synchronized void startBackgroundExpiry() {
		cache.startBackgroundExpiry();
	}

	@Override public synchronized void stopBackgroundExpiry() {
		cache.stopBackgroundExpiry();
	}

	@Override public synchronized void removeExpiredEntries() {
		cache.removeExpiredEntries();
	}

	@Override public synchronized CacheStatistics getStatistics() {
		return cache.getStatistics();
	}

	@Override public synchronized void resetStatistics() {
		cache.resetStatistics();
	}

	@Override public synchronized void addCacheListener(CacheListener<K, V> listener) {
		cache.addCacheListener(listener);
	}

	@Override public synchronized void removeCacheListener(CacheListener<K, V> listener) {
		cache.removeCacheListener(listener);
	}
}
