package org.strangeforest.concurrent;

import java.util.*;

/**
 * <p>This is <tt>LockableMap</tt> implementation based on <tt>HashMap</tt>.</p>
 * <p>It uses <tt>ReentrantLock</tt> class for underlying locking mechanism.</p>
 * <p>This class <i>is</i> thread-safe.</p>
 */
public class LockableHashMap<K, V> extends BaseLockableMap<K, V> {

	public LockableHashMap() {
		super(new HashMap<>(), new HashMap<>());
	}

	public LockableHashMap(int initialCapacity) {
		super(new HashMap<>(initialCapacity), new HashMap<>(initialCapacity));
	}

	public LockableHashMap(int initialCapacity, float loadFactor) {
		super(new HashMap<>(initialCapacity, loadFactor), new HashMap<>(initialCapacity, loadFactor));
	}
}
