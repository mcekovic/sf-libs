package org.strangeforest.orm;

public interface EquatableByValue<T extends EquatableByValue<T>> {

	boolean equalsByValue(T obj);
}
