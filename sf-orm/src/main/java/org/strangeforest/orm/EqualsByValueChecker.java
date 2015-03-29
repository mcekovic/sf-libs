package org.strangeforest.orm;

import java.lang.reflect.*;
import java.util.*;

import org.apache.commons.lang3.tuple.*;

import org.strangeforest.util.*;

class EqualsByValueChecker {

	private boolean isEquals = true;

	public boolean reflectionEqual(Object lhs, Object rhs, Class<?> cls, boolean useTransients, String[] excludeFields) {
		if (isRegistered(lhs, rhs))
			return isEquals;
		try {
			register(lhs, rhs);
			Field[] fields = cls.getDeclaredFields();
			AccessibleObject.setAccessible(fields, true);
			for (int i = 0; i < fields.length && isEquals; i++) {
				Field f = fields[i];
				if (!ArrayUtil.contains(excludeFields, f.getName())
					&& (f.getName().indexOf('$') == -1)
					&& (useTransients || !Modifier.isTransient(f.getModifiers()))
					&& (!Modifier.isStatic(f.getModifiers()))) {
						check(f.get(lhs), f.get(rhs));
				}
			}
		}
		catch (IllegalAccessException ex) {
			throw new InternalError("Unexpected IllegalAccessException");
		}
		finally {
			unregister(lhs, rhs);
		}
		return isEquals;
	}

	public boolean isEquals() {
		return isEquals;
	}

	private void check(Object lhs, Object rhs) {
		if (!isEquals || lhs == rhs)
			return;
		if (lhs != null && rhs != null) {
			Class<?> lhsClass = lhs.getClass();
			Class<?> rhsClass = rhs.getClass();
			if (lhsClass != rhsClass)
				isEquals = false;
			else if (!lhsClass.isArray()) {
				if (lhs instanceof EquatableByValue)
					isEquals = ((EquatableByValue)lhs).equalsByValue((EquatableByValue)rhs);
				else
					isEquals = lhs.equals(rhs);
			}
			else if (lhs instanceof long[])
				check((long[])lhs, (long[])rhs);
			else if (lhs instanceof int[])
				check((int[])lhs, (int[])rhs);
			else if (lhs instanceof short[])
				check((short[])lhs, (short[])rhs);
			else if (lhs instanceof char[])
				check((char[])lhs, (char[])rhs);
			else if (lhs instanceof byte[])
				check((byte[])lhs, (byte[])rhs);
			else if (lhs instanceof double[])
				check((double[])lhs, (double[])rhs);
			else if (lhs instanceof float[])
				check((float[])lhs, (float[])rhs);
			else if (lhs instanceof boolean[])
				check((boolean[])lhs, (boolean[])rhs);
			else
				check((Object[]) lhs, (Object[]) rhs);
		}
		else
			isEquals = false;
	}

	private void check(long lhs, long rhs) {
		if (!isEquals)
			isEquals = (lhs == rhs);
	}

	private void check(int lhs, int rhs) {
		if (isEquals)
			isEquals = lhs == rhs;
	}

	private void check(short lhs, short rhs) {
		if (isEquals)
			isEquals = lhs == rhs;
	}

	private void check(char lhs, char rhs) {
		if (isEquals)
			isEquals = lhs == rhs;
	}

	private void check(byte lhs, byte rhs) {
		if (isEquals)
			isEquals = lhs == rhs;
	}

	private void check(double lhs, double rhs) {
		if (isEquals)
			isEquals = Double.doubleToLongBits(lhs) == Double.doubleToLongBits(rhs);
	}

	private void check(float lhs, float rhs) {
		if (isEquals)
			isEquals = Float.floatToIntBits(lhs) == Float.floatToIntBits(rhs);
	}

	private void check(boolean lhs, boolean rhs) {
		if (isEquals)
			isEquals = lhs == rhs;
	}

	private void check(Object[] lhs, Object[] rhs) {
		if (!isEquals || lhs == rhs)
			return;
		if (lhs != null && rhs != null && lhs.length == rhs.length) {
			for (int i = 0; i < lhs.length && isEquals; i++)
				check(lhs[i], rhs[i]);
		}
		else
			isEquals = false;
	}

	private void check(long[] lhs, long[] rhs) {
		if (!isEquals || lhs == rhs)
			return;
		if (lhs != null && rhs != null && lhs.length == rhs.length) {
			for (int i = 0; i < lhs.length && isEquals; i++)
				check(lhs[i], rhs[i]);
		}
		else
			isEquals = false;
	}

	private void check(int[] lhs, int[] rhs) {
		if (!isEquals || lhs == rhs)
			return;
		if (lhs != null && rhs != null && lhs.length == rhs.length) {
			for (int i = 0; i < lhs.length && isEquals; i++)
				check(lhs[i], rhs[i]);
		}
		else
			isEquals = false;
	}

	private void check(short[] lhs, short[] rhs) {
		if (!isEquals || lhs == rhs)
			return;
		if (lhs != null && rhs != null && lhs.length == rhs.length) {
			for (int i = 0; i < lhs.length && isEquals; i++)
				check(lhs[i], rhs[i]);
		}
		else
			isEquals = false;
	}

	private void check(char[] lhs, char[] rhs) {
		if (!isEquals || lhs == rhs)
			return;
		if (lhs != null && rhs != null && lhs.length == rhs.length) {
			for (int i = 0; i < lhs.length && isEquals; i++)
				check(lhs[i], rhs[i]);
		}
		else
			isEquals = false;
	}

	private void check(byte[] lhs, byte[] rhs) {
		if (!isEquals || lhs == rhs)
			return;
		if (lhs != null && rhs != null && lhs.length == rhs.length) {
			for (int i = 0; i < lhs.length && isEquals; i++)
				check(lhs[i], rhs[i]);
		}
		else
			isEquals = false;
	}

	private void check(double[] lhs, double[] rhs) {
		if (!isEquals || lhs == rhs)
			return;
		if (lhs != null && rhs != null && lhs.length == rhs.length) {
			for (int i = 0; i < lhs.length && isEquals; i++)
				check(lhs[i], rhs[i]);
		}
		else
			isEquals = false;
	}

	private void check(float[] lhs, float[] rhs) {
		if (!isEquals || lhs == rhs)
			return;
		if (lhs != null && rhs != null && lhs.length == rhs.length) {
			for (int i = 0; i < lhs.length && isEquals; i++)
				check(lhs[i], rhs[i]);
		}
		else
			isEquals = false;
	}

	private void check(boolean[] lhs, boolean[] rhs) {
		if (!isEquals || lhs == rhs)
			return;
		if (lhs != null && rhs != null && lhs.length == rhs.length) {
			for (int i = 0; i < lhs.length && isEquals; i++)
				check(lhs[i], rhs[i]);
		}
		else
			isEquals = false;
	}


	// Cyclic object graphs detection

	private static final ThreadLocal<Set<Pair<IDKey, IDKey>>> REGISTRY = new ThreadLocal<>();

	private static Set<Pair<IDKey, IDKey>> getRegistry() {
		return REGISTRY.get();
	}

	private static Pair<IDKey, IDKey> getRegisterPair(Object lhs, Object rhs) {
		return Pair.of(new IDKey(lhs), new IDKey(rhs));
	}

	private static boolean isRegistered(Object lhs, Object rhs) {
		Set<Pair<IDKey, IDKey>> registry = getRegistry();
		if (registry == null)
			return false;
		Pair<IDKey, IDKey> pair = getRegisterPair(lhs, rhs);
		Pair<IDKey, IDKey> swappedPair = Pair.of(pair.getRight(), pair.getLeft());
		return registry.contains(pair) || registry.contains(swappedPair);
	}

	private static void register(Object lhs, Object rhs) {
		synchronized (EqualsByValueChecker.class) {
			if (getRegistry() == null)
				REGISTRY.set(new HashSet<>());
		}
		getRegistry().add(getRegisterPair(lhs, rhs));
	}

	private static void unregister(Object lhs, Object rhs) {
		Set<Pair<IDKey, IDKey>> registry = getRegistry();
		if (registry != null) {
			registry.remove(getRegisterPair(lhs, rhs));
			synchronized (EqualsByValueChecker.class) {
				registry = getRegistry();
				if (registry != null && registry.isEmpty())
					REGISTRY.remove();
			}
		}
	}

	private static final class IDKey {

		private final Object value;
		private final int id;

		public IDKey(Object value) {
			super();
			this.value = value;
			id = System.identityHashCode(value);
		}

		@Override public int hashCode() {
			return id;
		}

		@Override public boolean equals(Object other) {
			if (!(other instanceof IDKey)) return false;
			IDKey idKey = (IDKey)other;
			return value == idKey.value && id == idKey.id;
		}
	}
}
