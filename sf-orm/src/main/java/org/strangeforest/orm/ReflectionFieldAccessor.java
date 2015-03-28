package org.strangeforest.orm;

import java.io.*;
import java.lang.reflect.*;

public class ReflectionFieldAccessor<T, V> implements FieldAccessor<T, V> {

	private final Class<T> cls;
	private final String fieldName;
	private transient Field field;

	public ReflectionFieldAccessor(Class<T> cls, String fieldName) {
		super();
		this.cls = cls;
		this.fieldName = fieldName;
		lookupField();
	}

	private void lookupField() {
		try {
			field = cls.getDeclaredField(fieldName);
		}
		catch (NoSuchFieldException ex) {
			throw new RuntimeException(ex);
		}
		field.setAccessible(true);
	}

	public V get(T obj) {
		try {
			return (V)field.get(obj);
		}
		catch (IllegalAccessException ex) {
			throw new IllegalStateException(ex);
		}
	}

	public void set(T obj, V value) {
		try {
			field.set(obj, value);
		}
		catch (IllegalAccessException ex) {
			throw new IllegalStateException(ex);
		}
	}


	// Object methods

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.defaultWriteObject();
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		lookupField();
	}
}
