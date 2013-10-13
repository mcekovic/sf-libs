package org.strangeforest.concurrent;

import java.util.*;

import org.strangeforest.util.*;

/**
 * <p>This is a <tt>Map</tt> that supports fine-grained, key-level locking.</p>
 */
public interface LockableMap<K, V> extends Map<K, V>, LockManager<K> {

	/**
	 * Encapsulates standard pattern of cache usage:
	 * - Locks the specified key,
	 * - Gets the value,
	 * - If value is not in cache, fetches the value using the function,
	 * - Finally unlocks the key
	 * @param key key
	 * @param function mapping strategy used to fetch the value
	 * @return value for the key
	 */
	V lockedGet(K key, Function<K, V> function);

	/**
	 * Locks the key, puts mapping and finally unlocks the key.
	 * @param key key
	 * @param value value
	 * @return old value
	 */
	V lockedPut(K key, V value);

	/**
	 * Locks the key, removes mapping for the key and finally unlocks the key.
	 * @param key key
	 * @return removed value
	 */
	V lockedRemove(K key);

	/**
	 * Clears map for all unlocked keys. Locked keys will be marked dirty and will be removed from the map when they are unlocked.
	 */
	void tryClear();


	/**
	 * Returns snapshot <tt>Set</tt> of keys.
	 * @return snapshot <tt>Set</tt> of keys.
	 */
	Set<K> keySetSnapshot();

	/**
	 * Returns snapshot <tt>Collection</tt> of values.
	 * @return snapshot <tt>Collection</tt> of values.
	 */
	Collection<V> valuesSnapshot();

	/**
	 * Returns snapshot <tt>Set</tt> of <tt>Map.Entry</tt> entries.
	 * @return snapshot <tt>Set</tt> of <tt>Map.Entry</tt> entries.
	 */
	Set<Map.Entry<K, V>> entrySetSnapshot();

	/**
	 * Returns snapshot <tt>Set</tt> of locked keys.
	 * @return snapshot <tt>Set</tt> of locked keys.
	 */
	Set<K> lockedKeySetSnapshot();
}
