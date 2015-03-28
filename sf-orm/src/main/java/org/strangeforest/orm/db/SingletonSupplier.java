package org.strangeforest.orm.db;

import java.util.function.*;

public class SingletonSupplier<T> implements Supplier<T> {

	private final T instance;

	public SingletonSupplier(T instance) {
		this.instance = instance;
	}

	@Override public T get() {
		return instance;
	}
}
