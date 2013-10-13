package org.strangeforest.util;

import java.util.*;

/**
 * <p>Generic <tt>Comparator</tt> for JavaBeans.</p>
 */
public class BeanComparator<T> implements Comparator<T> {

	private String name;
	private int dir;
	private boolean deep;

	/**
	 * Creates <tt>BeanComparator</tt> for comparing JavaBeans by specified property using ascending order.
	 * @param name property name.
	 */
	public BeanComparator(String name) {
		this(name, false, false);
	}

	/**
	 * Creates <tt>BeanComparator</tt> for comparing JavaBeans by specified property.
	 * @param name property name.
	 * @param desc is descending order used.
	 */
	public BeanComparator(String name, boolean desc) {
		this(name, desc, false);
	}

	/**
	 * Creates <tt>BeanComparator</tt> for comparing JavaBeans by specified property.
	 * @param name property name.
	 * @param desc is descending order used.
	 * @param deep if expression like syntax is allowed (using '.' operator for accessing referenced been properties).
	 */
	public BeanComparator(String name, boolean desc, boolean deep) {
		super();
		this.name = name;
		dir = desc ? -1 : 1;
		this.deep = deep;
	}

	public String getPropertyName() {
		return name;
	}

	public boolean isDescendingOrder() {
		return dir == 1;
	}

	public boolean isDeep() {
		return deep;
	}

	@Override public int compare(T o1, T o2) {
		Object v1;
		Object v2;
		if (deep) {
			v1 = BeanUtil.evalExpression(o1, name);
			v2 = BeanUtil.evalExpression(o2, name);
		}
		else {
			v1 = BeanUtil.getProperty(o1, name);
			v2 = BeanUtil.getProperty(o2, name);
		}
		if (ObjectUtil.isCastableBy(v1, Comparable.class) && ObjectUtil.isCastableBy(v2, Comparable.class))
			return dir*ObjectUtil.compare((Comparable)v1, (Comparable)v2);
		else
			throw new IllegalArgumentException(o1.getClass().getName() + " is not comparable by " + name + ".");
	}

	@Override public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (!(obj instanceof BeanComparator))
			return false;
		BeanComparator<?> bc = (BeanComparator)obj;
		return bc.name.equals(name) && bc.dir == dir && bc.deep == deep;
	}

	@Override public int hashCode() {
		return 31*(31*name.hashCode() + dir) + (deep ? 1 : 0);
	}
}
