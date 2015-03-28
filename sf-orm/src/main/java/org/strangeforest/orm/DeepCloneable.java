package org.strangeforest.orm;

public interface DeepCloneable<T extends DeepCloneable<T>> extends Cloneable {

	T deepClone();
}
