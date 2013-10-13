package org.strangeforest.util;

import java.util.*;
import java.io.*;

public class LazySet<K, V> extends AbstractSet<V> implements LazyCollection<K, V>, Serializable, Cloneable {

	private Set<K> s;
	private BiDirFunction<K, V> ls;

	public LazySet(BiDirFunction<K, V> ls) {
		this(new HashSet<K>(), ls);
	}

	public LazySet(Set<K> s, BiDirFunction<K, V> ls) {
		super();
		this.s = s;
		this.ls = ls;
	}

	public LazySet(Collection<V> values, BiDirFunction<K, V> ls) {
		super();
		s = new HashSet<>(values.size());
		for (V value : values)
			s.add(ls.unapply(value));
		this.ls = ls;
	}

	@Override public Set<K> keys() {
		return s;
	}

	@Override public boolean add(V o) {
		return s.add(ls.unapply(o));
	}

	@Override public boolean contains(Object o) {
		return s.contains(ls.unapply((V)o));
	}

	@Override public boolean remove(Object o) {
		return s.remove(ls.unapply((V)o));
	}

	@Override public int size() {
		return s.size();
	}

	@Override public boolean isEmpty() {
		return s.isEmpty();
	}

	@Override public Iterator<V> iterator() {
		return new LazyIterator<>(s.iterator(), ls);
	}

	@Override public void clear() {
		s.clear();
	}


	// Object methods

	@Override public boolean equals(Object o) {
		if (o == this)
			 return true;
		if (o instanceof LazySet) {
			LazySet ls = (LazySet)o;
			return ls.ls.equals(ls) && ls.s.equals(s);
		}
		else
			return false;
	}

	@Override public int hashCode() {
		return s.hashCode();
	}

	@Override public Object clone() {
		return cloneSet();
	}

	public LazySet<K, V> cloneSet() {
		return new LazySet<>(cloneSet(s), ls);
	}

	static <K> Set<K> cloneSet(Set<K> s) {
		if (s instanceof LinkedHashSet)
			return new LinkedHashSet<>(s); // linked hash set does not override clone method
		else if (s instanceof HashSet)
			return (Set<K>)((HashSet<K>)s).clone();
		else if (s instanceof TreeSet)
			return (Set<K>)((TreeSet<K>)s).clone();
		else if (s instanceof SortedSet)
			return new TreeSet<>((SortedSet<K>)s);
		else
			return new HashSet<>(s);
	}
}
