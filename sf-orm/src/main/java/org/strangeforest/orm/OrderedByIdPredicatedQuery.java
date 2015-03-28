package org.strangeforest.orm;

import java.util.*;

public abstract class OrderedByIdPredicatedQuery<I, E extends DomainEntity<I, E>> extends PredicatedQuery<I, E> {

	public OrderedByIdPredicatedQuery(String name) {
		super(name);
	}

	public OrderedByIdPredicatedQuery(String name, Object... params) {
		super(name, params);
	}

	@Override protected Comparator<I> idComparator() {
		return ID_COMPARATOR;
	}

	private static final Comparator ID_COMPARATOR = new Comparator() {
		@Override public int compare(Object o1, Object o2) {
			return ((Comparable)o1).compareTo(o2);
		}
	};
}
