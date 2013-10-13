package test.cache;

import java.util.*;

import org.junit.*;
import org.strangeforest.cache.*;
import org.strangeforest.util.*;

import static org.junit.Assert.*;

public class CacheTest {

	@Test
	public void testLRUCache() throws Exception {
		test(new LRUCache<Integer, Integer>(5), new LRUCache<Integer, Integer>(5));
	}

	@Test
	public void testMLFUCache() throws Exception {
		test(new MLFUCache<Integer, Integer>(5), new MLFUCache<Integer, Integer>(5));
	}

	private static void test(Cache<Integer, Integer> cache, Cache<Integer, Integer> cache2) {
		for (int i = 1; i <= 10; i++)
			cache.put(i, 10*i);

		assertEquals(100, cache.get(10).intValue());
		assertEquals(60, cache.put(6, 61).intValue());
		assertEquals(80, cache.get(8).intValue());
		assertEquals(100, cache.put(10, 101).intValue());
		assertEquals(90, cache.remove(9).intValue());

		cache2.put(7, 70);
		cache2.put(6, 61);
		cache2.put(8, 80);
		cache2.put(10, 101);

		assertEquals(cache, cache2);
		assertEquals(cache.toString(), cache2.toString());
	}

	@Test
	public void testExpiryPeriod() throws Exception {
		Cache<Integer, Integer> cache = new LRUCache<>(5);
		cache.setExpiryPeriod(25);
		cache.put(1, 1);
		cache.put(2, 2);
		cache.put(3, 3);
		cache.put(4, 4);
		assertEquals(cache.get(1).intValue(), 1);
		Thread.sleep(20);
		cache.get(1);
		cache.get(2);
		cache.get(3);
		cache.get(4);
		Thread.sleep(20);
		assertNull(cache.get(2));
		assertFalse(cache.containsKey(3));
		assertFalse(cache.containsValue(4));
		assertEquals(cache.size(), 0);
	}

	@Test
	public void testBackgroundExpiry() throws Exception {
		Cache<Integer, Integer> cache = new LRUCache<>(5);
		cache.setExpiryPeriod(20);
		cache.setCheckExpiryPeriod(20);
		cache.startBackgroundExpiry();
		cache.put(1, 1);
		cache.put(2, 2);
		cache.put(3, 3);
		cache.put(4, 4);
		assertEquals(cache.size(), 4);
		Thread.sleep(60);
		assertEquals(cache.size(), 0);
		cache.stopBackgroundExpiry();
	}

	@Test
	public void testSoftness() {
		Map<Integer, Integer> map = new SoftHashMap<>();
		for (int i = 1; i <= 10; i++)
			map.put(i, 10 * i);
		Cache<Integer, Integer> cache = new LRUCache<>(5, map);
		assertEquals(100, cache.get(10).intValue());
		assertEquals(100, cache.put(10, 101).intValue());
		assertEquals(101, cache.get(10).intValue());
	}
}
