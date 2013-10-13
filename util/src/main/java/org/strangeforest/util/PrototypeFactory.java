package org.strangeforest.util;

public final class PrototypeFactory<T> implements Supplier<T> {

	private final Class<T> cls;

	public PrototypeFactory(Class<T> cls) {
		super();
		this.cls = cls;
	}

	@Override public T create() {
		try {
			return cls.newInstance();
		}
		catch (Exception ex) {
			throw ExceptionUtil.throwIt(ex);
		}
	}
}
