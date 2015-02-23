package org.strangeforest.transaction;

public abstract class TransactionSynchronizationAdapter implements TransactionSynchronization {

	@Override public void beforeCompletion() {}
	@Override public void afterCompletion(TransactionStatus status) {}
}
