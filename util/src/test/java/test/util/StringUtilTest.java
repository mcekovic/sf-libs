package test.util;

import org.junit.*;
import org.strangeforest.util.*;

public class StringUtilTest {

	@Test
	public void testCompactWhiteSpace() {
		Assert.assertEquals(StringUtil.compactWhiteSpace("   pera   mika\tzika\n\njoca \t\n\r skljoca   "), "pera mika zika joca skljoca");
	}
}
