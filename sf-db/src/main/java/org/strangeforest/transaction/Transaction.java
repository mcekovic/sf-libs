package org.strangeforest.transaction;

import java.util.*;

import org.strangeforest.util.*;

import static org.strangeforest.transaction.TransactionStatus.*;

public class Transaction {

	private TransactionalResource resource;
	private boolean rollbackOnly;
	private TransactionStatus status;
	private int beginCount;
	private boolean completing, completed;
	private List<TransactionSynchronization> synchronizations;

	Transaction() {
		super();
		status = ACTIVE;
	}

	void begin() {
		beginCount++;
	}

	public TransactionalResource getResource() {
		return resource;
	}

	public void enlistResource(TransactionalResource resource) {
		if (this.resource != null && !resource.isSameResource(this.resource))
			throw new IllegalStateException("Distributed transactions are not supported.");
		this.resource = resource;
	}

	public void commit() {
		if (beginCount == 1) {
			if (!rollbackOnly)
				doCommit();
			else
				doRollback();
		}
		else if (beginCount > 1)
			beginCount--;
	}

	public void rollback() {
		if (beginCount == 1)
			doRollback();
		else if (beginCount > 1)
			beginCount--;
	}

	private void doCommit() {
		notifyBeforeCompletion();
		try {
			if (resource != null)
				resource.commit();
			status = COMMITED;
			beginCount--;
			TransactionManager.removeTransaction();
		}
		catch (Throwable th) {
			status = UNKNOWN;
			throw ExceptionUtil.throwIt(th);
		}
		finally {
			notifyAfterCompletion(status);
		}
	}

	private void doRollback() {
		notifyBeforeCompletion();
		beginCount--;
		TransactionManager.removeTransaction();
		try {
			if (resource != null)
				resource.rollback();
			status = ROLLED_BACK;
		}
		catch (Throwable th) {
			status = UNKNOWN;
			throw ExceptionUtil.throwIt(th);
		}
		finally {
			notifyAfterCompletion(status);
		}
	}

	public void suspend() {
		TransactionManager.removeTransaction();
		status = SUSPENDED;
	}

	public void resume() {
		status = ACTIVE;
		TransactionManager.setTransaction(this);
	}

	public boolean isRollbackOnly() {
		return rollbackOnly;
	}

	public void setRollbackOnly() {
		rollbackOnly = true;
	}

	public TransactionStatus getStatus() {
		return status;
	}

	public boolean isCompleting() {
		return completing;
	}

	public boolean isCompleted() {
		return completed;
	}


	// Synchronization

	public void registerSynchronization(TransactionSynchronization synchronization) {
		if (synchronizations == null)
			synchronizations = new ArrayList<TransactionSynchronization>(4);
		synchronizations.add(synchronization);
	}

	private void notifyBeforeCompletion() {
		if (synchronizations != null && !completing && !completed) {
			for (TransactionSynchronization synchronization : synchronizations)
				synchronization.beforeCompletion();
		}
		completing = true;
	}

	private void notifyAfterCompletion(TransactionStatus status) {
		if (!completed) {
			completing = false;
			completed = true;
			if (synchronizations != null) {
				for (TransactionSynchronization synchronization : synchronizations)
					synchronization.afterCompletion(status);
			}
		}
	}
}
