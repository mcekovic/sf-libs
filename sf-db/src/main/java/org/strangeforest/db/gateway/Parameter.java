package org.strangeforest.db.gateway;

public final class Parameter {

	// DSL

	public static Parameter param(String name, Object value) {
		return new Parameter(name, value);
	}


	// Impl

	final String name;
	final Object value;

	Parameter(String name, Object value) {
		this.name = name;
		this.value = value;
	}
}
