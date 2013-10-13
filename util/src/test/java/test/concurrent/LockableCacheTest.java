package test.concurrent;

import java.util.*;

import org.junit.*;
import org.strangeforest.cache.*;
import org.strangeforest.concurrent.*;
import org.strangeforest.util.*;

import static org.junit.Assert.*;

public class LockableCacheTest {

	private static final int THREADS = 20;
	private static final int COUNT = 1000;
	private static final long LIMIT = 500L;
	private static final int KEYS = 10;
	private static final int SIZE = 11;

	@Test
	public void testLock() throws InterruptedException {
		TestLockableLRUCache<String, String> cache = new TestLockableLRUCache<>(2);
		cache.setExpiryPeriod(20);
		cache.lockedPut("1", "10");
		cache.lockedPut("2", "20");
		cache.lock("1");
		try {
			cache.lockedPut("3", "30");
		}
		finally {
			cache.unlock("1");
		}
		assertEquals(cache.size(), 2);
		assertEquals(cache.getLocksSize(), 2);
		Thread.sleep(40);
		cache.removeExpiredEntries();
		assertEquals(cache.getLocksSize(), 0);
	}

	@Test
	public void testConcurrency() throws Exception {
		Thread.sleep(1000);
//		long t0 = System.currentTimeMillis();
		LockableCache<String, Long> cache = new LockableLRUCache<>(SIZE, new SoftHashMap<String, Long>());
//		Cache<String, Long> cache = new LRUCache<String, Long>(SIZE, new SoftHashMap());
		Thread[] threads = new Thread[THREADS];
		for (int i = 0; i < THREADS; i++) {
			threads[i] = new LockThread(cache);
//			threads[i] = new SyncThread(cache);
			threads[i].start();
		}

		for (int i = 0; i < THREADS; i++)
			threads[i].join();

		long sum = 0L;
		Long highValue = cache.get(HIGH_KEY);
		if (highValue != null)
			sum += highValue *LIMIT;
		for (int i = 0; i < KEYS; i++) {
			Long value = cache.get(KEY + i);
			if (value != null)
				sum += value;
		}

		assertEquals("Test failed: " + sum + "!=" + THREADS*COUNT, THREADS*COUNT, sum);

		System.out.println("Hit ratio: " + cache.getStatistics().hitRatio());
//		System.out.println((System.currentTimeMillis()-t0)/1000.0);
	}

	private static final String KEY = "key";
	private static final String HIGH_KEY = "high-key";

	public static abstract class TestThread extends Thread {

		protected Random rnd;
		private int j;

		public TestThread() {
			rnd = new Random();
		}

		public void run() {
			try {
				for (int i = 0; i < COUNT; i++)
					inc(KEY + rnd.nextInt(KEYS), LIMIT);
			}
			catch (InterruptedException ex) {
				ex.printStackTrace();
			}
		}

		protected void doSomething(String key) throws InterruptedException {
			for (int i = 0; i < 5000; i++)
				j++;
			if (key == HIGH_KEY)
				Thread.sleep(1);
		}

		protected abstract void inc(String key, long limit) throws InterruptedException;
	}

	public static class LockThread extends TestThread {

		private LockableCache<String, Long> cache;

		public LockThread(LockableCache<String, Long> cache) {
			this.cache = cache;
		}

		protected void inc(String key, long limit) throws InterruptedException {
			cache.lock(key);
			Long value = cache.get(key);
			if (value == null)
				value = 1L;
			else
				value = value + 1L;
			doSomething(key);
			if (limit == 0 || value < limit)
				cache.put(key, value);
			else {
				cache.remove(key);
				inc(HIGH_KEY, 0);
			}
			cache.unlock(key);
		}
	}

	public static class SyncThread extends TestThread {

		private final Cache<String, Long> cache;

		public SyncThread(Cache<String, Long> cache) {
			this.cache = cache;
		}

		protected void inc(String key, long limit) throws InterruptedException {
			boolean incHigh = false;
			synchronized (cache) {
				Long value = cache.get(key);
				if (value == null)
					value = 1L;
				else
					value = value + 1L;
				doSomething(key);
				if (limit == 0 || value < limit)
					cache.put(key, value);
				else {
					cache.remove(key);
					incHigh = true;
				}
			}
			if (incHigh)
				inc(HIGH_KEY, 0);
		}
	}

	class TestLockableLRUCache<K, V> extends LockableLRUCache<K, V> {

		public TestLockableLRUCache(int capacity) {
			super(capacity);
		}

		public int getLocksSize() {
			return getLockMap().size();
		}
	}
}
