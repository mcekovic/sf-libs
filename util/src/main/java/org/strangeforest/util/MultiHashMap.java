package org.strangeforest.util;

import java.util.*;
import java.io.*;

/**
 * <p><tt>MultiMap</tt> implementation based on <tt>HashMap</tt>.</p>
 */
public class MultiHashMap<K, V> extends AbstractMap<K, V> implements MultiMap<K, V>, Serializable, Cloneable {

	private Map<K, Set<V>> map;

	public MultiHashMap() {
		super();
		map = new HashMap<>();
	}

	public MultiHashMap(int initialCapacity) {
		super();
		map = new HashMap<>(initialCapacity);
	}

	public MultiHashMap(int initialCapacity, float loadFactor) {
		super();
		map = new HashMap<>(initialCapacity, loadFactor);
	}

	@Override public V put(K key, V value) {
		Set<V> values = map.get(key);
		if (values == null) {
			values = new HashSet<>();
			map.put(key, values);
		}
		V old = values.isEmpty() ? null : values.iterator().next();
		values.add(value);
		return old;
	}

	@Override public Set<V> getAll(K key) {
		Set<V> values = map.get(key);
		if (values == null) {
			values = new HashSet<>();
			map.put(key, values);
		}
		return values;
	}

	@Override public V get(Object key) {
		Set<V> values = map.get(key);
		if (values == null || values.isEmpty())
			return null;
		else
			return values.iterator().next();
	}

	@Override public boolean containsKey(Object key) {
		return map.containsKey(key);
	}

	@Override public boolean containsValue(Object value) {
		for (Set<V> values : map.values())
			if (values.contains(value))
				return true;
		return false;
	}

	@Override public Set<V> remove(Object key, Object value) {
		Set<V> values = map.get(key);
		if (values != null)
			values.remove(value);
		return values;
	}

	@Override public Set<V> removeAll(Object key) {
		return map.remove(key);
	}

	@Override public int size() {
		return map.size();
	}

	@Override public boolean isEmpty() {
		return map.isEmpty();
	}

	@Override public void clear() {
		map.clear();
	}

	@Override public Set<K> keySet() {
		return map.keySet();
	}

	@Override public Set<Map.Entry<K, Set<V>>> allEntrySet() {
		return map.entrySet();
	}

	@Override public boolean equals(Object obj) {
		if (obj == this) return true;
		if (!(obj instanceof MultiMap)) return false;
		MultiMap t = (MultiMap)obj;
		if (t.size() != size())
			return false;
		for (Map.Entry<K, Set<V>> entry : allEntrySet())
			if (!ObjectUtil.equal(entry.getValue(), t.getAll(entry.getKey())))
				return false;
		return true;
	}

	@Override public int hashCode() {
		return super.hashCode();
	}

	@Override public MultiMap<K, V> clone() {
		MultiMap<K, V> newMap = new MultiHashMap<>();
		newMap.putAll(this);
		return newMap;
	}

	private transient Collection<V> values;

	@Override public Collection<V> values() {
		if (values == null)
			values = new AbstractCollection<V>() {
				@Override public int size() {
					int size = 0;
					for (Set<V> values : map.values())
						size += values.size();
					return size;
				}
				@Override public Iterator<V> iterator() {
					return new ValuesIterator();
				}
			};
		return values;
	}

	private transient Set<Map.Entry<K, V>> entries;

	@Override public Set<Map.Entry<K, V>> entrySet() {
		if (entries == null)
			entries = new AbstractSet<Map.Entry<K, V>>() {
				@Override public int size() {
					return map.size();
				}
				@Override public Iterator<Map.Entry<K, V>> iterator() {
					return new EntriesIterator();
				}
			};
		return entries;
	}

	private abstract class MultiMapIterator<E> implements Iterator<E> {

		private Iterator<Map.Entry<K, Set<V>>> mainIter;
		private Iterator<V> valuesIter;
		private boolean hasNext;
		private K currKey;

		public MultiMapIterator() {
			super();
			mainIter = map.entrySet().iterator();
			valuesIter = nextValuesIterator();
		}

		@Override public boolean hasNext() {
			while (valuesIter != null) {
				if (valuesIter.hasNext()) {
					hasNext = true;
					return true;
				}
				valuesIter = nextValuesIterator();
			}
			hasNext = false;
			return false;
		}

		public Entry nextEntry() {
			if (hasNext || hasNext()) {
				hasNext = false;
				return new Entry(currKey, valuesIter.next());
			}
			else
				throw new NoSuchElementException();
		}

		@Override public void remove() {
			if (valuesIter == null)
				throw new IllegalStateException();
			valuesIter.remove();
			hasNext = false;
		}

		private Iterator<V> nextValuesIterator() {
			if (mainIter.hasNext()) {
				Map.Entry<K, Set<V>> entry = mainIter.next();
				currKey = entry.getKey();
				return entry.getValue().iterator();
			}
			else
				return null;
		}
	}

	private class EntriesIterator extends MultiMapIterator<Map.Entry<K, V>> {
		@Override public Map.Entry<K, V> next() {
			return nextEntry();
		}
	}

	private class ValuesIterator extends MultiMapIterator<V> {
		@Override public V next() {
			return nextEntry().value;
		}
	}

	private class Entry extends MapEntry<K, V> {

		public Entry(K key, V value) {
			super(key, value);
		}

		@Override public V setValue(V value) {
			super.setValue(value);
			return put(key, value);
		}
	}
}
