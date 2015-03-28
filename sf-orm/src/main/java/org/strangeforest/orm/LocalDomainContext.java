package org.strangeforest.orm;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

import org.strangeforest.concurrent.*;

public class LocalDomainContext extends DomainContext implements Closeable {

	private final ScheduledExecutorService cacheExpirer;

	public LocalDomainContext() {
		super();
		cacheExpirer = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("Domain Cache Expirer", true, true));
	}

	ScheduledExecutorService getCacheExpirer() {
		return cacheExpirer;
	}

	@Override public <I, E extends DomainEntity<I, E>> Repository<I, E> getRepository(Class<E> entityClass) {
		return super.getRepository(entityClass);
	}

	@Override public void evictAll() {
		super.evictAll();
	}

	public void useEntityCache(boolean useEntityCache) {
		for (Repository repository : getRepositories())
			((LocalRepository)repository).setUseCache(useEntityCache);
	}

	public void useQueryCache(boolean useQueryCache) {
		for (Repository repository : getRepositories())
			((LocalRepository)repository).setUseQueryCache(useQueryCache);
	}

	@Override public void close() {
		cacheExpirer.shutdown();
	}


	// Statistics

	public Map<String, Object> getStatisticsAsMap() {
		Map<String, Object> stats = new HashMap<>();
		int cachedEntities = 0, cachedQueries = 0;
		Collection<Repository> repositories = getRepositories();
		for (Repository repository : repositories) {
			LocalRepository localRepository = (LocalRepository)repository;
			cachedEntities += localRepository.getCachedEntityCount();
			cachedQueries  += localRepository.getCachedQueryCount();
		}
		stats.put("Repositories", repositories.size());
		stats.put("CachedEntities", cachedEntities);
		stats.put("CachedQueries", cachedQueries);
		return stats;
	}

	public List<String> getRepositoryNames() {
		List<String> names = new ArrayList<>();
		for (Repository repository : getRepositories())
			names.add(repository.getEntityName());
		return names;
	}

	public void resetStatistics() {
		for (Repository repository : getRepositories())
			((LocalRepository)repository).resetStatistics();
	}
}
