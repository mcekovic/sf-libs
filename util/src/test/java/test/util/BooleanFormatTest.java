package test.util;

import java.text.*;

import org.junit.*;
import org.strangeforest.util.*;

import static org.junit.Assert.*;

public class BooleanFormatTest {

	private static BooleanFormat def;
	private static BooleanFormat fmt;

	@BeforeClass
	public static void setUp() throws Exception {
		def = BooleanFormat.DEFAULT;
		fmt = new BooleanFormat("Yes|No|Unknown");
	}

	@Test
	public void testFormat() {
		assertEquals("true", def.format(true));
		assertEquals("true", def.format(Boolean.TRUE));
		assertEquals("false", def.format(false));
		assertEquals("false", def.format(Boolean.FALSE));
		assertEquals("", def.format(null));

		assertEquals("Yes", fmt.format(true));
		assertEquals("Yes", fmt.format(Boolean.TRUE));
		assertEquals("No", fmt.format(false));
		assertEquals("No", fmt.format(Boolean.FALSE));
		assertEquals("Unknown", fmt.format(null));
	}

	@Test
	public void testParse() throws ParseException {
		assertTrue(def.parse("TRUE"));
		assertEquals(Boolean.TRUE, def.parseBoolean("TRUE"));
		assertFalse(def.parse("FALSE"));
		assertEquals(Boolean.FALSE, def.parseBoolean("FALSE"));
		assertTrue(def.parseBoolean("") == null);

		assertTrue(fmt.parse("yes"));
		assertEquals(Boolean.TRUE, fmt.parseBoolean("yes"));
		assertFalse(fmt.parse("no"));
		assertEquals(Boolean.FALSE, fmt.parseBoolean("no"));
		assertTrue(fmt.parseBoolean("Unknown") == null);

		try {
			def.parse("pera");
			fail("Can't parse 'pera'.");
		}
		catch (ParseException ignored) {}
	}
}
