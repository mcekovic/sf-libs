package org.strangeforest.transaction;

import java.text.*;

public class TransactionException extends RuntimeException {

	public TransactionException(String message) {
		super(message);
	}

	public TransactionException(Throwable cause) {
		super(cause);
	}

	public TransactionException(String message, Object... params) {
		super(MessageFormat.format(message, params));
	}

	public TransactionException(String message, Throwable cause) {
		super(message, cause);
	}
}
