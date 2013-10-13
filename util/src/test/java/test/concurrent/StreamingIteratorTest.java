package test.concurrent;

import java.text.*;
import java.util.*;

import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;
import org.strangeforest.concurrent.*;
import org.strangeforest.util.*;

import static org.junit.Assert.*;
import static org.junit.runners.Parameterized.*;

@RunWith(Parameterized.class)
public class StreamingIteratorTest {

	private int bufferSize;
	private boolean awaitStart;


	private static final long TICK = 20L;
	private static final long MARGIN = 10L;
	private static final String BOOOM = "Booom!!!";

	public StreamingIteratorTest(int bufferSize, boolean awaitStart) {
		this.bufferSize = bufferSize;
		this.awaitStart = awaitStart;
	}

	@Parameters
	public static Collection<Object[]> createBufferSizes() {
		return Arrays.asList(new Object[][] {
			{0, false},
			{10, false},
			{0, true},
			{10, true},
		});
	}

	@Test
	public void testStreamingIterator() throws InterruptedException {
		StopWatch watch = new StopWatch();

		final StreamingIterator<Integer> iter = new StreamingIterator<>(bufferSize);
		iter.start(new Runnable() {
			public void run() {
				try {
					Thread.sleep(TICK);
					iter.put(1);
					Thread.sleep(TICK);
					iter.put(2);
					Thread.sleep(TICK);
					iter.put(3);
				}
				catch (InterruptedException ignored) {}
			}
		});

		if (awaitStart) {
			iter.awaitStart();
			assertPassed(watch, TICK);
		}
		assertTrue(iter.hasNext());

		List<Integer> list = Collections.list(new IteratorEnumeration<>(iter));
		assertFalse(iter.hasNext());
		assertEquals(list.size(), 3);
		assertPassed(watch, 3*TICK);
	}

	@Test
	public void testEmptyIterator() throws InterruptedException {
		final StreamingIterator<Integer> iter = new StreamingIterator<>(bufferSize);
		iter.start(new Runnable() {
			public void run() {}
		});
		if (awaitStart)
			iter.awaitStart();

		assertFalse(iter.hasNext());
	}

	@Test
	public void testErrorInStreamingStart() throws InterruptedException {
		final StreamingIterator<Integer> iter = new StreamingIterator<>(bufferSize);
		try {
			iter.start(new Runnable() {
				public void run() {
					throw new TestException(BOOOM);
				}
			});
			if (awaitStart) {
				iter.awaitStart();
				fail("Unreachable reached!");
			}
			else {
				assertTrue(iter.hasNext());
				iter.next();
				fail("Unreachable reached!");
			}
		}
		catch (TestException ex) {
			assertEquals(ex.getMessage(), BOOOM);
			assertFalse(iter.hasNext());
		}
	}

	@Test
	public void testErrorInStreaming() throws InterruptedException {
		StopWatch watch = new StopWatch();

		final StreamingIterator<Integer> iter = new StreamingIterator<>(bufferSize);
		iter.start(new Runnable() {
			public void run() {
				try {
					Thread.sleep(TICK);
					iter.put(1);
					Thread.sleep(TICK);
					iter.put(2);
				}
				catch (InterruptedException ignored) {}
				throw new TestException(BOOOM);
			}
		});

		if (awaitStart) {
			iter.awaitStart();
			assertPassed(watch, TICK);
		}
		assertTrue(iter.hasNext());

		try {
			Collections.list(new IteratorEnumeration<>(iter));
			fail("Unreachable reached!");
		}
		catch (TestException ex) {
			assertEquals(ex.getMessage(), BOOOM);
			assertFalse(iter.hasNext());
			assertPassed(watch, 2*TICK);
		}
	}

	private void assertPassed(StopWatch watch, long millis) {
		long passedMillis = watch.time().getMillis();
		assertTrue(MessageFormat.format("Passed {0}, expected at least {1}", passedMillis, millis), passedMillis >= millis - MARGIN);
	}

	private static class TestException extends RuntimeException {

		private TestException(String message) {
			super(message);
		}
	}
}
