package org.strangeforest.orm;

import java.util.*;

public class DomainContext {

	private final Map<String, Repository> repositories;
	private final Map<String, Object> services;

	public DomainContext() {
		super();
		repositories = new HashMap<>();
		services = new HashMap<>();
	}

	public <I, E extends DomainEntity<I, E>> void registerRepository(Class<E> entityClass, Repository<I, E> repository) {
		repositories.put(entityClass.getName(), repository);
	}

	public <I, E extends DomainEntity<I, E>> Repository<I, E> getRepository(Class<E> entityClass) {
		String entityName = entityClass.getName();
		Repository<I, E> repository = repositories.get(entityName);
		if (repository == null)
			throw new IllegalArgumentException("Cannot find Repository for Entity: " + entityName);
		return repository;
	}

	public Collection<Repository> getRepositories() {
		return repositories.values();
	}

	public boolean isAttached(Object obj) {
		return obj instanceof DomainContextAware && ((DomainContextAware)obj).getContext() != null;
	}

	public void attach(Object obj) {
		if (obj instanceof DomainContextAware)
			((DomainContextAware)obj).setContext(this);
		else if (obj instanceof Iterable) {
			for (Object item : ((Iterable)obj))
				attach(item);
		}
	}

	public void evictAll() {
		for (Repository repository : repositories.values())
			repository.evictAll();
	}


	// Services

	public void registerService(DomainService service) {
		services.put(service.getServiceClass().getName(), service);
	}

	public <S extends DomainService> S getService(Class<S> serviceClass) {
		String serviceName = serviceClass.getName();
		S service = (S)services.get(serviceName);
		if (service == null)
			throw new IllegalArgumentException("Cannot find Service: " + serviceName);
		return service;
	}


	// Utilities

	public static <I, E extends DomainEntity<I, E>> List<I> getIds(Iterable<E> entities) {
		if (entities instanceof EntityReferenceList)
			return new ArrayList<>(((EntityReferenceList<I, E>)entities).ids());
		else {
			List<I> ids = entities instanceof Collection ? new ArrayList<>(((Collection)entities).size()) : new ArrayList<>();
			for (E entity : entities)
				ids.add(entity.getId());
			return ids;
		}
	}
}
