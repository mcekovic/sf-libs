package org.strangeforest.util;

import java.io.*;

public class Range<T extends Comparable<? super T>> implements Serializable, Cloneable {

	private T from;
	private T to;

	public Range() {
		super();
	}

	public Range(T from, T to) {
		super();
		this.from = from;
		this.to = to;
		check();
	}

	public T getFrom() {
		return from;
	}

	public void setFrom(T from) {
		this.from = from;
	}

	public T getTo() {
		return to;
	}

	public void setTo(T to) {
		this.to = to;
	}

	public void check() {
		if (from != null && to != null && from.compareTo(to) > 0)
			throw new IllegalArgumentException("From greater then to.");
	}

	public boolean isFullRange() {
		return from == null && to == null;
	}

	public boolean isSingleValue() {
		return from != null && to != null && from.equals(to);
	}

	public boolean isBounded() {
		return from != null || to != null;
	}

	public boolean isBothBounded() {
		return from != null && to != null;
	}

	public boolean isLowerBounded() {
		return from != null && to == null;
	}

	public boolean isUpperBounded() {
		return from == null && to != null;
	}

	public boolean contains(T t) {
		return (from == null || from.compareTo(t) <= 0) && (to == null || to.compareTo(t) >= 0);
	}

	public boolean contains(Range<? extends T> r) {
		return (r.isBothBounded() && contains(r.from) && contains(r.to))
			 || (r.isLowerBounded() && to == null && (from == null || from.compareTo(r.from) <= 0))
			 || (r.isUpperBounded() && from == null && (to == null || to.compareTo(r.to) >= 0));
	}

	public Range<T> intersection(Range<? extends T> r) {
		T iFrom, iTo;
		if (from != null) {
			if (r.from != null)
				iFrom = from.compareTo(r.from) >= 0 ? from : r.from;
			else
				iFrom = from;
		}
		else
			iFrom = r.from;
		if (to != null) {
			if (r.to != null)
				iTo = to.compareTo(r.to) <= 0 ? to : r.to;
			else
				iTo = to;
		}
		else
			iTo = r.to;
		if (iFrom == null || iTo == null || iFrom.compareTo(iTo) <= 0)
			return new Range<>(iFrom, iTo);
		else
			return null;
	}


	// Object methods

	@Override public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj == null) return false;
		if (obj instanceof Range) {
			Range<?> range = (Range)obj;
			return ObjectUtil.equal(from, range.from) && ObjectUtil.equal(to, range.to);
		}
		else
			return false;
	}

	@Override public int hashCode() {
		int hc = 0;
		if (from != null)
			hc += from.hashCode();
		if (to != null)
			hc += to.hashCode();
		return hc;
	}

	@Override public String toString() {
		StringBuilder sb = new StringBuilder(100);
		sb.append('[');
		if (from != null)
			sb.append(from);
		sb.append(", ");
		if (to != null)
			sb.append(to);
		sb.append(']');
		return sb.toString();
	}

	@Override public Object clone() {
		try {
			return super.clone();
		}
		catch (CloneNotSupportedException ex) {
			throw new IllegalStateException(ex.getMessage());
		}
	}
}
