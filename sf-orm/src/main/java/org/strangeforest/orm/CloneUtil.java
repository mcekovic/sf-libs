package org.strangeforest.orm;

import java.util.*;

import org.w3c.dom.*;

public abstract class CloneUtil {

	public static <T> List<T> cloneList(List<T> list) {
		return cloneList(list, true);
	}

	public static <T> List<T> cloneList(List<T> list, boolean deep) {
		if (list == null)
			return null;
		else if (list instanceof EntityReferenceList)
			return ((EntityReferenceList)list).cloneList();
		else {
			ArrayList<T> clonedList = new ArrayList<>(list.size());
			for (T item : list)
				clonedList.add(deep && item instanceof DeepCloneable ? (T)((DeepCloneable)item).deepClone() : item);
			return clonedList;
		}
	}

	public static <N extends Node> N cloneNode(N node) {
		return node != null ? (N)node.cloneNode(true) : null;
	}

	public static <T extends DeepCloneable<T>> T cloneObject(T obj) {
		return obj != null ? obj.deepClone() : null;
	}
}
