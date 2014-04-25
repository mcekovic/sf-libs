package org.strangeforest.transaction;

@FunctionalInterface
public interface TransactionCallback<R> {
	
	R doInTransaction(Transaction tran) throws Throwable;
}
