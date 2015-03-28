package org.strangeforest.orm;

import org.apache.commons.lang3.builder.*;

/**
 * Base class for all entities in the domain. Cached instances are held in Repository caches.
 * Entities do not need to be thread safe as entity instances in cache are not modified
 * while entity instances that are used in transactions are supposed to be used by single thread. 
 * @param <I> ID class
 * @param <E> Entity class itself
 */
public abstract class DomainEntity<I, E extends DomainEntity<I, E>> extends Entity<I> implements EquatableByValue<E>, DeepCloneable<E>, DomainContextAware {

	private long _version;
	protected transient DomainContext context;

	private static final String[] EXCLUDE_VERSION = {EntityDAO._VERSION};

	protected DomainEntity(I id) {
		super(id);
	}

	public long get_version() {
		return _version;
	}

	public void set_version(long _version) {
		this._version = _version;
	}

	public void inc_version() {
		_version++;
	}

	public void dec_version() {
		_version--;
	}

	@Override public DomainContext getContext() {
		return context;
	}

	@Override public void setContext(DomainContext context) {
		this.context = context;
	}

	@Override public final E deepClone() {
		try {
			return clone();
		}
		catch (CloneNotSupportedException ex) {
			throw new IllegalStateException(ex);
		}
	}

	@Override protected E clone() throws CloneNotSupportedException {
		return (E)super.clone();
	}

	public final E lazyDeepClone() {
		try {
			return lazyClone();
		}
		catch (CloneNotSupportedException ex) {
			throw new IllegalStateException(ex);
		}
	}

	protected E lazyClone() throws CloneNotSupportedException {
		return clone();
	}

	@Override public boolean equalsByValue(E entity) {
		return EqualsByValueUtil.reflectionEqualsByValue(this, entity, EXCLUDE_VERSION);
	}

	protected void loadDetails() {}

	@Override public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

	public static final Query QUERY_ALL = new Query("All");
}
