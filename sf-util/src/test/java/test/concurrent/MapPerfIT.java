package test.concurrent;

import java.text.*;
import java.util.*;

import org.junit.*;
import org.strangeforest.cache.*;
import org.strangeforest.concurrent.*;
import org.strangeforest.util.*;

public class MapPerfIT {

	private static final int SIZE  = 1000;
	private static final int COUNT = 100000;

	@Test
	public void test() throws Exception {
		testMapAccess(100, new HashMap<>(), null, false);

		testMapAccess(COUNT, new HashMap<>(SIZE), "HashMap");
		testMapAccess(COUNT, new LRUCache<>(SIZE), "LRUCache");
		testMapAccess(COUNT, new MLFUCache<>(SIZE), "MLFUCache");
		testMapAccess(COUNT, new LRUCache<>(SIZE, new SoftHashMap<>(SIZE*3/2)), "Soft LRUCache");
		testSyncMapAccess(COUNT, new HashMap<>(SIZE), "HashMap");
		testSyncMapAccess(COUNT, new LRUCache<>(SIZE), "LRUCache");
		testSyncMapAccess(COUNT, new MLFUCache<>(SIZE), "MLFUCache");
		testConcMapAccess(COUNT, new LockableHashMap<>(SIZE), "LockableHashMap");
		testConcMapAccess(COUNT, new LockableLRUCache<>(SIZE), "LockableLRUCache");
		testConcMapAccess(COUNT, new LockableMLFUCache<>(SIZE), "LockableMLFUCache");
	}

	private static void testMapAccess(int count, Map<Integer, Long> map, String desc) throws Exception {
		testMapAccess(count, map, desc, true);
	}

	private static void testMapAccess(int count, Map<Integer, Long> map, String desc, boolean show) throws Exception {
		Thread.sleep(1000);
		long s = 0L;
		long t0 = System.currentTimeMillis();
		for (int i = 0; i < count; i++) {
			Integer key = i%SIZE;
			Long value = map.get(key);
			if (value == null) {
				value = new Long(i);
				map.put(key, value);
			}
			s += value;
		}
		if (show)
			output(desc, t0, count, s);
	}

	private static void testSyncMapAccess(int count, Map<Integer, Long> map, String desc) throws Exception {
		Thread.sleep(1000);
		long s = 0L;
		long t0 = System.currentTimeMillis();
		for (int i = 0; i < count; i++) {
			Integer key = i%SIZE;
			Long value;
			synchronized (map) {
				value = map.get(key);
				if (value == null) {
					value = new Long(i);
					map.put(key, value);
				}
			}
			s += value;
		}
		output("Synchronized " + desc, t0, count, s);
	}

	private static void testConcMapAccess(int count, LockableMap<Integer, Long> map, String desc) throws Exception {
		Thread.sleep(1000);
		long s = 0L;
		long t0 = System.currentTimeMillis();
		for (int i = 0; i < count; i++) {
			Integer key = i%SIZE;
			Long value;
			map.lock(key);
			try {
				value = map.get(key);
				if (value == null) {
					value = new Long(i);
					map.put(key, value);
				}
			}
			finally {
				map.unlock(key);
			}
			s += value;
		}
		output("Concurrent " + desc, t0, count, s);
	}

	private static void output(String msg, long t0, int size, long s) {
//		System.out.println(s);
		System.out.println(msg + MessageFormat.format(" access: {0,number,####.##}ns", ((System.currentTimeMillis() - t0)*1000000L)/size));
	}
}
