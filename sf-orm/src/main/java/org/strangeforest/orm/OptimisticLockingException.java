package org.strangeforest.orm;

public class OptimisticLockingException extends RuntimeException {

	public OptimisticLockingException(Class entityClass, Object id) {
		super(String.format("Optimistic locking conflict on entity '%1$s' with id=%2$s.", entityClass.getSimpleName(), id));
	}

	public OptimisticLockingException(Class entityClass, Object id, long newVersion, long oldVersion) {
		super(String.format("Optimistic locking conflict on entity '%1$s' with id=%2$s. Attempting to save version %3$d while current version is %4$d.",
			entityClass.getSimpleName(), id, newVersion, oldVersion));
	}
}
