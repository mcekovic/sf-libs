package org.strangeforest.concurrent;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;

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
	 * Tries locking the key, puts mapping and finally unlocks the key.
	 * @param key key
	 * @param value value
	 * @return <tt>true</tt> if key is successfully locked and value is put, otherwise if key is already locked <tt>false</tt> is returned.
	 */
	boolean tryLockedPut(K key, V value);

	/**
	 * Tries locking the key, puts mapping and finally unlocks the key.
	 * @param key key
	 * @param value value
	 * @param timeout number of milliseconds to wait if object is already locked.
	 * @param unit time unit.
	 * @return <tt>true</tt> if key is successfully locked and value is put, otherwise if key is already locked <tt>false</tt> is returned.
	 * @throws InterruptedException if current thread is interrupted.
	 */
	boolean tryLockedPut(K key, V value, long timeout, TimeUnit unit) throws InterruptedException;

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
