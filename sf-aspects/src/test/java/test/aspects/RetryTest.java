package test.aspects;

import org.junit.*;
import org.strangeforest.annotation.*;

public class RetryTest {

	@Test(expected = RuntimeException.class)
	public void testNoRetry() {
		new Worker().noRetry();
	}

	@Test(expected = RuntimeException.class)
	public void testRetried1() {
		new Worker().retry1();
	}

	@Test
	public void testRetried2() {
		new Worker().retry2();
	}

	@Test
	public void testRetried2ExpectedException() {
		new Worker().retry2ExpectedEx();
	}

	@Test(expected = RuntimeException.class)
	public void testRetried2OtherException() {
		new Worker().retry2OtherEx();
	}

	public static class Worker {

		private int tries;

		private void noRetry() {
			doSomething();
		}

		@Retried
		private void retry1() {
			doSomething();
		}

		@Retried(count = 2)
		private void retry2() {
			doSomething();
		}

		@Retried(count = 2, exceptions = RuntimeException.class)
		private void retry2ExpectedEx() {
			doSomething();
		}

		@Retried(count = 2, exceptions = IllegalArgumentException.class)
		private void retry2OtherEx() {
			doSomething();
		}

		private void doSomething() {
			if (++tries <= 2)
				throw new RuntimeException("Booom!");
			System.out.println("OK");
		}
	}
}
