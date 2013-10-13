package test.aspects;

import org.junit.*;
import org.strangeforest.annotation.*;

import static org.junit.Assert.*;

public class HandleExceptionTest {

	private static TestService testService;

	@BeforeClass
	public static void setUp() {
		testService = new TestService();
	}

	@Test
	public void noExceptionNoHandling() {
		testService.hello("Testko");
	}

	@Test(expected = BooomException.class)
	public void exceptionIsWrapped() {
		testService.ooops();
	}

	@Test
	public void exceptionIsNotWrappedByItself() throws BooomException {
		try {
			testService.booom();
			fail();
		}
		catch (BooomException ex) {
			assertEquals(ex.getClass(), BooomException.class);
			assertNull(ex.getCause());
		}
	}

	@Test(expected = KaBooomException.class)
	public void exceptionIsNotWrappedBySuperclass() throws BooomException {
		testService.kaBooom();
	}

	@Test
	public void exceptionIsStrictlyWrapped() {
		try {
			testService.kaBooomStrict();
			fail();
		}
		catch (Exception ex) {
			assertEquals(ex.getClass(), BooomException.class);
		}
	}


	@HandleException(wrapInto = BooomException.class)
	public static class TestService {

		public void hello(String name) {
			System.out.println("Hello " + name  + '!');
		}

		@HandleException
		public void ooops() {
			throw new NullPointerException("Ooops!!!");
		}

		@HandleException
		public void booom() throws BooomException {
			throw new BooomException("KaBooom!!!");
		}

		@HandleException
		public void kaBooom() throws KaBooomException {
			throw new KaBooomException("KaBooom!!!");
		}

		@HandleException(strictWrapping = true)
		public void kaBooomStrict() throws KaBooomException {
			throw new KaBooomException("KaBooom!!!");
		}
	}

	public static class BooomException extends Exception {

		public BooomException() {}

		public BooomException(String message) {
			super(message);
		}
	}

	public static class KaBooomException extends BooomException {

		public KaBooomException() {}

		public KaBooomException(String message) {
			super(message);
		}
	}
}
