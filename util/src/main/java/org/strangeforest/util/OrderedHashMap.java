package org.strangeforest.util;

import java.util.*;
import java.io.*;

/**
 * <p><tt>OrderedHashMap</tt> is <tt>OrderedMap</tt> implementation that is based on <tt>HashMap</tt>.</p>
 */
public class OrderedHashMap<K, V> extends AbstractMap<K, V> implements OrderedMap<K, V>, Serializable, Cloneable {

	private LinkedEntry<K, V> header;
	private Map<K, LinkedEntry<K, V>> map;

	public OrderedHashMap() {
		super();
		map = new HashMap<>();
		createHeader();
	}

	public OrderedHashMap(int initialCapacity) {
		super();
		map = new HashMap<>(initialCapacity);
		createHeader();
	}

	public OrderedHashMap(int initialCapacity, float loadFactor) {
		super();
		map = new HashMap<>(initialCapacity, loadFactor);
		createHeader();
	}

	public OrderedHashMap(Map<? extends K, ? extends V> m) {
		this();
		putAll(m);
	}

	private void createHeader() {
		header = new LinkedEntry<>();
		header.prev = header.next = header;
	}


	// OrderedMap interface

	@Override public Entry<K, V> getEntry(int index) {
		if (index < 0 || index >= size())
			throw new IndexOutOfBoundsException();
		LinkedEntry<K, V> e = header.next;
		for (int i = 0; i < index; i++)
			e = e.next;
		return e;
	}

	@Override public K getKey(int index) {
		return getEntry(index).getKey();
	}

	@Override public V getValue(int index) {
		return getEntry(index).getValue();
	}

	@Override public Entry<K, V> getFirstEntry() {
		LinkedEntry<K, V> e = header.next;
		if (e != header)
			return e;
		else
			throw new NoSuchElementException();
	}

	@Override public K getFirstKey() {
		return getFirstEntry().getKey();
	}

	@Override public V getFirstValue() {
		return getFirstEntry().getValue();
	}

	@Override public Entry<K, V> getLastEntry() {
		LinkedEntry<K, V> e = header.prev;
		if (e != header)
			return e;
		else
			throw new NoSuchElementException();
	}

	@Override public K getLastKey() {
		return getLastEntry().getKey();
	}

	@Override public V getLastValue() {
		return getLastEntry().getValue();
	}

	@Override public Entry<K, V> put(int index, K key, V value) {
		if (index < 0 || index > size())
			throw new IndexOutOfBoundsException();
		LinkedEntry<K, V> old = header.next;
		boolean before = false;
		for (int i = 0; i < index; i++) {
			old = old.next;
			if (ObjectUtil.equal(key, old.key))
				before = true;
		}
		if (before)
			old = old.next;
		LinkedEntry<K, V> e = map.get(key);
		if (e != null) {
			if (index == size())
				throw new IndexOutOfBoundsException();
			if (e != old)
				e.moveBefore(old);
			e.value = value;
		}
		else {
			e = new LinkedEntry<>(key, value);
			e.insertBefore(old);
			map.put(key, e);
		}
		return old;
	}

	@Override public Entry<K, V> putFirst(K key, V value) {
		LinkedEntry<K, V> old = header.next;
		LinkedEntry<K, V> e = map.get(key);
		if (e != null) {
			e.moveAfter(header);
			e.value = value;
		}
		else {
			e = new LinkedEntry<>(key, value);
			e.insertAfter(header);
			map.put(key, e);
		}
		return old != header ? old : null;
	}

	@Override public Entry<K, V> putLast(K key, V value) {
		LinkedEntry<K, V> old = header.prev;
		LinkedEntry<K, V> e = map.get(key);
		if (e != null) {
			e.moveBefore(header);
			e.value = value;
		}
		else {
			e = new LinkedEntry<>(key, value);
			e.insertBefore(header);
			map.put(key, e);
		}
		return old != header ? old : null;
	}

	@Override public void putAll(int index, Map<? extends K, ? extends V> map) {
		for (Entry<? extends K, ? extends V> e : map.entrySet())
			put(index++, e.getKey(), e.getValue());
	}

	@Override public Entry<K, V> remove(int index) {
		Entry<K, V> e = getEntry(index);
		remove(e.getKey());
		return e;
	}

	@Override public Entry<K, V> removeFirst() {
		Entry<K, V> e = getFirstEntry();
		remove(e.getKey());
		return e;
	}

	@Override public Entry<K, V> removeLast() {
		Entry<K, V> e = getLastEntry();
		remove(e.getKey());
		return e;
	}

	@Override public void moveUp(K key) {
		LinkedEntry<K, V> e = map.get(key);
		if (e != null && e.next != header)
			e.moveAfter(e.next);
	}

	@Override public void moveDown(K key) {
		LinkedEntry<K, V> e = map.get(key);
		if (e != null && e.prev != header)
			e.moveBefore(e.prev);
	}

	@Override public void moveTo(K key, int index) {
		LinkedEntry<K, V> e = map.get(key);
		if (e != null)
			e.moveBefore((LinkedEntry<K, V>)getEntry(index));
	}

	@Override public void swap(K key1, K key2) {
		LinkedEntry<K, V> e1 = map.get(key1);
		LinkedEntry<K, V> e2 = map.get(key2);
		if (e1 != null && e2 != null && !e1.equals(e2))
			e1.swapWith(e2);
	}

	@Override public int indexOfKey(Object key) {
		int i = 0;
		for (LinkedEntry<K, V> e = header.next; e != header; e = e.next) {
			K ek = e.key;
			if (ObjectUtil.equal(ek, key))
				return i;
			i++;
		}
		return -1;
	}

	@Override public int indexOfValue(Object value) {
		int i = 0;
		for (LinkedEntry<K, V> e = header.next; e != header; e = e.next) {
			V ev = e.value;
			if (ObjectUtil.equal(ev, value))
				return i;
			i++;
		}
		return -1;
	}

	@Override public int lastIndexOfValue(Object value) {
		int i = size();
		for (LinkedEntry<K, V> e = header.prev; e != header; e = e.prev) {
			i--;
			V ev = e.value;
			if (ObjectUtil.equal(ev, value))
				return i;
		}
		return -1;
	}

	@Override public Iterator<Map.Entry<K, V>> iterator() {
		return new EntriesIterator();
	}

	@Override public Entry<K, V>[] toArray() {
		int size = size();
		Entry<K, V>[] entries = (Entry<K, V>[])new LinkedEntry[size];
		LinkedEntry<K, V> e = header.next;
		for (int i = 0; i < size; i++) {
			entries[i] = e;
			e = e.next;
		}
		return entries;
	}

	@Override public void sortByKeys() {
		sort(new MapEntryComparatorByKeys<K, V>(null));
	}

	@Override public void sortByKeys(Comparator<? super K> comparator) {
		sort(new MapEntryComparatorByKeys<K, V>(comparator));
	}

	@Override public void sortByValues() {
		sort(new MapEntryComparatorByValues<K, V>(null));
	}

	@Override public void sortByValues(Comparator<? super V> comparator) {
		sort(new MapEntryComparatorByValues<K, V>(comparator));
	}

	private void sort(Comparator<Entry<K, V>> comparator) {
		List<Entry<K, V>> entries = new ArrayList<>(entrySet());
		Collections.sort(entries, comparator);
		clear();
		for (Entry<K, V> entry : entries)
			put(entry.getKey(), entry.getValue());
	}

	private static final class MapEntryComparatorByKeys<K, V> implements Comparator<Entry<K, V>> {

		private Comparator<? super K> comparator;

		public MapEntryComparatorByKeys() {
			super();
		}

		public MapEntryComparatorByKeys(Comparator<? super K> comparator) {
			super();
			this.comparator = comparator;
		}

		@Override public int compare(Entry<K, V> o1, Entry<K, V> o2) {
			K k1 = o1.getKey();
			K k2 = o2.getKey();
			return comparator != null ? comparator.compare(k1, k2) : ((Comparable<K>)k1).compareTo(k2);
		}
	}

	private static final class MapEntryComparatorByValues<K, V> implements Comparator<Entry<K, V>> {

		private Comparator<? super V> comparator;

		public MapEntryComparatorByValues() {
			super();
		}

		public MapEntryComparatorByValues(Comparator<? super V> comparator) {
			super();
			this.comparator = comparator;
		}

		@Override public int compare(Entry<K, V> o1, Entry<K, V> o2) {
			V v1 = o1.getValue();
			V v2 = o2.getValue();
			return comparator != null ? comparator.compare(v1, v2) : ((Comparable<V>)v1).compareTo(v2);
		}
	}


	// Map interface

	@Override public V get(Object key) {
		LinkedEntry<K, V> e = map.get(key);
		return e != null ? e.value : null;
	}

	@Override public V put(K key, V value) {
		LinkedEntry<K, V> e = map.get(key);
		if (e != null) {
			V old = e.value;
			e.value = value;
			return old;
		}
		else {
			e = new LinkedEntry<>(key, value);
			e.insertBefore(header);
			map.put(key, e);
			return null;
		}
	}

	@Override public V remove(Object key) {
		LinkedEntry<K, V> e = map.get(key);
		if (e != null) {
			e.remove();
			map.remove(key);
			return e.value;
		}
		else
			return null;
	}

	@Override public boolean containsValue(Object value) {
		if (value != null) {
			for (V v : values())
				if (value.equals(v))
					return true;
		}
		else {
			for (V v : values())
				if (v == null)
					return true;
		}
		return false;
	}

	@Override public int size() {
		return map.size();
	}

	@Override public boolean isEmpty() {
		return map.isEmpty();
	}

	@Override public void clear() {
		map.clear();
		createHeader();
	}



	private transient Set<Entry<K, V>> entries;

	@Override public Set<Entry<K, V>> entrySet() {
		if (entries == null)
			entries = new AbstractSet<Entry<K, V>>() {
				@Override public int size() {
					return map.size();
				}
				@Override public Iterator<Entry<K, V>> iterator() {
					return new EntriesIterator();
				}
			};
		return entries;
	}

	private transient Set<K> keys;

	@Override public Set<K> keySet() {
		if (keys == null)
			keys = new AbstractSet<K>() {
				@Override public int size() {
					return map.size();
				}
				@Override public Iterator<K> iterator() {
					return new KeysIterator();
				}
			};
		return keys;
	}

	private transient Collection<V> values;

	@Override public Collection<V> values() {
		if (values == null)
			values = new AbstractCollection<V>() {
				@Override public int size() {
					return map.size();
				}
				@Override public Iterator<V> iterator() {
					return new ValuesIterator();
				}
			};
		return values;
	}


	private transient List<Entry<K, V>> entryList;

	@Override public List<Entry<K, V>> entryList() {
		if (entryList == null)
			entryList = new AbstractList<Entry<K, V>>() {
				@Override public int size() {
					return map.size();
				}
				@Override public Entry<K, V> get(int index) {
					return getEntry(index);
				}
			};
		return entryList;
	}

	private transient List<K> keyList;

	@Override public List<K> keyList() {
		if (keyList == null)
			keyList = new AbstractList<K>() {
				@Override public int size() {
					return map.size();
				}
				@Override public K get(int index) {
					return getKey(index);
				}
			};
		return keyList;
	}

	private transient List<V> valueList;

	@Override public List<V> valueList() {
		if (valueList == null)
			valueList = new AbstractList<V>() {
				@Override public int size() {
					return map.size();
				}
				@Override public V get(int index) {
					return getValue(index);
				}
				@Override public V set(int index, V element) {
					return getEntry(index).setValue(element);
				}
			};
		return valueList;
	}


	// Creates swallow clone. Keys and values are not cloned.
	@Override public OrderedHashMap<K, V> clone() {
		return new OrderedHashMap<>(this);
	}

	private static final class LinkedEntry<K, V> extends MapEntry<K, V> {

		private LinkedEntry<K, V> prev;
		private LinkedEntry<K, V> next;

		// Needed for serialization
		public LinkedEntry() {
			super(null, null);
		}

		public LinkedEntry(K key, V value) {
			super(key, value);
		}

		public void remove() {
			prev.next = next;
			next.prev = prev;
		}

		public void insertBefore(LinkedEntry<K, V> e) {
			next = e;
			prev = e.prev;
			e.prev = this;
			prev.next = this;
		}

		public void insertAfter(LinkedEntry<K, V> e) {
			prev = e;
			next = e.next;
			e.next = this;
			next.prev = this;
		}

		public void moveBefore(LinkedEntry<K, V> e) {
			remove();
			insertBefore(e);
		}

		public void moveAfter(LinkedEntry<K, V> e) {
			remove();
			insertAfter(e);
		}

		public void swapWith(LinkedEntry<K, V> e) {
			if (e != next) {
				LinkedEntry<K, V> t = prev; prev = e.prev; e.prev = t;
				t = next; next = e.next; e.next = t;
			}
			else {
				next = e.next;
				e.next = this;
				e.prev = prev;
				prev = e;
			}
		}

		@Override public String toString() {
			return value != null ? value.toString() : "null";
		}
	}

	private abstract class OrderedMapIterator<E> implements Iterator<E> {

		private LinkedEntry<K, V> curr, prev;

		public OrderedMapIterator() {
			super();
			curr = header.next;
		}

		@Override public boolean hasNext() {
			return curr != header;
		}

		@Override public void remove() {
			if (prev == null)
				throw new IllegalStateException();
			map.remove(prev.key);
			prev.remove();
			prev = null;
		}

		protected LinkedEntry<K, V> nextEntry() {
			if (curr == header)
				throw new NoSuchElementException();
			prev = curr;
			curr = curr.next;
			return prev;
		}
	}

	private final class EntriesIterator extends OrderedMapIterator<Map.Entry<K, V>> {
		@Override public Map.Entry<K, V> next() {
			return nextEntry();
		}
	}

	private final class KeysIterator extends OrderedMapIterator<K> {
		@Override public K next() {
			return nextEntry().key;
		}
	}

	private final class ValuesIterator extends OrderedMapIterator<V> {
		@Override public V next() {
			return nextEntry().value;
		}
	}
}
