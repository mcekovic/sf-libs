package org.strangeforest.util;

public abstract class PrimitiveUtil {

	public static int compare(boolean b1, boolean b2) {
		return !b1 && b2 ? -1 : (b1 == b2 ? 0 : 1);
	}

	public static int compare(byte b1, byte b2) {
		return b1 < b2 ? -1 : (b1 == b2 ? 0 : 1);
	}

	public static int compare(short s1, short s2) {
		return s1 < s2 ? -1 : (s1 == s2 ? 0 : 1);
	}

	public static int compare(int i1, int i2) {
		return i1 < i2 ? -1 : (i1 == i2 ? 0 : 1);
	}

	public static int compare(long l1, long l2) {
		return l1 < l2 ? -1 : (l1 == l2 ? 0 : 1);
	}

	public static int compare(float f1, float f2) {
		return f1 < f2 ? -1 : (f1 == f2 ? 0 : 1);
	}

	public static int compare(double d1, double d2) {
		return d1 < d2 ? -1 : (d1 == d2 ? 0 : 1);
	}

	public static int compare(char c1, byte c2) {
		return c1 < c2 ? -1 : (c1 == c2 ? 0 : 1);
	}
}
