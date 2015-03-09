package org.strangeforest.transaction;

public interface TransactionSynchronization {

	void beforeCompletion();
	void afterCompletion(TransactionStatus status);
}