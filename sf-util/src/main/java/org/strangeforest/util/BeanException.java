package org.strangeforest.util;

import java.text.*;

public class BeanException extends RuntimeException {

	public BeanException(String message) {
		super(message);
	}

	public BeanException(Throwable cause) {
		super(cause);
	}

	public BeanException(String message, Throwable cause) {
		super(message, cause);
	}

	public BeanException(String message, Throwable cause, Object... arguments) {
		super(MessageFormat.format(message, arguments), cause);
	}
}
