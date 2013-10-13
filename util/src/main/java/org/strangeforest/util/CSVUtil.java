package org.strangeforest.util;

import java.util.*;

public abstract class CSVUtil {

	private static final String[] EMPTY_STRING_ARRAY = new String[0];

	public static String[] toStringArray(String csv) {
		if (!StringUtil.isNullOrEmpty(csv)) {
			List<String> list = split(csv);
			return list.toArray(new String[list.size()]);
		}
		else
			return EMPTY_STRING_ARRAY;
	}

	public static List<String> toStringList(String csv) {
		return !StringUtil.isNullOrEmpty(csv) ? split(csv) : Collections.<String>emptyList();
	}

	public static List<Integer> toIntegerList(String csv) {
		List<String> integers = toStringList(csv);
		if (!integers.isEmpty()) {
			List<Integer> integerList = new ArrayList<>(integers.size());
			for (String integer : integers) {
				if (!StringUtil.isNullOrEmpty(integer)) {
					try {
						integerList.add(Integer.valueOf(integer));
					}
					catch (NumberFormatException ex) {
						throw new IllegalArgumentException("Invalid integer: " + integer, ex);
					}
				}
			}
			return integerList;
		}
		else
			return Collections.emptyList();
	}

	private static List<String> split(String csv) {
		List<String> items = new ArrayList<>();
		for (StringTokenizer st = new StringTokenizer(csv, ","); st.hasMoreTokens(); )
			items.add(st.nextToken());
		return items;
	}

	public static <T> String toCSV(Iterable<T> items) {
		return items != null ? StringUtil.concat(items, ',') : null;
	}
}
