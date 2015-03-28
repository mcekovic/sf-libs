package org.strangeforest.orm;

import java.util.*;
import java.util.function.*;

public abstract class PredicatedQuery<I, E extends DomainEntity<I, E>> extends Query implements Predicate<E> {

	private boolean sorted;

	public PredicatedQuery(String name) {
		super(name);
	}

	public PredicatedQuery(String name, Object... params) {
		super(name, params);
	}

	public boolean isSorted() {
		return sorted;
	}

	public PredicatedQuery<I, E> sorted() {
		sorted = true;
		return this;
	}

	protected Comparator<I> idComparator() {
		return null;
	}

	protected Comparator<E> comparator() {
		return null;
	}

	public Comparator<I> getIdComparator(final Repository<I, E> manager) {
		Comparator<I> idComparator = idComparator();
		if (idComparator != null)
			return idComparator;
		else {
			Comparator<E> comparator = comparator();
			return comparator != null ? (id1, id2) -> comparator.compare(manager.get(id1), manager.get(id2)) : null;
		}
	}

	public Comparator<E> getComparator() {
		Comparator idComparator = idComparator();
		return idComparator != null ? (e1, e2) -> idComparator.compare(e1.getId(), e2.getId()) : comparator();
	}
}
