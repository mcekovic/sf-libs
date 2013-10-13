package org.strangeforest.concurrent;

import java.util.*;

import org.strangeforest.cache.*;

/**
 * <p><tt>LockableLRUCache</tt> is <tt>LockableCache</tt> that uses
 * LRU algorithm to choose what entry to remove when capacity limit is reached.</p>
 */
public class LockableLRUCache<K, V> extends LockableCache<K, V> {

	/**
	 * Creates <tt>LockableLRUCache</tt> with unlimited capacity.
	 */
	public LockableLRUCache() {
		super(new LRUCache<K, V>());
	}

	/**
	 * Creates <tt>LockableLRUCache</tt> with specified capacity.
	 * @param capacity cache capacity. Capacity of 0 means unlimited capacity.
	 */
	public LockableLRUCache(int capacity) {
		super(new LRUCache<K, V>(capacity));
	}

	/**
	 * Creates <tt>LockableLRUCache</tt> with unlimited capacity based on specified map.
	 * Useful for building memory-sensitive cache by passing <tt>SoftHashMap</tt> instance.
	 * @param map map that this cache will be based upon. Entries in the specified map are ordered by its <tt>entrySet.iterator()</tt>.
	 */
	public LockableLRUCache(Map<? extends K, ? extends V> map) {
		super(new LRUCache<>(map));
	}

	/**
	 * Creates <tt>LockableLRUCache</tt> with specified capacity based on specified map.
	 * Useful for building memory-sensitive cache by passing <tt>SoftHashMap</tt> instance.
	 * @param capacity cache capacity. Capacity of 0 means unlimited capacity.
	 * @param map map that this cache will be based upon. Entries in the specified map are ordered by its <tt>entrySet.iterator()</tt>.
	 */
	public LockableLRUCache(int capacity, Map<? extends K, ? extends V> map) {
		super(new LRUCache<>(capacity, map));
	}
}
