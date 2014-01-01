package org.strangeforest.util;

import java.util.*;

public final class Reference<T> {

	private volatile T obj;

	public Reference() {
		super();
	}

	public Reference(T obj) {
		super();
		this.obj = obj;
	}

	public T get() {
		return obj;
	}

	public void set(T obj) {
		this.obj = obj;
	}

	public boolean isNull() {
		return obj == null;
	}

	
	// Object methods

	@Override public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof Reference))
			return false;
		Reference reference = (Reference)o;
		return Objects.equals(obj, reference.obj);
	}

	@Override public int hashCode() {
		return obj != null ? obj.hashCode() : 0;
	}

	@Override public String toString() {
		return String.valueOf(obj);
	}
}
