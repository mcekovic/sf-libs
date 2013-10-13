package org.strangeforest.concurrent;

import java.util.*;

import org.strangeforest.cache.*;

/**
 * <p><tt>LockableMLFUCache</tt> is <tt>LockableCache</tt> that uses modified
 * LFU algorithm to choose what entry to remove when capacity limit is reached.
 * See <tt>MLFUCache</tt> for the details on modified LFU algorithm.</p>
 * @see org.strangeforest.cache.MLFUCache
 */
public class LockableMLFUCache<K, V> extends LockableCache<K, V> {

	/**
	 * Creates <tt>LockableMLFUCache</tt> with unlimited capacity.
	 */
	public LockableMLFUCache() {
		super(new MLFUCache<K, V>());
	}

	/**
	 * Creates <tt>LockableMLFUCache</tt> with specified capacity.
	 * @param capacity cache capacity. Capacity of 0 means unlimited capacity.
	 */
	public LockableMLFUCache(int capacity) {
		super(new MLFUCache<K, V>(capacity));
	}

	/**
	 * Creates <tt>LockableMLFUCache</tt> with unlimited capacity based on specified map.
	 * Useful for building memory-sensitive cache by passing <tt>SoftHashMap</tt> instance.
	 * @param map map that this cache will be based upon. Entries in the specified map are ordered by its <tt>entrySet.iterator()</tt>.
	 */
	public LockableMLFUCache(Map<? extends K, ? extends V> map) {
		super(new MLFUCache<>(map));
	}

	/**
	 * Creates <tt>LockableMLFUCache</tt> with specified capacity based on specified map.
	 * Useful for building memory-sensitive cache by passing <tt>SoftHashMap</tt> instance.
	 * @param capacity cache capacity. Capacity of 0 means unlimited capacity.
	 * @param map map that this cache will be based upon. Entries in the specified map are ordered by its <tt>entrySet.iterator()</tt>.
	 */
	public LockableMLFUCache(int capacity, Map<? extends K, ? extends V> map) {
		super(new MLFUCache<>(capacity, map));
	}
}
