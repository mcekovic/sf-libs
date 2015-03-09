package org.strangeforest.util;

import java.util.*;
import java.util.regex.*;
import java.io.*;

public abstract class StringUtil {

	public static final String EMPTY = "";

	public static boolean isNullOrEmpty(String s) {
		return s == null || s.length() == 0;
	}

	public static boolean equalIgnoreCase(String s1, String s2) {
		return s1 == s2 || (s1 != null && s1.equalsIgnoreCase(s2));
	}

	public static boolean startsWithIgnoreCase(String s1, String s2) {
		return s1.length() >= s2.length() && s1.substring(0, s2.length()).equalsIgnoreCase(s2);
	}

	public static String intern(String s) {
		return s != null ? s.intern() : null;
	}

	public static String maskNull(String s) {
		return s != null ? s : EMPTY;
	}

	public static String concatIfNotNull(String... strings) {
		StringBuilder sb = new StringBuilder();
		for (String s : strings) {
			if (!isNullOrEmpty(s))
				sb.append(s);
		}
		return sb.toString();
	}

	public static <T> String concat(Iterable<T> items, char separator) {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (T item : items) {
			if (first)
				first = false;
			else
				sb.append(separator);
			sb.append(item);
		}
		return sb.toString();
	}

	public static String copy(String s, int count) {
		StringBuilder sb = new StringBuilder(s.length()*count);
		for (int i = 0; i < count; i++)
			sb.append(s);
		return sb.toString();
	}

	public static String copy(char c, int count) {
		char[] cs = new char[count];
		Arrays.fill(cs, c);
		return new String(cs);
	}

	public static String plural(String noun) {
		if (isNullOrEmpty(noun))
			return noun;
		char last = noun.charAt(noun.length()-1);
		if (Character.isLowerCase(last)) {
			if (noun.endsWith("x") || noun.endsWith("s"))
				return noun + "es";
			else if (noun.endsWith("y"))
				return noun.substring(0, noun.length() - 1) + "ies";
			else
				return noun + "s";
		}
		else if (Character.isUpperCase(last)) {
			if (noun.endsWith("X") || noun.endsWith("S"))
				return noun + "ES";
			else if (noun.endsWith("Y"))
				return noun.substring(0, noun.length() - 1) + "IES";
			else
				return noun + "S";
		}
		else
			return noun;
	}

	public static String capitalize(String s) {
		StringBuilder sb = new StringBuilder();
		for (StringTokenizer t = new StringTokenizer(s, " ", true); t.hasMoreTokens(); ) {
			String word = t.nextToken();
			sb.append(Character.toUpperCase(word.charAt(0)));
			sb.append(word.substring(1));
		}
		return sb.toString();
	}

	private static final String CNT_STRING = "...";
	private static final int CNT_STR_LEN = CNT_STRING.length();

	public static String shortString(String s, int maxLength) {
		return shortString(s, maxLength, 1);
	}

	/**
	 * Shortens string to specified length by inserting '...' instead of long text if longer then maximum length.
	 * @param s string to shorten
	 * @param maxLength maximum length
	 * @param pos position where to insert '...': -1 at the beginning, 0 in the middle, 1 at the end.
	 * @return string with specified maximum length or original if shorter then maximum length.
	 */
	public static String shortString(String s, int maxLength, int pos) {
		if (maxLength > CNT_STR_LEN) {
			int length = s.length();
			if (length > maxLength) {
				int newLength = maxLength - CNT_STR_LEN;
				if (pos > 0)
					return s.substring(0, newLength) + CNT_STRING;
				else if (pos == 0) {
					int cntPos = newLength / 2;
					return s.substring(0, cntPos) + CNT_STRING + s.substring(newLength - cntPos);
				}
				else
					return CNT_STRING + s.substring(length - newLength);
			}
			else
				return s;
		}
		else
			return CNT_STRING.substring(0, maxLength);
	}

	public static String maxLength(String s, int maxLength) {
		return s.length() > maxLength ? s.substring(0, maxLength) : s;
	}

	public static String escape(String s, char ec) {
		char[] chars = s.toCharArray();
		int length = chars.length;
		StringBuilder b = new StringBuilder(length + 16);
		for (char c : chars) {
			if (c == ec)
				b.append('\\');
			b.append(c);
		}
		return b.toString();
	}

	public static String escapeXML(String s) {
		StringWriter writer = new StringWriter((int)(s.length()*1.1));
		try {
			escapeXML(writer, s);
		}
		catch (IOException ex) {
			throw new IllegalStateException(ex);
		}
		return writer.toString();
	}

	private static void escapeXML(Writer writer, String s) throws IOException {
		int length = s.length();
		for (int i = 0; i < length; i++) {
			char c = s.charAt(i);
			switch (c) {
				case '&': writer.write("&amp;"); break;
				case '<': writer.write("&lt;"); break;
				case '>': writer.write("&gt;"); break;
				case '"': writer.write("&quot;"); break;
				case '\'': writer.write("&apos;"); break;
				default:
					if (c < 0x80)
						writer.write(c);
					else {
						writer.write("&#");
						writer.write(Integer.toString(c, 10));
						writer.write(';');
					}
			}
		}
	}

	public static List<String> breakIntoLines(String s, int maxLineSize) {
		List<String> list = new ArrayList<>();
		if (s == null)
			return list;
		StringBuilder line = new StringBuilder(maxLineSize);
		for (StringTokenizer t = new StringTokenizer(s); t.hasMoreTokens(); ) {
			String word = t.nextToken();
			int len = line.length();
			if (len + word.length() < maxLineSize) {
				if (len > 0)
					line.append(' ');
			}
			else {
				list.add(line.toString());
				line = new StringBuilder(maxLineSize);
			}
			line.append(word);
		}
		if (line.length() > 0)
			list.add(line.toString());
		return list;
	}

	public static String appendLine(String s, String line) {
		if (isNullOrEmpty(line))
			return s;
		else if (isNullOrEmpty(s))
			return line;
		else
			return new StringBuilder(s).append('\n').append(line).toString();
	}

	private static final Pattern PATTERN = Pattern.compile("\\s+");
	private static final String SPACE = " ";

	public static String compactWhiteSpace(String text) {
		return isNullOrEmpty(text) ? text : PATTERN.matcher(text.trim()).replaceAll(SPACE);
	}

	public static String padLeft(String s, int length) {
		return pad(s, length, ' ', true);
	}

	public static String padRight(String s, int length) {
		return pad(s, length, ' ', false);
	}

	public static String pad(String s, int length, char pc, boolean left) {
		if (s == null)
			s = EMPTY;
		int slen = s.length();
		if (slen < length) {
			StringBuilder sb = new StringBuilder(length);
			if (!left)
				sb.append(s);
			for (int i = slen; i < length; i++)
				sb.append(pc);
			if (left)
				sb.append(s);
			return sb.toString();
		}
		else if (slen == length)
			return s;
		else
			return left ? s.substring(slen - length) : s.substring(0, length);
	}

	public static String toString(Object obj) {
		if (obj != null) {
			Class cls = obj.getClass();
			if (cls.isArray()) {
				if (obj instanceof Object[])
					return Arrays.toString((Object[])obj);
				else if (obj instanceof byte[])
					return Arrays.toString((byte[])obj);
				else if (obj instanceof short[])
					return Arrays.toString((short[])obj);
				else if (obj instanceof int[])
					return Arrays.toString((int[])obj);
				else if (obj instanceof long[])
					return Arrays.toString((long[])obj);
				else if (obj instanceof float[])
					return Arrays.toString((float[])obj);
				else if (obj instanceof double[])
					return Arrays.toString((double[])obj);
				else if (obj instanceof char[])
					return Arrays.toString((char[])obj);
				else if (obj instanceof boolean[])
					return Arrays.toString((boolean[])obj);
			}
		}
		return String.valueOf(obj);
	}

	public static String toStringOrNull(Object obj) {
		return obj != null ? obj.toString() : null;
	}

    public static String normalizeWhiteSpace(String phrase) {
        return phrase != null ? phrase.replaceAll("\\s+", " ").trim() : null;
    }
}
