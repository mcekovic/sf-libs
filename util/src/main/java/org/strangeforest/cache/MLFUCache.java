package org.strangeforest.cache;

import java.util.*;

/**
 * <p><tt>MLFUCache</tt> is <tt>Cache</tt> implementation that uses modified LFU
 * algorithm to choose what entry to remove when capacity limit is reached.</p>
 * <p>Modified LFU algorithm puts new entries at the top of the list and moves
 * entries one step up when they are accessed again (either by get or put).</p>
 */
public class MLFUCache<K, V> extends LRUCache<K, V>{

	/**
	 * Creates <tt>MLFUCache</tt> with unlimited capacity.
	 */
	public MLFUCache() {
		super();
	}

	/**
	 * Creates <tt>MLFUCache</tt> with specified capacity.
	 * @param capacity cache capacity. Capacity of 0 means unlimited capacity.
	 */
	public MLFUCache(int capacity) {
		super(capacity);
	}

	/**
	 * Creates <tt>MLFUCache</tt> with unlimited capacity based on specified map.
	 * Useful for building memory-sensitive cache by passing SoftHashMap instance.
	 * @param map that this cache will be based upon. Entries in the specified map are ordered by its entrySet.iterator().
	 */
	public MLFUCache(Map<? extends K, ? extends V> map) {
		super(map);
	}

	/**
	 * Creates <tt>MLFUCache</tt> with specified capacity based on specified map.
	 * Useful for building memory-sensitive cache by passing SoftHashMap instance.
	 * @param capacity cache capacity. Capacity of 0 means unlimited capacity.
	 * @param map that this cache will be based upon. Entries in the specified map are ordered by its entrySet.iterator().
	 */
	public MLFUCache(int capacity, Map<? extends K, ? extends V> map) {
		super(capacity, map);
	}


	@Override protected void touch(LinkedValue<K, V> lv) {
		if (lv.prev != header) {
			lv.remove();
			lv.insertBefore(lv.prev);
		}
	}
}
