package org.strangeforest.transaction;

public class Transaction {

	private TransactionalResource resource;
	private boolean rollbackOnly;
	private int beginCount;
	private boolean completed;

	Transaction() {
		super();
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
			if (!rollbackOnly) {
				if (resource != null)
					resource.commit();
				beginCount--;
				TransactionManager.removeTransaction();
				completed = true;
			}
			else
				rollback();
		}
		else if (beginCount > 1)
			beginCount--;
	}

	public void rollback() {
		if (beginCount == 1) {
			beginCount--;
			TransactionManager.removeTransaction();
			completed = true;
			if (resource != null)
				resource.rollback();
		}
		else if (beginCount > 1)
			beginCount--;
	}

	public void suspend() {
		TransactionManager.removeTransaction();
	}

	public void resume() {
		TransactionManager.setTransaction(this);
	}

	public boolean isRollbackOnly() {
		return rollbackOnly;
	}

	public void setRollbackOnly() {
		rollbackOnly = true;
	}

	public boolean isCompleted() {
		return completed;
	}
}
