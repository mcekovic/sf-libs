package org.strangeforest.util;

import java.util.*;

/**
 * <p><tt>OrderedMap</tt> is a <tt>Map</tt> that has its entries ordered.</p>
 */
public interface OrderedMap<K, V> extends Map<K, V> {

	/**
	 * Returns entry at specified index.
	 * @param index index
	 * @return entry at specified index.
	 * @throws IndexOutOfBoundsException if <tt>index < 0 || index >= size()</tt>.
	 */
	Map.Entry<K, V> getEntry(int index);

	/**
	 * Returns first entry.
	 * @return first entry.
	 * @throws IndexOutOfBoundsException if map is empty.
	 */
	Map.Entry<K, V> getFirstEntry();

	/**
	 * Returns last entry.
	 * @return last entry.
	 * @throws IndexOutOfBoundsException if map is empty.
	 */
	Map.Entry<K, V> getLastEntry();


	/**
	 * Returns key at specified index.
	 * @param index index
	 * @return key at specified index.
	 * @throws IndexOutOfBoundsException if <tt>index < 0 || index >= size()</tt>.
	 */
	K getKey(int index);

	/**
	 * Returns first key.
	 * @return first key.
	 * @throws IndexOutOfBoundsException if map is empty.
	 */
	K getFirstKey();

	/**
	 * Returns last key.
	 * @return last key.
	 * @throws IndexOutOfBoundsException if map is empty.
	 */
	K getLastKey();


	/**
	 * Returns value at specified index.
	 * @param index index
	 * @return value at specified index.
	 * @throws IndexOutOfBoundsException if <tt>index < 0 || index >= size()</tt>.
	 */
	V getValue(int index);

	/**
	 * Returns first value.
	 * @return first value.
	 * @throws IndexOutOfBoundsException if map is empty.
	 */
	V getFirstValue();

	/**
	 * Returns last value.
	 * @return last value.
	 * @throws IndexOutOfBoundsException if map is empty.
	 */
	V getLastValue();


	/**
	 * Puts new mapping into the map. If specified key is not already in map
	 * new entry is created and put as the last entry, otherwise index of
	 * the specified key is not changed.
	 * @param key key
	 * @param value value
	 * @return previous value of the specified key or <tt>null</tt> if key was not already in the map.
	 */
	@Override V put(K key, V value);

	/**
	 * Puts new mapping into the map at the specified index. If specified key already exists in the map
	 * it is moved to specified position, otherwise new entry is inserted at specified position.
	 * @param index index
	 * @param key key
	 * @param value value
	 * @return previous entry at the specified index or <tt>null</tt> if <tt>index == size()</tt>.
	 * @throws IndexOutOfBoundsException if <tt>index < 0 || index > size()</tt>.
	 */
	Map.Entry<K, V> put(int index, K key, V value);

	/**
	 * Puts new mappings into the map at the specified index.
	 * @param index index
	 * @param map Map<K, V> from which to put mappings
	 * @throws IndexOutOfBoundsException if <tt>index < 0 || index > size()</tt>.
	 */
	void putAll(int index, Map<? extends K, ? extends V> map);

	/**
	 * Puts new mapping at the first position.
	 * @param key key
	 * @param value value
	 * @return previous first entry or <tt>null</tt> if map is empty.
	 */
	Map.Entry<K, V> putFirst(K key, V value);

	/**
	 * Puts new mapping at the last position.
	 * @param key key
	 * @param value value
	 * @return previous last entry or <tt>null</tt> if map is empty.
	 */
	Map.Entry<K, V> putLast(K key, V value);


	/**
	 * Removes mapping at the specified index.
	 * @param index index
	 * @return entry that has been removed.
	 * @throws IndexOutOfBoundsException if <tt>index < 0 || index >= size()</tt>.
	 */
	Map.Entry<K, V> remove(int index);

	/**
	 * Removes first entry from the map.
	 * @return entry that has been removed.
	 * @throws IndexOutOfBoundsException if map is empty.
	 */
	Map.Entry<K, V> removeFirst();
	/**
	 * Removes last entry from the map.
	 * @return entry that has been removed.
	 * @throws IndexOutOfBoundsException if map is empty.
	 */
	Map.Entry<K, V> removeLast();

	/**
	 * Moves up entry with the specified key.
	 * @param key key.
	 */
	void moveUp(K key);

	/**
	 * Moves down entry with the specified key.
	 * @param key key.
	 */
	void moveDown(K key);

	/**
	 * Moves entry with the specified key to the specified position.
	 * @param key key.
	 * @param index new position.
	 * @throws IndexOutOfBoundsException if <tt>index < 0 || index > size()</tt>.
	 */
	void moveTo(K key, int index);

	/**
	 * Swaps two entries if keys exist in the map and are different.
	 * @param key1 key
	 * @param key2 key
	 */
	void swap(K key1, K key2);

	/**
	 * Returns index of a specified key.
	 * @param key key
	 * @return index of a specified key or -1 if key does not exist in the map.
	 */
	int indexOfKey(Object key);

	/**
	 * Returns first index of a specified value.
	 * @param value value
	 * @return first index of a specified value or -1 if value does not exist in the map.
	 */
	int indexOfValue(Object value);

	/**
	 * Returns last index of a specified value.
	 * @param value value
	 * @return last index of a specified value or -1 if value does not exist in the map.
	 */
	int lastIndexOfValue(Object value);


	/**
	 * Returns entries <tt>Iterator</tt>.
	 * @return entries <tt>Iterator</tt>.
	 */
	Iterator<Map.Entry<K, V>> iterator();

	/**
	 * Returns unmodifiable <tt>List</tt> of entries that is backed by this <tt>OrderedMap</tt>.
	 * @return <tt>List</tt> of entries.
	 */
	List<Map.Entry<K, V>> entryList();

	/**
	 * Returns unmodifiable <tt>List</tt> of keys that is backed by this <tt>OrderedMap</tt>.
	 * @return <tt>List</tt> of keys.
	 */
	List<K> keyList();

	/**
	 * Returns fixed-size <tt>List</tt> of values that is backed by this <tt>OrderedMap</tt>.
	 * @return <tt>List</tt> of values.
	 */
	List<V> valueList();

	/**
	 * Returns array of map entries.
	 * @return array of map entries.
	 */
	Map.Entry<K, V>[] toArray();

	/**
	 * Sorts the <tt>OrderedMap</tt> by keys using natural ordering.
	 */
	void sortByKeys();

	/**
	 * Sorts the <tt>OrderedMap</tt> by keys using specified comparator.
	 * @param comparator comparator.
	 */
	void sortByKeys(Comparator<? super K> comparator);

	/**
	 * Sorts the <tt>OrderedMap</tt> by values using natural ordering.
	 */
	void sortByValues();

	/**
	 * Sorts the <tt>OrderedMap</tt> by values using specified comparator.
	 * @param comparator comparator.
	 */
	void sortByValues(Comparator<? super V> comparator);
}
