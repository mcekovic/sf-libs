package org.strangeforest.util;

public abstract class ObjectUtil {

	public static <T extends Comparable<? super T>> int compare(T o1, T o2) {
		return o1 == o2 ? 0 : (o1 == null ? -1 : (o2 == null ? 1 : o1.compareTo(o2)));
	}

	public static <T extends Comparable<? super T>> T max(T o1, T o2) {
		return o1 == o2 || o2 == null ? o1 : (o1 != null && o1.compareTo(o2) >= 0 ? o1 : o2);
	}

	public static <T extends Comparable<? super T>> T min(T o1, T o2) {
		return o1 == o2 || o2 == null ? o1 : (o1 != null && o1.compareTo(o2) <= 0 ? o1 : o2);
	}

	public static boolean isCastableTo(Object o, Class<?> c) {
		return o == null || c.isInstance(o);
	}
}
