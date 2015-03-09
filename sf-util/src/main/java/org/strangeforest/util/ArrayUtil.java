package org.strangeforest.util;

import java.util.*;

/**
 * <p>Utility class providing static methods for arrays similar with what <tt>java.util.List</tt> have.</p>
 */
public abstract class ArrayUtil {

	// contains

	public static boolean contains(byte[] a, byte b) {
		for (byte e : a)
			if (e == b)
				return true;
		return false;
	}

	public static boolean contains(short[] a, short s) {
		for (short e : a)
			if (e == s)
				return true;
		return false;
	}

	public static boolean contains(int[] a, int i) {
		for (int e : a)
			if (e == i)
				return true;
		return false;
	}

	public static boolean contains(long[] a, long l) {
		for (long e : a)
			if (e == l)
				return true;
		return false;
	}

	public static boolean contains(float[] a, float f) {
		for (float e : a)
			if (e == f)
				return true;
		return false;
	}

	public static boolean contains(double[] a, double d) {
		for (double e : a)
			if (e == d)
				return true;
		return false;
	}

	public static boolean contains(char[] a, char c) {
		for (char e : a)
			if (e == c)
				return true;
		return false;
	}

	public static boolean contains(boolean[] a, boolean b) {
		for (boolean e : a)
			if (e == b)
				return true;
		return false;
	}

	public static <E> boolean contains(E[] a, E o) {
		for (E e : a)
			if (Objects.equals(e, o))
				return true;
		return false;
	}


	// reverse

	public static void reverse(byte[] a) {
		int size = a.length;
		int mid = size / 2;
		byte t;
		for (int i = 0, j = size - 1; i < mid; i++, j--) {
			t = a[i];
			a[i] = a[j];
			a[j] = t;
		}
	}

	public static void reverse(short[] a) {
		int size = a.length;
		int mid = size / 2;
		short t;
		for (int i = 0, j = size - 1; i < mid; i++, j--) {
			t = a[i];
			a[i] = a[j];
			a[j] = t;
		}
	}

	public static void reverse(int[] a) {
		int size = a.length;
		int mid = size/2;
		int t;
		for (int i = 0, j = size-1; i < mid; i++, j--) {
			t = a[i];
			a[i] = a[j];
			a[j] = t;
		}
	}

	public static void reverse(long[] a) {
		int size = a.length;
		int mid = size/2;
		long t;
		for (int i = 0, j = size-1; i < mid; i++, j--) {
			t = a[i];
			a[i] = a[j];
			a[j] = t;
		}
	}

	public static void reverse(float[] a) {
		int size = a.length;
		int mid = size / 2;
		float t;
		for (int i = 0, j = size - 1; i < mid; i++, j--) {
			t = a[i];
			a[i] = a[j];
			a[j] = t;
		}
	}

	public static void reverse(double[] a) {
		int size = a.length;
		int mid = size/2;
		double t;
		for (int i = 0, j = size-1; i < mid; i++, j--) {
			t = a[i];
			a[i] = a[j];
			a[j] = t;
		}
	}

	public static void reverse(char[] a) {
		int size = a.length;
		int mid = size / 2;
		char t;
		for (int i = 0, j = size - 1; i < mid; i++, j--) {
			t = a[i];
			a[i] = a[j];
			a[j] = t;
		}
	}

	public static void reverse(boolean[] a) {
		int size = a.length;
		int mid = size / 2;
		boolean t;
		for (int i = 0, j = size - 1; i < mid; i++, j--) {
			t = a[i];
			a[i] = a[j];
			a[j] = t;
		}
	}

	public static <E> void reverse(E[] a) {
		int size = a.length;
		int mid = size/2;
		E t;
		for (int i = 0, j = size-1; i < mid; i++, j--) {
			t = a[i];
			a[i] = a[j];
			a[j] = t;
		}
	}


	// subArray

	public static byte[] subArray(byte[] a, int from, int to) {
		int len = Math.max(to-from+1, 0);
		byte[] sa = new byte[len];
		System.arraycopy(a, from, sa, 0, len);
		return sa;
	}

	public static short[] subArray(short[] a, int from, int to) {
		int len = Math.max(to-from+1, 0);
		short[] sa = new short[len];
		System.arraycopy(a, from, sa, 0, len);
		return sa;
	}

	public static int[] subArray(int[] a, int from, int to) {
		int len = Math.max(to-from+1, 0);
		int[] sa = new int[len];
		System.arraycopy(a, from, sa, 0, len);
		return sa;
	}

	public static long[] subArray(long[] a, int from, int to) {
		int len = Math.max(to-from+1, 0);
		long[] sa = new long[len];
		System.arraycopy(a, from, sa, 0, len);
		return sa;
	}

	public static float[] subArray(float[] a, int from, int to) {
		int len = Math.max(to-from+1, 0);
		float[] sa = new float[len];
		System.arraycopy(a, from, sa, 0, len);
		return sa;
	}

	public static double[] subArray(double[] a, int from, int to) {
		int len = Math.max(to-from+1, 0);
		double[] sa = new double[len];
		System.arraycopy(a, from, sa, 0, len);
		return sa;
	}

	public static char[] subArray(char[] a, int from, int to) {
		int len = Math.max(to-from+1, 0);
		char[] sa = new char[len];
		System.arraycopy(a, from, sa, 0, len);
		return sa;
	}

	public static boolean[] subArray(boolean[] a, int from, int to) {
		int len = Math.max(to-from+1, 0);
		boolean[] sa = new boolean[len];
		System.arraycopy(a, from, sa, 0, len);
		return sa;
	}

	public static <E> E[] subArray(E[] a, int from, int to) {
		int len = Math.max(to-from+1, 0);
		E[] sa = (E[])(new Object[len]);
		System.arraycopy(a, from, sa, 0, len);
		return sa;
	}


	// objectArray

	public static Byte[] objectArray(byte... a) {
		int len = a.length;
		Byte[] ba = new Byte[len];
		for (int i = 0; i < len; i++)
			ba[i] = Byte.valueOf(a[i]);
		return ba;
	}

	public static Short[] objectArray(short... a) {
		int len = a.length;
		Short[] sa = new Short[len];
		for (int i = 0; i < len; i++)
			sa[i] = Short.valueOf(a[i]);
		return sa;
	}

	public static Integer[] objectArray(int... a) {
		int len = a.length;
		Integer[] ia = new Integer[len];
		for (int i = 0; i < len; i++)
			ia[i] = Integer.valueOf(a[i]);
		return ia;
	}

	public static Long[] objectArray(long... a) {
		int len = a.length;
		Long[] la = new Long[len];
		for (int i = 0; i < len; i++)
			la[i] = Long.valueOf(a[i]);
		return la;
	}

	public static Float[] objectArray(float... a) {
		int len = a.length;
		Float[] fa = new Float[len];
		for (int i = 0; i < len; i++)
			fa[i] = a[i];
		return fa;
	}

	public static Double[] objectArray(double... a) {
		int len = a.length;
		Double[] da = new Double[len];
		for (int i = 0; i < len; i++)
			da[i] = a[i];
		return da;
	}

	public static Character[] objectArray(char... a) {
		int len = a.length;
		Character[] ca = new Character[len];
		for (int i = 0; i < len; i++)
			ca[i] = Character.valueOf(a[i]);
		return ca;
	}

	public static Boolean[] objectArray(boolean... a) {
		int len = a.length;
		Boolean[] ba = new Boolean[len];
		for (int i = 0; i < len; i++)
			ba[i] = Boolean.valueOf(a[i]);
		return ba;
	}


	// primitiveArray

	public static byte[] primitiveArray(Byte... a) {
		int len = a.length;
		byte[] ba = new byte[len];
		for (int i = 0; i < len; i++) {
			Byte e = a[i];
			ba[i] = e != null ? e : 0;
		}
		return ba;
	}

	public static short[] primitiveArray(Short... a) {
		int len = a.length;
		short[] sa = new short[len];
		for (int i = 0; i < len; i++) {
			Short e = a[i];
			sa[i] = e != null ? e : 0;
		}
		return sa;
	}

	public static int[] primitiveArray(Integer... a) {
		int len = a.length;
		int[] ia = new int[len];
		for (int i = 0; i < len; i++) {
			Integer e = a[i];
			ia[i] = e != null ? e : 0;
		}
		return ia;
	}

	public static long[] primitiveArray(Long[] a) {
		int len = a.length;
		long[] la = new long[len];
		for (int i = 0; i < len; i++) {
			Long e = a[i];
			la[i] = e != null ? e : 0L;
		}
		return la;
	}

	public static float[] primitiveArray(Float[] a) {
		int len = a.length;
		float[] fa = new float[len];
		for (int i = 0; i < len; i++) {
			Float e = a[i];
			fa[i] = e != null ? e : 0.0f;
		}
		return fa;
	}

	public static double[] primitiveArray(Double[] a) {
		int len = a.length;
		double[] da = new double[len];
		for (int i = 0; i < len; i++) {
			Double e = a[i];
			da[i] = e != null ? e : 0.0;
		}
		return da;
	}

	public static char[] primitiveArray(Character[] a) {
		int len = a.length;
		char[] ca = new char[len];
		for (int i = 0; i < len; i++) {
			Character e = a[i];
			ca[i] = e != null ? e : 0;
		}
		return ca;
	}

	public static boolean[] primitiveArray(Boolean[] a) {
		int len = a.length;
		boolean[] ba = new boolean[len];
		for (int i = 0; i < len; i++) {
			Boolean e = a[i];
			ba[i] = e != null ? e : false;
		}
		return ba;
	}


	// asList

	public static List<Byte> asList(byte... a) {
		return Arrays.asList(objectArray(a));
	}

	public static List<Short> asList(short... a) {
		return Arrays.asList(objectArray(a));
	}

	public static List<Integer> asList(int... a) {
		return Arrays.asList(objectArray(a));
	}

	public static List<Long> asList(long... a) {
		return Arrays.asList(objectArray(a));
	}

	public static List<Float> asList(float... a) {
		return Arrays.asList(objectArray(a));
	}

	public static List<Double> asList(double... a) {
		return Arrays.asList(objectArray(a));
	}

	public static List<Character> asList(char... a) {
		return Arrays.asList(objectArray(a));
	}

	public static List<Boolean> asList(boolean... a) {
		return Arrays.asList(objectArray(a));
	}
}
