package org.strangeforest.cache;

import java.util.*;
import java.util.concurrent.*;

import org.strangeforest.concurrent.*;
import org.strangeforest.util.*;

/**
 * <p><tt>LRUCache</tt> is <tt>Cache</tt> implementation that uses LRU algorithm
 * to choose what entry to remove when capacity limit is reached.</p>
 */
public class LRUCache<K, V> extends AbstractMap<K, V> implements Cache<K, V> {

	private final Map<K, LinkedValue<K, V>> map;
	private final FixedRateScheduler checkExpiryScheduler;
	private int capacity;
	private long expiryPeriod;
	private long gets;
	private long hits;
	private List<CacheListener<K, V>> listeners;
	protected final LinkedValue<K, V> header;

	/**
	 * Creates <tt>LRUCache</tt> with unlimited capacity.
	 */
	public LRUCache() {
		this(0, null);
	}

	/**
	 * Creates <tt>LRUCache</tt> with specified capacity.
	 * @param capacity cache capacity. Capacity of 0 means unlimited capacity.
	 */
	public LRUCache(int capacity) {
		this(capacity, null);
	}

	/**
	 * Creates <tt>LRUCache</tt> with unlimited capacity based on specified map.
	 * Useful for building memory-sensitive cache by passing SoftHashMap instance.
	 * @param map that this cache will be based upon. Entries in the specified map are ordered by its entrySet.iterator().
	 */
	public LRUCache(Map<? extends K, ? extends V> map) {
		this(0, map);
	}

	/**
	 * Creates <tt>LRUCache</tt> with specified capacity based on specified map.
	 * Useful for building memory-sensitive cache by passing SoftHashMap instance.
	 * @param capacity cache capacity. Capacity of 0 means unlimited capacity.
	 * @param map that this cache will be based upon. Entries in the specified map are ordered by its entrySet.iterator().
	 */
	public LRUCache(int capacity, Map<? extends K, ? extends V> map) {
		super();
		if (capacity < 0)
			throw new IllegalArgumentException("Invalid capacity: " + capacity);
		this.capacity = capacity;

		header = new LinkedValue<>(null, null);
		initHeader();

		if (map != null) {
			this.map = (Map<K, LinkedValue<K, V>>)map;
			for (Map.Entry<K, LinkedValue<K, V>> e : this.map.entrySet()) {
				K key = e.getKey();
				LinkedValue<K, V> lv = new LinkedValue<>(key, (V)e.getValue());
				lv.insertAfter(header);
				e.setValue(lv);
			}
			ensureCapacity();
		}
		else
			this.map = (capacity > 0) ? new HashMap<>(capacity*3/2) : new HashMap<>();

		checkExpiryScheduler = new FixedRateScheduler(this::removeExpiredEntries, 0L, "Cache Expirer");
	}

	private void initHeader() {
		header.prev = header.next = header;
	}


	// Cache interface

	@Override public int getCapacity() {
		return capacity;
	}

	@Override public void setCapacity(int capacity) {
		this.capacity = capacity;
		removeExpiredEntries();
		ensureCapacity();
	}

	@Override public long getExpiryPeriod() {
		return expiryPeriod;
	}

	@Override public void setExpiryPeriod(long expiryPeriod) {
		this.expiryPeriod = expiryPeriod;
	}

	@Override public long getCheckExpiryPeriod() {
		return checkExpiryScheduler.getPeriod();
	}

	@Override public void setCheckExpiryPeriod(long checkExpiryPeriod) {
		checkExpiryScheduler.setPeriod(checkExpiryPeriod);
	}

	@Override public ScheduledExecutorService getCheckExpiryExecutor() {
		return checkExpiryScheduler.getExecutor();
	}

	@Override public void setCheckExpiryExecutor(ScheduledExecutorService executor) {
		checkExpiryScheduler.setExecutor(executor);
	}

	@Override public void startBackgroundExpiry() {
		checkExpiryScheduler.schedule();
	}

	@Override public void stopBackgroundExpiry() {
		checkExpiryScheduler.shutdown();
	}

	@Override public void removeExpiredEntries() {
		if (expiryPeriod != 0L) {
			long now = System.currentTimeMillis();
			for (LinkedValuesIterator lvs  = new LinkedValuesIterator(); lvs.hasNext(); ) {
				LinkedValue<K, V> lv = lvs.next();
				if (now > lv.lastPut + expiryPeriod)
					lvs.remove();
			}
		}
	}

	@Override public CacheStatistics getStatistics() {
		return new CacheStatistics(map.size(), capacity, gets, hits);
	}

	@Override public void resetStatistics() {
		gets = hits = 0L;
	}

	@Override public void addCacheListener(CacheListener<K, V> listener) {
		if (listeners == null)
			listeners = new ArrayList<>(4);
		listeners.add(listener);
	}

	@Override public void removeCacheListener(CacheListener<K, V> listener) {
		if (listeners != null)
			listeners.remove(listener);
	}


	// Map interface

	@Override public V get(Object key) {
		gets++;
		LinkedValue<K, V> lv = map.get(key);
		if (lv != null) {
			if (lv.isValid(expiryPeriod)) {
				touch(lv);
				hits++;
				return lv.value;
			}
			else
				doRemove(lv);
		}
		return null;
	}

	/**
	 * Puts new entry in the cache. If cache has reached its capacity limit, last entry is removed.
	 * @param key key
	 * @param value value
	 * @return previous value for specified key
	 */
	@Override public V put(K key, V value) {
		LinkedValue<K, V> lv = map.get(key);
		if (lv != null) {
			touch(lv);
			lv.touchPut();
			V old = lv.value;
			lv.value = value;
			return old;
		}
		else {
			lv = new LinkedValue<>(key, value);
			lv.insertAfter(header);
			map.put(key, lv);
			if (capacity > 0 && map.size() > capacity)
				removeLastEntry();
			return null;
		}
	}

	@Override public V remove(Object key) {
		LinkedValue<K, V> lv = map.get(key);
		if (lv != null) {
			doRemove(lv);
			return lv.value;
		}
		else
			return null;
	}

	@Override public void clear() {
		map.clear();
		initHeader();
	}

	@Override public int size() {
		return map.size();
	}

	@Override public boolean isEmpty() {
		return map.isEmpty();
	}

	@Override public boolean containsKey(Object key) {
		// Take advantage of possibly faster implementation in underlying map then default AbstractMap implementation
		if (map.containsKey(key)) {
			LinkedValue<K, V> lv = map.get(key);
			if (lv != null) {
				if (lv.isValid(expiryPeriod))
					return true;
				else
					doRemove(lv);
			}
		}
		return false;
	}

	@Override public boolean containsValue(Object value) {
		// See containsKey comment
		// Because LinkedValue.equals is overridden it is OK to just delegate containsValue method to the underlying Map.
		removeExpiredEntries();
		return map.containsValue(value);
	}

	@Override public void putAll(Map<? extends K, ? extends V> t) {
		for (Map.Entry<? extends K, ? extends V> e : t.entrySet()) {
			K key = e.getKey();
			LinkedValue<K, V> lv = new LinkedValue<>(key, e.getValue());
			lv.insertAfter(header);
			map.put(key, lv);
		}
		removeExpiredEntries();
		ensureCapacity();
	}

	protected void touch(LinkedValue<K, V> lv) {
		lv.remove();
		lv.insertAfter(header);
	}

	private void doRemove(LinkedValue<K, V> lv) {
		lv.remove();
		map.remove(lv.key);
		if (listeners != null) {
			MapEntry<K, V> entry = new MapEntry<>(lv.key, lv.value);
			for (CacheListener<K, V> listener : listeners)
				listener.entryRemoved(entry);
		}
	}

	private void removeLastEntry() {
		doRemove(header.prev);
	}

	private void ensureCapacity() {
		if (capacity > 0) {
			while (map.size() > capacity)
				removeLastEntry();
		}
	}

	private transient Set<K> keys;

	@Override public Set<K> keySet() {
		removeExpiredEntries();
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
		removeExpiredEntries();
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

	private transient Set<Map.Entry<K, V>> entries;

	@Override public Set<Map.Entry<K, V>> entrySet() {
		removeExpiredEntries();
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

	protected static final class LinkedValue<K, V> {

		protected final K key;
		protected V value;
		protected LinkedValue<K, V> prev;
		protected LinkedValue<K, V> next;
		private long lastPut;

		public LinkedValue(K key, V value) {
			super();
			this.key = key;
			this.value = value;
			touchPut();
		}

		@Override public boolean equals(Object o) {
			return value == o || (value != null && value.equals(o));
		}

		@Override public int hashCode() {
			return value != null ? value.hashCode() : 0;
		}

		@Override public String toString() {
			return value != null ? value.toString() : "null";
		}

		public void remove() {
			prev.next = next;
			next.prev = prev;
		}

		public void insertAfter(LinkedValue<K, V> value) {
			prev = value;
			next = value.next;
			value.next = this;
			next.prev = this;
		}

		public void insertBefore(LinkedValue<K, V> value) {
			next = value;
			prev = value.prev;
			value.prev = this;
			prev.next = this;
		}

		public void touchPut() {
			lastPut = System.currentTimeMillis();
		}

		public boolean isValid(long expiryPeriod) {
			return expiryPeriod == 0L || System.currentTimeMillis() <= lastPut + expiryPeriod;
		}
	}

	private abstract class CacheIterator<E> implements Iterator<E> {

		private LinkedValue<K, V> curr, prev;

		public CacheIterator() {
			super();
			curr = header.prev;
		}

		@Override public boolean hasNext() {
			return curr != header;
		}

		@Override public void remove() {
			if (prev == null)
				throw new IllegalStateException();
			doRemove(prev);
			prev = null;
		}

		protected LinkedValue<K, V> nextEntry() {
			if (curr == header)
				throw new NoSuchElementException();
			prev = curr;
			curr = curr.prev;
			return prev;
		}
	}

	private class EntriesIterator extends CacheIterator<Map.Entry<K, V>> {
		@Override public Map.Entry<K, V> next() {
			LinkedValue<K, V> lv = nextEntry();
			return new Entry(lv.key, lv.value);
		}
	}

	private class KeysIterator extends CacheIterator<K> {
		@Override public K next() {
			return nextEntry().key;
		}
	}

	private class ValuesIterator extends CacheIterator<V> {
		@Override public V next() {
			return nextEntry().value;
		}
	}

	private class LinkedValuesIterator extends CacheIterator<LinkedValue<K, V>> {
		@Override public LinkedValue<K, V> next() {
			return nextEntry();
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
