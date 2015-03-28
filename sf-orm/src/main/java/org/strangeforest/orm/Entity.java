package org.strangeforest.orm;

import java.io.*;

/**
 * <i>Entity</i> is an <i>Object</i> with unique identifier.
 * @param <I> type of the <i>Entity</i> identifier
 */
public abstract class Entity<I> implements Serializable {

	protected I id;

	protected Entity() {
		super();
	}

	protected Entity(I id) {
		super();
		this.id = id;
	}

	public I getId() {
		return id;
	}

	public void setId(I id) {
		this.id = id;
	}


	// Object methods

	@Override public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Entity that = (Entity)o;
		return id != null && that.id != null && id.equals(that.id);
	}

	@Override public int hashCode() {
		return id != null ? id.hashCode() : super.hashCode();
	}

	@Override public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getSimpleName());
		sb.append(": ");
		sb.append(id);
		return sb.toString();
	}
}
