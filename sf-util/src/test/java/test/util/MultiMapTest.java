package test.util;

import org.junit.*;
import org.strangeforest.util.*;

import static org.junit.Assert.*;

public class MultiMapTest {

	private static MultiHashMap<String, String> map;

	@BeforeClass
	public static void setUp() throws Exception {
		map = new MultiHashMap<>();
		map.put("1", "2");
	}

	@Test
	public void testClone() throws Exception {
		MultiMap<String, String> map2 = map.clone();
		assertEquals(map, map2);
		assertEquals(map.toString(), map2.toString());
	}
}
