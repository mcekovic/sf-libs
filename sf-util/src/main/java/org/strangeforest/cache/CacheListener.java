package org.strangeforest.cache;

import java.util.*;

/**
 * <p>Interface for notifying changes in cache.</p>
 */
public interface CacheListener<K, V> {

	/**
	 * This event is fired when entry is removed from cache because of ensuring maximum capacity.
	 * @param entry entry that is removed from cache.
	 */
	void entryRemoved(Map.Entry<K, V> entry);
}
