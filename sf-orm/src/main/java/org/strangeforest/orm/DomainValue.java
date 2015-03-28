package org.strangeforest.orm;

import java.io.*;

import org.apache.commons.lang3.builder.*;

public abstract class DomainValue<T extends DomainValue<T>> implements EquatableByValue<T>, DeepCloneable<T>, Serializable {

	protected transient DomainContext context;

	public DomainContext getContext() {
		return context;
	}

	public void setContext(DomainContext context) {
		this.context = context;
	}

	@Override public final T deepClone() {
		try {
			return clone();
		}
		catch (CloneNotSupportedException ex) {
			throw new IllegalStateException(ex);
		}
	}

	@Override protected T clone() throws CloneNotSupportedException {
		return (T)super.clone();
	}

	public final T lazyDeepClone() {
		try {
			return lazyClone();
		}
		catch (CloneNotSupportedException ex) {
			throw new IllegalStateException(ex);
		}
	}

	protected T lazyClone() throws CloneNotSupportedException {
		return clone();
	}

	@Override public final boolean equalsByValue(T detail) {
		return EqualsByValueUtil.reflectionEqualsByValue(this, detail);
	}

	@Override public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

	@Override public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
