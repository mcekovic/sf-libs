package org.strangeforest.util;

import java.util.*;

public class IteratorEnumeration<E> implements Enumeration<E> {

	private final Iterator<E> i;

	public IteratorEnumeration(Iterator<E> i) {
		super();
		this.i = i;
	}

	@Override public boolean hasMoreElements() {
		return i.hasNext();
	}

	@Override public E nextElement() {
		return i.next();
	}
}