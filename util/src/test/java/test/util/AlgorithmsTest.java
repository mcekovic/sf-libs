package test.util;

import java.util.*;

import org.strangeforest.util.*;
import org.junit.*;

import static java.lang.String.format;
import static org.junit.Assert.*;

public class AlgorithmsTest {
	
	private static final Function<Object, String> FUNCTION = new Function<Object, String>(){
		@Override public String apply(Object obj) {
			return format("Transformed-%s", obj);
		}
	};

	@Test
	public void transformsEveryElement() {
		List<Object> original = Arrays.<Object>asList("A", "B", "C");
			
		Iterator<String> iter = Algorithms.transform(original.iterator(), FUNCTION);
		
		assertTrue(iter.hasNext());
		assertEquals(iter.next(), "Transformed-A");
		assertEquals(iter.next(), "Transformed-B");
		assertEquals(iter.next(), "Transformed-C");
		assertFalse(iter.hasNext());
	}
	
	@Test
	public void worksWithEmptyIterator() {
		List<Object> original = Collections.emptyList();
		Iterator<String> iter = Algorithms.transform(original.iterator(), FUNCTION);
		assertFalse(iter.hasNext());
	}
}
