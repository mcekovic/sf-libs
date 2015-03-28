package org.strangeforest.orm;

import java.io.*;
import java.util.*;

public class EntityReferenceList<I, E extends DomainEntity<I, E>> extends AbstractList<E> implements DomainContextAware, EquatableByValue<EntityReferenceList<I, E>>, Serializable, Cloneable {

	private transient DomainContext context;
	private final Class<E> entityClass;
	private List<EntityReference<I, E>> refs;

	public EntityReferenceList(DomainContext context, Class<E> entityClass) {
		this(context, entityClass, 10);
	}

	public EntityReferenceList(DomainContext context, Class<E> entityClass, int initialCapacity) {
		this(context, entityClass, new ArrayList<EntityReference<I, E>>(initialCapacity));
	}

	EntityReferenceList(DomainContext context, Class<E> entityClass, Collection<I> ids) {
		this(context, entityClass, ids.size());
		for (I id : ids)
			addId(id);
	}

	EntityReferenceList(DomainContext context, Class<E> entityClass, Collection<E> entities, boolean dummy) {
		this(context, entityClass, entities.size());
		for (E entity : entities)
			add(entity);
	}

	private EntityReferenceList(DomainContext context, Class<E> entityClass, List<EntityReference<I, E>> refs) {
		this.context = context;
		this.entityClass = entityClass;
		this.refs = refs;
	}

	public Class<E> getEntityClass() {
		return entityClass;
	}

	@Override public DomainContext getContext() {
		return context;
	}

	@Override public void setContext(DomainContext context) {
		this.context = context;
		if (context != null) {
			for (EntityReference<I, E> ref : refs) {
            if (!context.isAttached(ref))
					context.attach(ref);
			}
		}
	}

	public List<EntityReference<I, E>> getReferences() {
		return refs;
	}

	public I getId(int index) {
		return refs.get(index).getId();
	}

	@Override public E get(int index) {
		return refs.get(index).get();
	}

	public boolean addId(I id) {
		return refs.add(new EntityReference<>(context, entityClass, id));
	}

	@Override public boolean add(E entity) {
		return refs.add(new EntityReference<>(context, entityClass, entity));
	}

	public void addId(int index, I id) {
		refs.add(index, new EntityReference<>(context, entityClass, id));
	}

	@Override public void add(int index, E entity) {
		refs.add(index, new EntityReference<>(context, entityClass, entity));
	}

	public I removeId(int index) {
		EntityReference<I, E> ref = refs.remove(index);
		return ref != null ? ref.getId() : null;
	}

	@Override public E remove(int index) {
		EntityReference<I, E> ref = refs.remove(index);
		return ref != null ? ref.get() : null;
	}

	public boolean removeId(Object o) {
		return refs.remove(new EntityReference<>(entityClass, (I)o));
	}

	@Override public boolean remove(Object o) {
		return entityClass.isInstance(o) && refs.remove(new EntityReference<>(entityClass, (E)o));
	}

	public I setId(int index, I id) {
		EntityReference<I, E> ref = refs.get(index);
		I oldId = ref.getId();
		ref.setId(id);
		return oldId;
	}

	@Override public E set(int index, E entity) {
		EntityReference<I, E> ref = refs.get(index);
		E oldEntity = ref.get();
		ref.set(entity);
		return oldEntity;
	}

	public boolean containsId(Object o) {
		return refs.contains(new EntityReference<>(context, entityClass, (I)o));
	}

	@Override public boolean contains(Object o) {
		return entityClass.isInstance(o) && refs.contains(new EntityReference<>(context, entityClass, (E)o));
	}

	public int indexOfId(Object o) {
		return refs.indexOf(new EntityReference<>(context, entityClass, (I)o));
	}

	@Override public int indexOf(Object o) {
		return entityClass.isInstance(o) ? refs.indexOf(new EntityReference<>(context, entityClass, (E)o)) : -1;
	}

	public int lastIndexOfId(Object o) {
		return refs.lastIndexOf(new EntityReference<>(context, entityClass, (I)o));
	}

	@Override public int lastIndexOf(Object o) {
		return entityClass.isInstance(o) ? refs.lastIndexOf(new EntityReference<>(context, entityClass, (E)o)) : -1;
	}

	@Override public int size() {
		return refs.size();
	}

	@Override public boolean isEmpty() {
		return refs.isEmpty();
	}

	@Override public List<E> subList(int fromIndex, int toIndex) {
		return new EntityReferenceList<>(context, entityClass, refs.subList(fromIndex, toIndex));
	}

	@Override public void clear() {
		refs.clear();
	}

	public List<I> ids() {
		return new AbstractList<I>() {
			@Override public I get(int index) {
				return EntityReferenceList.this.getId(index);
			}
			@Override public int size() {
				return EntityReferenceList.this.size();
			}
		};
	}

	public List<E> entities() {
		return new AbstractList<E>() {
			@Override public E get(int index) {
				return EntityReferenceList.this.get(index);
			}
			@Override public int size() {
				return EntityReferenceList.this.size();
			}
		};
	}


	// Object methods

	@Override public boolean equalsByValue(EntityReferenceList<I, E> list) {
		if (list == this) return true;
		if (!entityClass.equals(list.entityClass)) return false;
		return EqualsByValueUtil.equalByValue(refs, list.refs);
	}

	@Override public EntityReferenceList<I, E> clone() throws CloneNotSupportedException {
		EntityReferenceList<I, E> list = (EntityReferenceList<I, E>)super.clone();
		list.refs = new ArrayList<>(refs.size());
		for (EntityReference<I, E> ref : refs)
			list.refs.add(ref.clone());
		return list;
	}

	public EntityReferenceList<I, E> cloneList() {
		try {
			return clone();
		}
		catch (CloneNotSupportedException ex) {
			throw new IllegalStateException(ex);
		}
	}
}
