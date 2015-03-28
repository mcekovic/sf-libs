package org.strangeforest.orm;

import java.util.*;

public abstract class EqualsByValueUtil {

	public static <T extends EquatableByValue<T>> boolean equalByValue(T o1, T o2) {
		return o1 == o2 || (o1 != null && o1.equalsByValue(o2));
	}

	public static <T extends EquatableByValue<T>> boolean equalByValue(Collection<T> coll1, Collection<T> coll2) {
		if (coll1 == coll2)
		    return true;
		if (coll1 == null || coll2 == null)
			return false;
		if (coll1.size() != coll2.size())
			return false;
		Iterator<T> iter1 = coll1.iterator(), iter2 = coll2.iterator();
		while (iter1.hasNext() && iter2.hasNext()) {
			if (!equalByValue(iter1.next(), iter2.next()))
				return false;
		}
		return !(iter1.hasNext() || iter2.hasNext());
	}
	
	public static <T extends EquatableByValue<T>> boolean containsByValue(Collection<T> coll, T item) {
		if (coll == null || coll.isEmpty())
		    return false;
		for (T anItem : coll) {
			if (equalByValue(anItem, item))
				return true;
		}
		return false;
	}

	public static boolean reflectionEqualsByValue(Object lhs, Object rhs, String... excludeFields) {
		return reflectionEqualsByValue(lhs, rhs, false, null, excludeFields);
	}

	public static boolean reflectionEqualsByValue(Object lhs, Object rhs, boolean testTransients, Class<?> reflectUpToClass, String... excludeFields) {
		if (lhs == rhs)
			return true;
		if (lhs == null || rhs == null)
			return false;
		Class<?> lhsClass = lhs.getClass();
		Class<?> rhsClass = rhs.getClass();
		Class<?> testClass;
		if (lhsClass.isInstance(rhs)) {
			testClass = lhsClass;
			if (!rhsClass.isInstance(lhs))
				testClass = rhsClass;
		}
		else if (rhsClass.isInstance(lhs)) {
			testClass = rhsClass;
			if (!lhsClass.isInstance(rhs))
				testClass = lhsClass;
		}
		else
			return false;
		try {
			EqualsByValueChecker checker = new EqualsByValueChecker();
			do {
				if (!checker.reflectionEqual(lhs, rhs, testClass, testTransients, excludeFields))
					return false;
				testClass = testClass.getSuperclass();
			}
			while (testClass.getSuperclass() != null && testClass != reflectUpToClass);
			return true;
		}
		catch (IllegalArgumentException ex) {
			return false;
		}
	}
}
