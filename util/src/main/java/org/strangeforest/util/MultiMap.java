package org.strangeforest.util;

import java.util.*;

/**
 * <p>This is a <tt>Map</tt> that can have multiple values associated with a single key.</p>
 */
public interface MultiMap<K, V> extends Map<K, V> {

	/**
	 * Returns one of the values associated with specified key. Which value is returned is dependent on the implementation.
	 * @param key key
	 * @return one of the values associated with specified key.
	 */
	@Override V get(Object key);

	/**
	 * Returns <tt>Set</tt> of values associated with specified key.
	 * Returned collection should be backed up by the <tt>MultiMap</tt>.
	 * @param key key.
	 * @return <tt>Set</tt> of values associated with the <tt>key</tt>.
	 */
	Set<V> getAll(K key);

	/**
	 * Removes all associations with specified key.
	 * @param key key
	 * @return Old <tt>Set</tt> of values associated with the <tt>key</tt> or <tt>null</tt> if specified key does not exist.
	 */
	Set<V> removeAll(Object key);

	/**
	 * Removes association of specified value with specified key.
	 * @param key key
	 * @param value value
	 * @return remaining <tt>Set</tt> of values associated with the <tt>key</tt> or <tt>null</tt> if specified key does not exist.
	 */
	Set<V> remove(Object key, Object value);

	Set<Map.Entry<K, Set<V>>> allEntrySet();
}
