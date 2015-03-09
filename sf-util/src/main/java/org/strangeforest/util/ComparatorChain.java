package org.strangeforest.util;

import java.util.*;

/**
 * <p>Creates a chain of <tt>Comparator</tt>s. When a <tt>Comparator</tt> returns 0
 * the next <tt>Comparator</tt> in the chain is queried.</p>
 */
public abstract class ComparatorChain {

	public static <T> Comparator<T> getComparator(List<Comparator<T>> comparators) {
		return getComparator((Comparator<T>[])comparators.toArray(new Comparator[comparators.size()]));
	}

	public static <T> Comparator<T> getComparator(Comparator<T>... comparators) {
		switch (comparators.length) {
			case 0: return NULL_COMPARATOR;
			case 1: return comparators[0];
			case 2: return new DualComparator<>(comparators[0], comparators[1]);
			default: return new ChainedComparator<>(comparators);
		}
	}

	private static final Comparator NULL_COMPARATOR = new NullComparator();

	private static final class NullComparator implements Comparator {

		@Override public int compare(Object o1, Object o2) {
			return 0;
		}

		@Override public boolean equals(Object obj) {
			return obj instanceof NullComparator;
		}

		@Override public int hashCode() {
			return 0;
		}
	}

	private static final class DualComparator<T> implements Comparator<T> {

		private Comparator<T> comp1;
		private Comparator<T> comp2;

		private DualComparator(Comparator<T> comp1, Comparator<T> comp2) {
			super();
			this.comp1 = comp1;
			this.comp2 = comp2;
		}

		@Override public int compare(T o1, T o2) {
			int r = comp1.compare(o1, o2);
			return r != 0 ? r : comp2.compare(o1, o2);
		}

		@Override public boolean equals(Object obj) {
			if (obj == this) return true;
			if (!(obj instanceof DualComparator)) return false;
			DualComparator dc = (DualComparator)obj;
			return dc.comp1.equals(comp1) && dc.comp2.equals(comp2);
		}

		@Override public int hashCode() {
			return 31*comp1.hashCode() + comp2.hashCode();
		}
	}

	private static final class ChainedComparator<T> implements Comparator<T> {

		private Comparator<T>[] comps;

		private ChainedComparator(Comparator<T>[] comparators) {
			super();
			comps = comparators;
		}

		@Override public int compare(T o1, T o2) {
			for (Comparator<T> comp : comps) {
				int r = comp.compare(o1, o2);
				if (r != 0)
					return r;
			}
			return 0;
		}

		@Override public boolean equals(Object obj) {
			if (obj == this)
				return true;
			if (!(obj instanceof ChainedComparator))
				return false;
			ChainedComparator<?> cc = (ChainedComparator)obj;
			return Arrays.equals(cc.comps, comps);
		}

		@Override public int hashCode() {
			return Arrays.hashCode(comps);
		}
	}
}
