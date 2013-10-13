package org.strangeforest.util;

public interface BiDirFunction<K, V> extends Function<K, V> {

	K unapply(V value);
}
