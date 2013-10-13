package org.strangeforest.pool;

/**
 * <p>Exception thrown by <tt>ResourcePool</tt> class.</p>
 */
public class PoolException extends RuntimeException {

	public PoolException(String message) {
		super(message);
	}

	public PoolException(String message, Throwable cause) {
		super(message, cause);
	}
}
