package org.strangeforest.util;

import java.io.*;
import java.sql.*;
import java.util.concurrent.*;

public abstract class ExceptionUtil {

	// Root causes

	public static Throwable getRootCause(Throwable th) {
		Throwable root = th;
		for (Throwable cause = th; cause != null; cause = cause.getCause())
			root = cause;
		return root;
	}

	public static <E extends Throwable> E getRootCause(Throwable th, Class<E> cls) {
		E ex = null;
		for (Throwable cause = th; cause != null; cause = cause.getCause()) {
			if (cls.isInstance(cause))
				ex = (E)cause;
		}
		return ex;
	}

	public static String getRootMessage(Throwable th) {
		String message = null;
		for (Throwable cause = th; cause != null; cause = cause.getCause()) {
			String msg = cause.getMessage();
			if (msg != null)
				message = msg;
		}
		return message;
	}

	public static int getRootSQLErrorCode(Throwable th) {
		SQLException cause = ExceptionUtil.getRootCause(th, SQLException.class);
		return cause != null ? cause.getErrorCode() : 0;
	}


	// Message util

	public static String getConcatenatedMessage(Throwable th) {
		StringBuilder sb = new StringBuilder();
		do {
			String msg = th.getMessage();
			if (msg != null) {
				if (sb.length() > 0)
					sb.append("\r\n");
				sb.append(msg);
			}
			th = th.getCause();
		}
		while (th != null);
		return sb.toString();
	}

	public static String getStackTrace(Throwable th) {
		Writer writer = new CharArrayWriter(256);
		th.printStackTrace(new PrintWriter(writer));
		return writer.toString();
	}


	// Wrapping

	public static <T extends Throwable> T wrap(Class<T> into, Throwable th) {
	   if (into.isInstance(th))
	      return (T)th;
		else {
		   try {
			   return createThrowable(into, th);
		   }
		   catch (Exception ex) {
			   throw new IllegalStateException(ex);
		   }
	   }
	}

	public static <T extends Throwable> T strictlyWrap(Class<T> into, Throwable th) {
	   if (th.getClass().equals(into))
	      return (T)th;
		else {
		   try {
			   return createThrowable(into, th);
		   }
		   catch (Exception ex) {
			   throw new IllegalStateException(ex);
		   }
	   }
	}

	public static <R, T extends Throwable> R executeAndWrapException(Callable<R> call, Class<T> into) throws T {
		try {
			return call.call();
		}
		catch (Throwable ex) {
			throw wrap(into, ex);
		}
	}

	private static <T extends Throwable> T createThrowable(Class<T> exClass, Throwable cause) throws InstantiationException, IllegalAccessException {
		T ex = exClass.newInstance();
		ex.initCause(cause);
		return ex;
	}


	// Sneaky throwing checked exception

	private static class Thrower<T extends Throwable> {
		private T throwIt(Throwable exception) throws T {
			throw (T)exception;
		}
	}

	private static final Thrower<RuntimeException> THROWER = new Thrower<>();

	/**
	 * Throws checked exceptions without declaration
	 * @param exception to throw
	 * @return dead code, never returns, used for more readable caller code
	 */
	public static RuntimeException throwIt(Throwable exception) {
		return THROWER.throwIt(exception);
	}
}
