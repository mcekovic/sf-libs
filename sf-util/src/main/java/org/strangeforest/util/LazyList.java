package org.strangeforest.util;

import java.util.*;
import java.io.*;

public class LazyList<K, V> extends AbstractList<V> implements LazyCollection<K, V>, Serializable, Cloneable {

	private List<K> l;
	private BiDirFunction<K, V> f;

	public LazyList(BiDirFunction<K, V> f) {
		this(new ArrayList<K>(), f);
	}

	public LazyList(List<K> l, BiDirFunction<K, V> f) {
		super();
		this.l = l;
		this.f = f;
	}

	public LazyList(Collection<V> values, BiDirFunction<K, V> f) {
		super();
		l = new ArrayList<>(values.size());
		for (V value : values)
			l.add(f.unapply(value));
		this.f = f;
	}

	@Override public List<K> keys() {
		return l;
	}

	@Override public V get(int index) {
		return f.apply(l.get(index)) ;
	}

	@Override public boolean add(V o) {
		return l.add(f.unapply(o));
	}

	@Override public void add(int index, V o) {
		l.add(index, f.unapply(o));
	}

	@Override public V remove(int index) {
		K key = l.remove(index);
		return key != null ? f.apply(key) : null;
	}

	@Override public boolean remove(Object o) {
		return l.remove(f.unapply((V)o));
	}

	@Override public V set(int index, V o) {
		K key = l.set(index, f.unapply(o));
		return key != null ? f.apply(key) : null;
	}

	@Override public boolean contains(Object o) {
		return l.contains(f.unapply((V)o));
	}

	@Override public int indexOf(Object o) {
		return l.indexOf(f.unapply((V)o));
	}

	@Override public int lastIndexOf(Object o) {
		return l.lastIndexOf(f.unapply((V)o));
	}

	@Override public int size() {
		return l.size();
	}

	@Override public boolean isEmpty() {
		return l.isEmpty();
	}

	@Override public List<V> subList(int fromIndex, int toIndex) {
		return new LazyList<>(l.subList(fromIndex, toIndex), f);
	}

	@Override public void clear() {
		l.clear();
	}


	// Object methods

	@Override public boolean equals(Object o) {
		if (o == this)
			 return true;
		if (o instanceof LazyList) {
			LazyList ll = (LazyList)o;
			return ll.f.equals(f) && ll.l.equals(l);
		}
		else
			return false;
	}

	@Override public int hashCode() {
		return l.hashCode();
	}

	@Override public Object clone() {
		return cloneList();
	}

	public LazyList<K, V> cloneList() {
		return new LazyList<>(cloneList(l), f);
	}

	private static <K> List<K> cloneList(List<K> l) {
		if (l instanceof ArrayList)
			return (List<K>)((ArrayList<K>)l).clone();
		else if (l instanceof LinkedList)
			return (List<K>)((LinkedList<K>)l).clone();
		else if (l instanceof RandomAccess)
			return new ArrayList<>(l);
		else
			return new LinkedList<>(l);
	}
}
