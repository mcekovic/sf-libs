package org.strangeforest.pool;

public interface ResourcePoolLogger {

	void logMessage(String message);
	void logError(String message, Throwable th);
}
