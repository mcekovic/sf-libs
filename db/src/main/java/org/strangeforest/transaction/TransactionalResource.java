package org.strangeforest.transaction;

public interface TransactionalResource {

	void commit() throws TransactionException;
	void rollback() throws TransactionException;
	boolean isSameResource(TransactionalResource resource);
}
