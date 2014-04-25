package org.strangeforest.util;

import java.util.function.*;

public interface BiDirFunction<K, V> extends Function<K, V> {

	K unapply(V value);
}
