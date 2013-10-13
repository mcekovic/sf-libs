package org.strangeforest.util;

import java.util.*;

public abstract class ObjectUtil {

	public static boolean equal(Object o1, Object o2) {
		return o1 == o2 || (o1 != null && o1.equals(o2));
	}

	public static boolean notEqual(Object o1, Object o2) {
		return o1 != o2 && (o1 == null || !o1.equals(o2));
	}
	
	public static int hashCode(Object o) {
		return o != null ? o.hashCode() : 0;
	}

	public static <T extends Comparable<? super T>> int compare(T o1, T o2) {
		return o1 == o2 ? 0 : (o1 == null ? -1 : (o2 == null ? 1 : o1.compareTo(o2)));
	}

	public static <T> int compare(T o1, T o2, Comparator<T> c) {
		return o1 == o2 ? 0 : (o1 == null ? -1 : (o2 == null ? 1 : c.compare(o1, o2)));
	}

	public static <T extends Comparable<? super T>> T max(T o1, T o2) {
		return o1 == o2 || o2 == null ? o1 : (o1 != null && o1.compareTo(o2) >= 0 ? o1 : o2);
	}

	public static <T extends Comparable<? super T>> T min(T o1, T o2) {
		return o1 == o2 || o2 == null ? o1 : (o1 != null && o1.compareTo(o2) <= 0 ? o1 : o2);
	}

	public static boolean isCastableBy(Object o, Class<?> c) {
		return o == null || c.isInstance(o);
	}
}
