package test.util;

import java.io.*;
import java.util.*;

import org.junit.*;
import org.strangeforest.util.*;

import static org.junit.Assert.*;

public class OrderedMapTest {

	private static OrderedMap<String, String> map;
	private static OrderedMap<String, String> original;

	@BeforeClass
	public static void setUp() throws Exception {
		map = new OrderedHashMap<>();
		map.put("1", "10");
		map.put("2", "20");
		map.put("3", "30");
		map.put("4", "40");
		map.put("5", "50");
		map.put("6", "60");
		map.put("7", "70");
		map.put("8", "80");
		original = ((OrderedHashMap<String, String>)map).clone();
	}

	@Test
	public void test() throws Exception {
		assertEquals("1", map.getFirstKey());
		assertEquals("8", map.getLastKey());
		assertEquals("10", map.getFirstValue());
		assertEquals("80", map.getLastValue());
		map.put(2, "7", "70");
		assertEquals("7", map.getKey(2));
		map.putFirst("0", "0");
		map.putFirst("0", "0");
		assertEquals("0", map.getFirstKey());
		map.putLast("9", "90");
		map.putLast("9", "90");
		assertEquals("9", map.getLastKey());
		map.put(1, "1", "10");
		assertEquals("10", map.getValue(1));
		map.moveUp("6");
		assertEquals("6", map.getKey(8));
		map.moveDown("6");
		assertEquals("6", map.getKey(7));
		map.moveUp("9");
		assertEquals("90", map.getValue(9));
		map.put(7, "7", "70");
		map.remove(9);
		map.removeFirst();
		assertEquals(map, original);
		map.sortByKeys(Collections.reverseOrder());
		map.swap("1", "8");
		map.swap("2", "7");
		map.swap("3", "6");
		map.swap("4", "5");
		assertEquals(map, original);
	}

	@Test
	public void testSerialization() throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream oOut = new ObjectOutputStream(out);
		oOut.writeObject(map);
		oOut.flush();
		oOut.close();
		out.close();

		ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
		ObjectInputStream oIn = new ObjectInputStream(in);
		OrderedMap<?, ?> sMap = (OrderedMap)oIn.readObject();
		assertEquals(map, sMap);
		assertEquals(map.toString(), sMap.toString());
	}

}
