package test.util;

import java.io.*;
import java.rmi.*;

import org.junit.*;
import org.strangeforest.util.*;

import static org.junit.Assert.*;

public class ExceptionUtilTest {

	@Test(expected = RemoteException.class)
	public void testThrowChecked() {
		throw ExceptionUtil.throwIt(new RemoteException("Booom!"));
	}

	@Test
	public void testThrowCheckedStackTrace() {
		try {
			sneakyThrowChecked();
			fail();
		}
		catch (Exception ex) {
			StringWriter buff = new StringWriter();
			ex.printStackTrace(new PrintWriter(buff));
			String stackTrace = buff.getBuffer().toString();
			assertTrue(stackTrace.contains("test.util.ExceptionUtilTest.throwRemote"));
			assertTrue(stackTrace.contains("test.util.ExceptionUtilTest.sneakyThrowChecked"));
			assertTrue(stackTrace.contains("test.util.ExceptionUtilTest.testThrowCheckedStackTrace"));
			assertFalse(stackTrace.contains("org.strangeforest.util"));
		}
	}

	private void sneakyThrowChecked() {
		try {
			throwRemote();
		}
		catch (RemoteException ex) {
			throw ExceptionUtil.throwIt(ex);
		}
	}

	private void throwRemote() throws RemoteException {
		throw new RemoteException("Booom!");
	}
}
