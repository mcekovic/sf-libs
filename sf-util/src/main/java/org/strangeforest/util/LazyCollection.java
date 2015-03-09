package org.strangeforest.util;

import java.util.*;

public interface LazyCollection<K, V> extends Collection<V> {

	Collection<K> keys();
}
