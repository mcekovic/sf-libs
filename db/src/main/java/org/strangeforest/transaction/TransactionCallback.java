package org.strangeforest.transaction;

public interface TransactionCallback<R> {
	
	R doInTransaction(Transaction tran) throws Throwable;
}
