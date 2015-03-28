package org.strangeforest.orm;

import java.io.*;
import java.util.*;

public final class EntityReference<I, E extends DomainEntity<I, E>> implements DomainContextAware, EquatableByValue<EntityReference<I, E>>, Serializable, Cloneable {

	private transient DomainContext context;
	private final Class<E> entityClass;
	private I id;
	private E entity;

	static final String LAZY_LOAD_NOT_SUPPORTED = "Lazy load is not supported on detached instances.";

	public EntityReference(Class<E> entityClass) {
		super();
		this.entityClass = entityClass;
	}

	public EntityReference(Class<E> entityClass, I id) {
		this(entityClass);
		this.id = id;
	}

	public EntityReference(Class<E> entityClass, E entity) {
		this(entityClass);
		this.entity = entity;
		if (entity != null)
			id = entity.getId();
	}

	EntityReference(DomainContext context, Class<E> entityClass, I id) {
		this(entityClass, id);
		this.context = context;
	}

	EntityReference(DomainContext context, Class<E> entityClass, E entity) {
		this(entityClass, entity);
		setContext(context);
	}

	public Class<E> getEntityClass() {
		return entityClass;
	}

	@Override public DomainContext getContext() {
		return context;
	}

	@Override public void setContext(DomainContext context) {
		this.context = context;
		if (context != null && !context.isAttached(entity))
			context.attach(entity);
	}

	public I getId() {
		return entity != null ? entity.getId() : id;
	}

	public I getMappedId() {
		if (entity != null) {
			I id = entity.getId();
			if (id == null)
				throw new IllegalStateException("Referenced entity does not have an ID.");
			return id;
		}
		else
			return id;
	}

	public void setId(I id) {
		if (!Objects.equals(id, this.id)) {
			this.id = id;
			entity = null;
		}
	}

	public E get() {
		if (entity == null && id != null) {
			if (context == null)
				throw new IllegalStateException(LAZY_LOAD_NOT_SUPPORTED);
			entity = context.getRepository(entityClass).get(id);
		}
		return entity;
	}

	public void set(E entity) {
		this.entity = entity;
		if (entity != null) {
			id = entity.getId();
			if (context != null)
				context.attach(entity);
		}
		else
			id = null;
	}

	public boolean isNull() {
		return id == null && entity == null;
	}

	public void setNull() {
		id = null;
		entity = null;
	}

	public boolean isSame(EntityReference<I, E> ref) {
		return !isNull() && !ref.isNull() && equals(ref);
	}

	public void refresh() {
		entity = context.getRepository(entityClass).get(id);
	}


	// Object methods

	@Override public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof EntityReference)) return false;
		EntityReference that = (EntityReference)obj;
		if (!entityClass.equals(that.entityClass)) return false;
		Object id = getId();
		Object thatId = that.getId();
		if (id != null || thatId != null)
			return Objects.equals(id, thatId);
		else
			return Objects.equals(entity, that.entity);
	}

	@Override public boolean equalsByValue(EntityReference<I, E> ref) {
		if (this == ref) return true;
		if (!entityClass.equals(ref.entityClass)) return false;
		Object id = getId();
		Object thatId = ref.getId();
		if (id != null || thatId != null)
			return Objects.equals(id, thatId);
		else
			return EqualsByValueUtil.reflectionEqualsByValue(entity, ref.entity);
	}

	@Override public int hashCode() {
		Object id = getId();
		return 31 * entityClass.hashCode() + (id != null ? id.hashCode() : (entity != null ? entity.hashCode() : 0));
	}

	@Override public String toString() {
		return String.valueOf(getId());
	}

	@Override public EntityReference<I, E> clone() throws CloneNotSupportedException {
		EntityReference<I, E> ref = (EntityReference<I, E>)super.clone();
		if (ref.entity != null) {
			ref.id = ref.entity.getId();
			ref.entity = null;
		}
		return ref;
	}
}
