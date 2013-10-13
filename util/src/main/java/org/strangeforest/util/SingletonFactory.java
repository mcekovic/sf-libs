package org.strangeforest.util;

public final class SingletonFactory<T> implements Supplier<T> {

	private final T singleton;

	public SingletonFactory(T singleton) {
		super();
		this.singleton = singleton;
	}

	@Override public T create() {
		return singleton;
	}
}
