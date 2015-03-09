package org.strangeforest.util;

import java.lang.ref.*;
import java.util.*;

/* TODO Add ReferenceQueue for SoftReferences and remove keys from the map when
   GC enqueue references into it. This needs a separate thread that will block
   on ReferenceQueue and then SoftHashMap needs to be synchronized. */

/**
 * <p><tt>Map</tt> implementation based on <tt>HashMap</tt> that wrapps map values with <tt>SoftReference</tt>s.</p>
 */
public class SoftHashMap<K, V> extends AbstractMap<K, V> {

	private Map<K, SoftReference<?>> map;

	private static final Object NULL_VALUE = new Object();

	public SoftHashMap() {
		super();
		map = new HashMap<>();
	}

	public SoftHashMap(int initialCapacity) {
		super();
		map = new HashMap<>(initialCapacity);
	}

	public SoftHashMap(int initialCapacity, float loadFactor) {
		super();
		map = new HashMap<>(initialCapacity, loadFactor);
	}

	public SoftHashMap(Map<? extends K, ? extends V> m) {
		this();
		putAll(m);
	}

	@Override public V get(Object key) {
		Object value = null;
		SoftReference<?> ref = map.get(key);
		if (ref != null) {
			value = ref.get();
			if (value == null)
				remove(key);
		}
		return unmaskNull(value);
	}

	@Override public V put(K key, V value) {
		SoftReference<?> ref = map.put(key, new SoftReference(maskNull(value)));
		return ref != null ? unmaskNull(ref.get()) : null;
	}

	@Override public V remove(Object key) {
		SoftReference<?> ref = map.remove(key);
		if (ref != null)
			return unmaskNull(ref.get());
		else
			return null;
	}

	@Override public boolean containsKey(Object key) {
		return map.containsKey(key);
	}

	@Override public boolean containsValue(Object value) {
		int count = size();
		SoftReference<?>[] refs = map.values().toArray(new SoftReference<?>[count]);
		if (value != null) {
			for (SoftReference<?> ref : refs)
				if (value.equals(ref.get()))
					return true;
		}
		else {
			for (SoftReference<?> ref : refs)
				if (ref.get() == NULL_VALUE)
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
	}

	private transient Set<Map.Entry<K, V>> entries;

	@Override public Set<Map.Entry<K, V>> entrySet() {
		if (entries == null)
			entries = new AbstractSet<Map.Entry<K, V>>() {
				@Override public int size() {
					return SoftHashMap.this.size();
				}
				@Override public Iterator<Map.Entry<K, V>> iterator() {
					return new EntriesIterator();
				}
			};
		return entries;
	}


	private transient Collection<V> values;

	@Override public Collection<V> values() {
		if (values == null)
			values = new AbstractCollection<V>() {
				@Override public int size() {
					return SoftHashMap.this.size();
				}
				@Override public Iterator<V> iterator() {
					return new ValuesIterator();
				}
			};
		return values;
	}

	public void purge() {
		int count = size();
		Map.Entry<K, SoftReference>[] entries = (Map.Entry<K, SoftReference>[])map.entrySet().toArray(new Map.Entry[count]);
		for (Map.Entry<K, SoftReference> entry : entries)
			if (entry.getValue().get() == null)
				remove(entry.getKey());
	}

	private Object maskNull(V value) {
		return value == null ? NULL_VALUE : value;
	}

	private V unmaskNull(Object value) {
		return value == NULL_VALUE ? null : (V)value;
	}


	private abstract class SoftMapIterator<E> implements Iterator<E> {

		protected Iterator<Map.Entry<K, SoftReference<?>>> iter;

		public SoftMapIterator() {
			super();
			iter = map.entrySet().iterator();
		}

		@Override public boolean hasNext() {
			return iter.hasNext();
		}

		@Override public void remove() {
			iter.remove();
		}
	}

	private class EntriesIterator extends SoftMapIterator<Map.Entry<K, V>> {
		@Override public Map.Entry<K, V> next() {
			Map.Entry<K, SoftReference<?>> entry = iter.next();
			return new Entry(entry.getKey(), unmaskNull(entry.getValue().get()));
		}
	}

	private class ValuesIterator extends SoftMapIterator<V> {
		@Override public V next() {
			Map.Entry<K, SoftReference<?>> entry = iter.next();
			return unmaskNull(entry.getValue().get());
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
