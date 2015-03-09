package org.strangeforest.cache;

import java.util.*;
import java.util.concurrent.*;

/**
 * <p><tt>Cache</tt> is a <tt>Map</tt> that has support for limiting the capacity and gathering statistics.</p>
 */
public interface Cache<K, V> extends Map<K, V> {

	/**
	 * Returns cache capacity (maximum number of entries in cache). The capacity of 0 means unlimited capacity.
	 * @return cache capacity.
	 */
	int getCapacity();

	/**
	 * Sets new cache capacity (maximum number of entries in cache). The capacity of 0 means unlimited capacity.
	 * @param capacity new cache capacity.
	 */
	void setCapacity(int capacity);

	/**
	 * Returns cached value expiry period. The expiry period of 0 means no expiry.
	 * @return expiry period.
	 */
	long getExpiryPeriod();

	/**
	 * Sets new cached value expiry period. The expiry period of 0 means no expiry.
	 * @param expiryPeriod new expiry period.
	 */
	void setExpiryPeriod(long expiryPeriod);

	/**
	 * Returns check expiry rate. The rate of 0 means no background expiration will be performed.
	 * @return check expiry rate.
	 */
	long getCheckExpiryPeriod();

	/**
	 * Sets new check expiry rate. The rate of 0 means no background expiration will be performed.
	 * @param checkExpiryRate new check expiry rate.
	 */
	void setCheckExpiryPeriod(long checkExpiryRate);

	ScheduledExecutorService getCheckExpiryExecutor();

	void setCheckExpiryExecutor(ScheduledExecutorService executor);

	void startBackgroundExpiry();

	void stopBackgroundExpiry();

	void removeExpiredEntries();

	/**
	 * Returns current cache statistics.
	 * @return cache statistics.
	 */
	CacheStatistics getStatistics();

	/**
	 * Resets cache statistics.
	 */
	void resetStatistics();

	/**
	 * Adds new <tt>CacheListener</tt>.
	 * @param listener new <tt>CacheListener</tt>.
	 */
	void addCacheListener(CacheListener<K, V> listener);

	/**
	 * Removes <tt>CacheListener</tt>.
	 * @param listener <tt>CacheListener</tt> to be removed.
	 */
	void removeCacheListener(CacheListener<K, V> listener);
}
