package test.concurrent;

import java.util.*;
import java.util.concurrent.*;

import org.junit.*;
import org.strangeforest.concurrent.*;

import static org.junit.Assert.*;

public class LockableMapTest {

	private static final int THREADS = 20;
	private static final int COUNT = 1000;
	private static final long LIMIT = 500L;
	private static final int KEYS = 10;

	private static final String KEY = "key";
	private static final String HIGH_KEY = "high-key";

	@Test
	public void testConcurrency() throws Exception {
		LockableMap<String, Long> map = new LockableHashMap<>();
		Thread[] threads = new Thread[THREADS];
		for (int i = 0; i < THREADS; i++) {
			threads[i] = new TestThread(map);
			threads[i].start();
		}

		for (int i = 0; i < THREADS; i++)
			threads[i].join();

		long sum = 0L;
		Long highValue = map.get(HIGH_KEY);
		if (highValue != null)
			sum += highValue *LIMIT;
		for (int i = 0; i < KEYS; i++) {
			Long value = map.get(KEY + i);
			if (value != null)
				sum += value;
		}
		assertEquals("Test failed: " + sum + "!=" + THREADS * COUNT, THREADS * COUNT, sum);
	}

	private static class TestThread extends Thread {

		private LockableMap<String, Long> map;
		private Random rnd;

		public TestThread(LockableMap<String, Long> map) {
			this.map = map;
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

		private void inc(String key, long limit) throws InterruptedException {
			map.lock(key);
			Long value = map.get(key);
			if (value == null)
				value = 1L;
			else
				value = value + 1L;
			if (limit == 0 || value < limit)
				map.put(key, value);
			else {
				map.remove(key);
				inc(HIGH_KEY, 0);
			}
			map.unlock(key);
		}
	}

	@Test
	public void testTryClear() throws Exception {
		final CyclicBarrier barrier = new CyclicBarrier(2);
		final Semaphore cleared = new Semaphore(0);
		final LockableMap<String, Long> map = new LockableHashMap<>();
		map.lockedPut("1", 1L);
		ThreadUtil.runInThread(() -> {
			try {
				barrier.await();
				map.tryClear();
				cleared.release();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		});
		map.lock("2");
		try {
			map.put("2", 2L);
			assertEquals(map.size(), 2);
			barrier.await();
			cleared.acquire();
			assertEquals(map.size(), 1);
		}
		finally {
			map.unlock("2");
		}
		assertEquals(map.size(), 0);
	}

	@Test
	public void testClearLock() throws Exception {
		final CyclicBarrier barrier = new CyclicBarrier(2);
		final Semaphore cleared = new Semaphore(0);
		final LockableMap<String, Long> map = new LockableHashMap<>();
		map.lockedPut("1", 1L);
		ThreadUtil.runInThread(() -> {
			try {
				barrier.await();
				map.clear();
				cleared.release();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		});
		map.lock("2");
		try {
			map.put("2", 2L);
			assertEquals(map.size(), 2);
			barrier.await();
			Thread.sleep(20L);
			assertFalse(map.isEmpty());
			assertEquals(cleared.availablePermits(), 0);
		}
		finally {
			map.unlock("2");
		}
		cleared.acquire();
		assertEquals(map.size(), 0);
	}
}
