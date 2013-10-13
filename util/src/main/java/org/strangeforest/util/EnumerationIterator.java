package org.strangeforest.util;

import java.util.*;

/**
 * <p>This is <tt>Iterator</tt> implementation based on <tt>Enumeration</tt> instance.</p>
 * </p>It is also <i>Adapter</i> from <tt>Enumeration</tt> to <tt>Iterator</tt> interface.</p>
 */
public class EnumerationIterator<E> implements Iterator<E> {

	private final Enumeration<E> e;

	public EnumerationIterator(Enumeration<E> e) {
		super();
		this.e = e;
	}

	@Override public boolean hasNext() {
		return e.hasMoreElements();
	}

	@Override public E next() {
		return e.nextElement();
	}

	@Override public void remove() {
		throw new UnsupportedOperationException();
	}
}
