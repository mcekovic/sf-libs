package test.util;

import org.junit.*;
import org.strangeforest.util.*;

import static org.junit.Assert.*;

public class RangeTest {

	private static Range<Integer> range;

	@BeforeClass
	public static void setUp() throws Exception {
		range = new Range<>(1, 5);
	}

	@Test
	public void testClone() {
		Range<Integer> range2 = (Range<Integer>)range.clone();
		assertEquals(range, range2);
	}

	@Test
	public void testContains() {
		assertEquals(false, range.contains(0));
		assertEquals(true, range.contains(1));
		assertEquals(true, range.contains(3));
		assertEquals(true, range.contains(5));
		assertEquals(false, range.contains(10));
		assertEquals(true, range.contains(new Range<>(4, 5)));
		assertEquals(false, range.contains(new Range<>(4, 6)));
		assertEquals(false, range.contains(new Range<>(-1, 0)));
		assertEquals(false, range.contains(new Range<>(3, null)));
		assertEquals(false, range.contains(new Range<>(null, 3)));
	}

	@Test
	public void testIntersection() {
		assertEquals(new Range<>(4, 5), range.intersection(new Range<>(4, 10)));
		assertEquals(new Range<>(3, 4), range.intersection(new Range<>(3, 4)));
		assertEquals(new Range<>(1, 1), range.intersection(new Range<>(-10, 1)));
		assertEquals(new Range<>(1, 5), range.intersection(new Range<>(null, null)));
		assertEquals(new Range<>(3, 5), range.intersection(new Range<>(3, null)));
		assertEquals(new Range<>(1, 5), range.intersection(new Range<>(null, 10)));
		assertEquals(null, range.intersection(new Range<>(10, null)));
	}
}
