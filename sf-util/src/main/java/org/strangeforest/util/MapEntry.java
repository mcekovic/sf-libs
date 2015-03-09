package org.strangeforest.util;

import java.util.*;
import java.io.*;

/**
 * <p>This is simple implementation of <tt>Map.Entry</tt> interface.</p>
 */
public class MapEntry<K, V> implements Map.Entry<K, V>, Serializable {

	protected K key;
	protected V value;

	/**
	 * Creates new <tt>MapEntry</tt> instance with specified key and value.
	 * @param key key.
	 * @param value value.
	 */
	public MapEntry(K key, V value) {
		super();
		this.key = key;
		this.value = value;
	}

	/**
	 * @return entry key.
	 */
	@Override public K getKey() {
		return key;
	}

	/**
	 * @return entry value.
	 */
	@Override public V getValue() {
		return value;
	}

	/**
	 * Sets new value for this entry. Underlying <tt>Map</tt> should reflect the changes to this entry.
	 * @param value new value.
	 * @return old value.
	 */
	@Override public V setValue(V value) {
		V old = this.value;
		this.value = value;
		return old;
	}

	@Override public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof Map.Entry))
			return false;
		Map.Entry<K, V> e = (Map.Entry<K, V>)o;
		K k = e.getKey();
		if (k == key || (k != null && k.equals(key))) {
			 V v = e.getValue();
			 return v == value || (v != null && v.equals(value));
		}
		return false;
	}

	@Override public int hashCode() {
		return (key == null ? 0 : key.hashCode()) ^ (value == null ? 0 : value.hashCode());
	}

	@Override public String toString() {
		return key + "=" + value;
	}
}
