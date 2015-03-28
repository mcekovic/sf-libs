package org.strangeforest.orm;

import java.util.*;

public abstract class DomainContextUtil {

	public static <I, E extends DomainEntity<I, E>> E getEntity(DomainContext context, Class<E> entityClass, Query query) {
		if (context == null)
			throw new IllegalStateException(EntityReference.LAZY_LOAD_NOT_SUPPORTED);
		return context.getRepository(entityClass).get(query);
	}

	public static <I, E extends DomainEntity<I, E>> E findEntity(DomainContext context, Class<E> entityClass, Query query) {
		if (context == null)
			throw new IllegalStateException(EntityReference.LAZY_LOAD_NOT_SUPPORTED);
		return context.getRepository(entityClass).find(query);
	}

	public static <I, E extends DomainEntity<I, E>> List<E> getEntityList(DomainContext context, Class<E> entityClass, Query query) {
		if (context != null)
			return context.getRepository(entityClass).getList(query);
		else
			return getEmptyList(context, entityClass);
	}

	public static <I, E extends DomainEntity<I, E>> EntityReferenceList<I, E> getEntityList(DomainContext context, Class<E> entityClass, List<E> entities) {
		return new EntityReferenceList<>(context, entityClass, entities, true);
	}

	public static <I, E extends DomainEntity<I, E>> EntityReferenceList<I, E> getEmptyList(DomainContext context, Class<E> entityClass) {
		return new EntityReferenceList<>(context, entityClass);
	}

	public static <I, E extends DomainEntity<I, E>, D extends DomainValue> List<D> getDetailList(DomainContext context, Class<E> entityClass, String detailName, I entityId) {
		return getDetailList(context, entityClass, detailName, entityId, null);
	}

	public static <I, E extends DomainEntity<I, E>, D extends DomainValue> List<D> getDetailList(DomainContext context, Class<E> entityClass, String detailName, I entityId, FieldAccessor<E, List<D>> accessor) {
		if (context != null)
			return context.getRepository(entityClass).getDetails(detailName, entityId, accessor);
		else
			return new ArrayList<>();
	}
}
