package org.strangeforest.orm;

import java.io.*;

public interface FieldAccessor<T, V> extends Serializable {

	V get(T obj);
	void set(T obj, V value);
}
