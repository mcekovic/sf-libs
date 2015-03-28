package org.strangeforest.orm;

public class NotFoundException extends RuntimeException {

	public NotFoundException(String message) {
		super(message);
	}

	public <I, E extends Entity<I>> NotFoundException(Class<E> entityClass, I id) {
		super(String.format("Cannot find '%1$s' with id=%2$s.", entityClass.getSimpleName(), id));
	}


	public <I, E extends Entity<I>> NotFoundException(Class<E> entityClass, Query query) {
		super(String.format("Cannot find '%1$s' for query '%2$s'.", entityClass.getSimpleName(), query));
	}
}
