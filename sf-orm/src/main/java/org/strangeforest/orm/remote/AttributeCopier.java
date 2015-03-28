package org.strangeforest.orm.remote;

import java.util.*;

public abstract class AttributeCopier<T> {

	public abstract void copyAttributes(T from, T to);

	protected static <D> void copyAttributes(AttributeCopier<D> detailCopier, Iterable<D> from, Iterable<D> to) {
		for (Iterator<D> fromIter = from.iterator(), toIter = to.iterator(); fromIter.hasNext() && toIter.hasNext(); )
			detailCopier.copyAttributes(fromIter.next(), toIter.next());
	}
}
