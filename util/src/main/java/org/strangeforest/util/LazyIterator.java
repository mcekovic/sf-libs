package org.strangeforest.util;

import java.util.*;

public class LazyIterator<K, V> implements Iterator<V> {

	private Iterator<K> i;
	private Function<K, V> lf;

	public LazyIterator(Iterator<K> i, Function<K, V> lf) {
		super();
		this.i = i;
		this.lf = lf;
	}

	public Iterator<K> keyIterator() {
		return i;
	}

	@Override public boolean hasNext() {
		return i.hasNext();
	}

	@Override public V next() {
		return lf.apply(i.next());
	}

	@Override public void remove() {
		i.remove();
	}
}
