package org.strangeforest.orm;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;

import org.strangeforest.cache.*;
import org.strangeforest.concurrent.*;
import org.strangeforest.transaction.*;
import org.strangeforest.util.*;

import static org.strangeforest.orm.CloneUtil.*;

public class LocalRepository<I, E extends DomainEntity<I, E>> implements Repository<I, E> {

	private final LocalDomainContext context;
	private final Class<E> entityClass;
	private final EntityDAO<I, E> dao;
	private boolean useCache;
	private final LockableCache<I, E> cache;
	private LockableCache<Query, List<I>> queryCache;
	private LockableCache<PredicatedQuery, List<I>> predicatedQueryCache;
	private LockManager<I> lockManager;

	private static final long CACHE_EXPIRY_PERIOD       = 3600000L;
	private static final long CACHE_CHECK_EXPIRY_PERIOD =   60000L;

	public LocalRepository(LocalDomainContext context, EntityDAO<I, E> dao) {
		this(context, dao, true, true, false);
	}

	public LocalRepository(LocalDomainContext context, EntityDAO<I, E> dao, boolean useCache, boolean useQueryCache, boolean usePredicatedQueryCache) {
		super();
		this.context = context;
		this.entityClass = dao.entityClass();
		this.dao = dao;
		this.useCache = useCache;
		setUseQueryCache(useQueryCache);
		setUsePredicatedQueryCache(usePredicatedQueryCache);
		this.cache = new LockableLRUCache<>();
		context.attach(dao);
		context.registerRepository(dao.entityClass(), this);
	}

	public void init() {
		if (lockManager == null)
			lockManager = new LockableHashMap<>();
		if (useCache)
			initCache(cache);
		if (queryCache != null)
			initCache(queryCache);
		if (predicatedQueryCache != null)
			initCache(predicatedQueryCache);
	}

	private void initCache(LockableCache cache) {
		cache.setCheckExpiryExecutor(context.getCacheExpirer());
		cache.setExpiryPeriod(CACHE_EXPIRY_PERIOD);
		cache.setCheckExpiryPeriod(CACHE_CHECK_EXPIRY_PERIOD);
		cache.startBackgroundExpiry();
	}

	public DomainContext getContext() {
		return context;
	}

	public Class<E> entityClass() {
		return entityClass;
	}

	@Override public String getEntityName() {
		return entityClass.getName();
	}

	public EntityDAO<I, E> getDAO() {
		return dao;
	}

	public boolean isUseCache() {
		return useCache;
	}

	public void setUseCache(boolean useCache) {
		this.useCache = useCache;
	}

	public boolean isUseQueryCache() {
		return queryCache != null;
	}

	public void setUseQueryCache(boolean useQueryCache) {
		if (useQueryCache) {
			if (queryCache == null)
				queryCache = new LockableLRUCache<>();
		}
		else
			queryCache = null;
	}

	public boolean isUsePredicatedQueryCache() {
		return predicatedQueryCache != null;
	}

	public void setUsePredicatedQueryCache(boolean usePredicatedQueryCache) {
		if (usePredicatedQueryCache) {
			if (predicatedQueryCache == null)
				predicatedQueryCache = new LockableLRUCache<>();
		}
		else
			predicatedQueryCache = null;
	}

	public int getCacheCapacity() {
		return cache.getCapacity();
	}

	public void setCacheCapacity(int capacity) {
		cache.setCapacity(capacity);
	}

	public int getQueryCacheCapacity() {
		return queryCache != null ? queryCache.getCapacity() : -1;
	}

	public void setQueryCacheCapacity(int capacity) {
		if (queryCache != null)
			queryCache.setCapacity(capacity);
	}

	public int getPredicatedQueryCacheCapacity() {
		return predicatedQueryCache != null ? predicatedQueryCache.getCapacity() : -1;
	}

	public void setPredicatedQueryCacheCapacity(int capacity) {
		if (predicatedQueryCache != null)
			predicatedQueryCache.setCapacity(capacity);
	}

	public long getCacheExpiryPeriod() {
		return cache.getExpiryPeriod();
	}

	public void setCacheExpiryPeriod(long expiryPeriod) {
		cache.setExpiryPeriod(expiryPeriod);
		if (queryCache != null)
			queryCache.setExpiryPeriod(expiryPeriod);
		if (predicatedQueryCache != null)
			predicatedQueryCache.setExpiryPeriod(expiryPeriod);
	}

	public long getCacheCheckExpiryPeriod() {
		return cache.getCheckExpiryPeriod();
	}

	public void setCacheCheckExpiryPeriod(long checkExpiryPeriod) {
		cache.setCheckExpiryPeriod(checkExpiryPeriod);
		if (queryCache != null)
			queryCache.setCheckExpiryPeriod(checkExpiryPeriod);
		if (predicatedQueryCache != null)
			predicatedQueryCache.setCheckExpiryPeriod(checkExpiryPeriod);
	}

	public LockManager<I> getLockManager() {
		return lockManager;
	}

	public void setLockManager(LockManager<I> lockManager) {
		this.lockManager = lockManager;
	}


	// Querying

	@Override public boolean exists(I id) {
		return find(id) != null;
	}

	@Override public E find(I id) {
		if (useCache) {
			E entity = cache.lockedGet(id, this::fetch);
			return entity != null ? entity.lazyDeepClone() : null;
		}
		else
			return fetch(id);
	}

	@Override public E get(I id) {
		E entity = find(id);
		if (entity == null)
			throw new NotFoundException(entityClass, id);
		return entity;
	}

	private E fetch(I id) {
		E entity = dao.fetch(id);
		context.attach(entity);
		return entity;
	}

	@Override public boolean exists(Query query) {
		return find(query) != null;
	}

	@Override public E find(final Query query) {
		LockableCache<Query, List<I>> queryCache = getQueryCache(query);
		if (queryCache != null) {
			final Reference<E> entityRef = new Reference<>();
			List<I> result = queryCache.lockedGet(query, key -> {
				E entity = fetch(query);
				if (entity != null) {
					entityRef.set(entity);
					if (useCache)
						cache.lockedPut(entity.getId(), entity);
					List<I> ids = new ArrayList<>(1);
					ids.add(entity.getId());
					return ids;
				}
				else
					return new ArrayList<>(0);
			});
			E entity = entityRef.get();
			if (entity != null)
				return entity.lazyDeepClone();
			else
				return !result.isEmpty() ? find(result.get(0)) : null;
		}
		else
			return fetch(query);
	}

	@Override public E get(Query query) {
		E entity = find(query);
		if (entity == null)
			throw new NotFoundException(entityClass, query);
		return entity;
	}

	private E fetch(Query query) {
		E entity = dao.fetch(query);
		context.attach(entity);
		return entity;
	}

	@Override public List<E> getList(final Query query) {
		LockableCache<Query, List<I>> queryCache = getQueryCache(query);
		if (queryCache != null) {
			return new EntityReferenceList<>(context, entityClass,
				new ArrayList<>(queryCache.lockedGet(query, key -> {
					List<E> entities = dao.fetchList(query);
					List<I> ids = new ArrayList<>(entities.size());
					for (E entity : entities) {
						context.attach(entity);
						I id = entity.getId();
						if (useCache) //TODO Rethink to put only if absent
							cache.lockedPut(id, entity);
						ids.add(id);
					}
					return ids;
				}))
			);
		}
		else {
			List<E> entities = dao.fetchList(query);
			context.attach(entities);
			return entities;
		}
	}

	@Override public void iterate(Query query, final Consumer<E> callback) {
		dao.fetch(query, entity -> {
			context.attach(entity);
			callback.accept(entity);
		});
	}

	@Override public <D> List<D> getDetails(String detailName, I id, FieldAccessor<E, List<D>> accessor) {
		if (useCache && accessor != null) {
			cache.lock(id);
			try {
				E cachedEntity = cache.get(id);
				if (cachedEntity != null) {
					List<D> details = accessor.get(cachedEntity);
					if (details == null) {
						details = fetchDetails(detailName, id);
						accessor.set(cachedEntity, details);
					}
					return cloneList(details);
				}
			}
			finally {
				cache.unlock(id);
			}
		}
		return fetchDetails(detailName, id);
	}

	private <D> List<D> fetchDetails(String detailName, I id) {
		List<D> details = dao.fetchDetailList(detailName, id);
		context.attach(details);
		return details;
	}

	private LockableCache<Query, List<I>> getQueryCache(Query query) {
		if (query.isCached()) {
			if (predicatedQueryCache != null && query instanceof PredicatedQuery)
				return (LockableCache)predicatedQueryCache;
			else
				return queryCache;
		}
		return null;
	}


	// Transacting

	@Override public void create(E entity) {
		I id = entity.getId();
		if (id == null) {
			dao.create(entity);
			addEntity(entity, false);
		}
		else {
			cache.lock(id);
			try {
				dao.create(entity);
				I newId = entity.getId();
				if (!id.equals(newId))
					throw new IllegalStateException("Trying to create already created entity."); 
				addEntity(entity, true);
			}
			finally {
				cache.unlock(id);
			}
		}
		// Evict queries after cache key-level locking to avoid deadlocks
		// as cache key-level lock happens inside query cache key-level lock
		evictFromQueries(entity);
	}

	@Override public void create(Iterable<E> entities) {
		List<I> lockedIds = new ArrayList<>();
		try {
			for (E entity : entities) {
				I id = entity.getId();
				if (id != null) {
					cache.lock(id);
					lockedIds.add(id);
				}
			}
			dao.create(entities);
			for (E entity : entities)
				addEntity(entity, lockedIds.contains(entity.getId()));
		}
		finally {
			for (I id : lockedIds)
				cache.unlock(id);
		}
		evictFromQueries(entities);
	}

	@Override public void create(Iterable<E> entities, Query query) {
		create(entities);
		addQuery(query, entities);
	}

	@Override public void save(E entity) throws OptimisticLockingException {
		I id = entity.getId();
		if (id == null) {
			dao.create(entity);
			addEntity(entity, false);
			evictFromQueries(entity);
		}
		else
			lockedSave(entity);
	}

	private void lockedSave(E entity) {
		boolean changed;
		I id = entity.getId();
		cache.lock(id);
		try {
			E oldEntity = find(id);
			changed = dao.save(entity, oldEntity);
			addEntity(entity, true);
		}
		finally {
			cache.unlock(id);
		}
		if (changed)
			evictFromQueries(entity);
	}

	@Override public void save(E entity, E optLockedEntity) throws OptimisticLockingException {
		boolean changed = false;
		I id = entity.getId();
		if (id == null || !context.isAttached(entity)) {
			dao.create(entity);
			addEntity(entity, false);
			changed = true;
		}
		else {
			cache.lock(id);
			try {
				E oldEntity = find(id);
				boolean hasBeenChanged = false;
				if (oldEntity != null && optLockedEntity != null) {
					oldEntity.loadDetails();
					hasBeenChanged = !optLockedEntity.equalsByValue(oldEntity);
				}
				if (!hasBeenChanged) {
					changed = dao.save(entity, oldEntity);
					addEntity(entity, true);
				}
				else if (!entity.equalsByValue(oldEntity))
					throw new OptimisticLockingException(entityClass, id);
			}
			finally {
				cache.unlock(id);
			}
		}
		if (changed)
			evictFromQueries(entity);
	}

	@Override public void save(Iterable<E> entities) throws OptimisticLockingException {
		save(entities, null);
	}

	@Override public void save(Iterable<E> entities, Iterable<E> oldEntities) throws OptimisticLockingException {
		Collection<E> changed = null;
		List<E> forCreate = new ArrayList<>();
		List<E> forSave = new ArrayList<>();
		List<E> oldForSave = new ArrayList<>();
		List<I> forDelete = new ArrayList<>();
		if (oldEntities != null) {
			for (E oldEntity : oldEntities)
				forDelete.add(oldEntity.getId());
		}
		List<I> lockedIds = new ArrayList<>();
		try {
			for (E entity : entities) {
				I id = entity.getId();
				if (id == null || !context.isAttached(entity))
					forCreate.add(entity);
				else {
					cache.lock(id);
					lockedIds.add(id);
					E oldEntity = oldEntities != null ? findIn(oldEntities, id) : null;
					if (oldEntity == null)
						oldEntity = find(id);
					if (oldEntity == null || !entity.equalsByValue(oldEntity)) {
						forSave.add(entity);
						oldForSave.add(oldEntity);
					}
				}
				forDelete.remove(entity.getId());
			}
			for (I id : forDelete) {
				cache.lock(id);
				lockedIds.add(id);
			}
			if (!forCreate.isEmpty())
				dao.create(forCreate);
			if (!forSave.isEmpty())
				changed = dao.save(forSave, oldForSave);
			if (!forDelete.isEmpty())
				dao.delete(forDelete);
			for (E entity : forCreate)
				addEntity(entity, false);
			for (E entity : forSave)
				addEntity(entity, true);
			for (I id : forDelete)
				cache.remove(id);
		}
		finally {
			for (I id : lockedIds)
				cache.unlock(id);
		}
		if (!forCreate.isEmpty())
			evictFromQueries(forCreate);
		if (changed != null && !changed.isEmpty())
			evictFromQueries(changed);
		if (!forDelete.isEmpty())
			evictFromQueries(forDelete);
	}

	@Override public void save(Iterable<E> entities, Iterable<E> oldEntities, Query query) throws OptimisticLockingException {
		save(entities, oldEntities);
		addQuery(query, entities);
	}

	private E findIn(Iterable<E> entities, I id) {
		for (E entity : entities) {
			if (Objects.equals(entity.getId(), id))
				return entity;
		}
		return null;
	}

	private void addEntity(E entity, boolean alreadyLocked) {
		context.attach(entity);
		if (useCache) {
			if (alreadyLocked)
				cache.put(entity.getId(), entity.deepClone());
			else
				cache.lockedPut(entity.getId(), entity.deepClone());
		}
	}

	private void addQuery(Query query, Iterable<E> entities) {
		LockableCache<Query, List<I>> queryCache = getQueryCache(query);
		if (queryCache != null)
			queryCache.lockedPut(query, DomainContext.getIds(entities));
	}

	@Override public void delete(I id) {
		dao.delete(id);
		evict(id);
		evictFromQueries(id);
	}

	@Override public void delete(E entity, E optLockedEntity) throws OptimisticLockingException {
		I id = entity.getId();
		E oldEntity = find(id);
		boolean hasBeenChanged = false;
		if (oldEntity != null && optLockedEntity != null) {
			oldEntity.loadDetails();
			hasBeenChanged = !optLockedEntity.equalsByValue(oldEntity);
		}
		if (!hasBeenChanged)
			delete(id);
		else
			throw new OptimisticLockingException(entityClass, id);
	}

	@Override public void deleteAll() {
		dao.deleteAll();
		evictAll();
	}


	// Pessimistic Locking

	@Override public void lock(I id) {
		lockManager.lock(id);
	}

	@Override public void lockInterruptibly(I id) throws InterruptedException {
		lockManager.lockInterruptibly(id);
	}

	@Override public boolean tryLock(I id) {
		return lockManager.tryLock(id);
	}

	@Override public boolean tryLock(I id, long timeout, TimeUnit unit) throws InterruptedException {
		return lockManager.tryLock(id, timeout, unit);
	}

	@Override public void unlock(I id) {
		lockManager.unlock(id);
	}

	@Override public boolean isLocked(I id) {
		return lockManager.isLocked(id);
	}

	@Override public E lockedUpdate(I id, Consumer<E> callback) throws OptimisticLockingException {
		lockManager.lock(id);
		try {
			return doLockedUpdate(id, callback);
		}
		finally {
			lockManager.unlock(id);
		}
	}

	protected E doLockedUpdate(I id, Consumer<E> callback) {
		E entity = get(id);
		callback.accept(entity);
		lockedSave(entity);
		return entity;
	}


	// Evicting

	@Override public void evict(I id) {
		if (useCache)
			cache.lockedRemove(id);
	}

	@Override public void evict(Query query) {
		LockableCache<Query, List<I>> queryCache = getQueryCache(query);
		if (queryCache != null)
			queryCache.lockedRemove(query);
	}

	@Override public void evictEntities() {
		if (useCache)
			cache.tryClear();
	}

	@Override public void evictQueries() {
		if (queryCache != null)
			queryCache.tryClear();
		if (predicatedQueryCache != null)
			predicatedQueryCache.tryClear();
	}

	@Override public void evictAll() {
		evictEntities();
		evictQueries();
	}

	private void evictFromQueries(final I id) {
		Transaction tran = TransactionManager.getTransaction();
		if (tran != null) {
			tran.registerSynchronization(new TransactionSynchronizationAdapter() {
				@Override public void afterCompletion(TransactionStatus status) {
					doEvictFromQueries(id);
				}
			});
		}
		else
			doEvictFromQueries(id);
	}

	private void evictFromQueries(final Collection<I> ids) {
		Transaction tran = TransactionManager.getTransaction();
		if (tran != null) {
			tran.registerSynchronization(new TransactionSynchronizationAdapter() {
				@Override public void afterCompletion(TransactionStatus status) {
					doEvictFromQueries(ids);
				}
			});
		}
		else
			doEvictFromQueries(ids);
	}

	private void doEvictFromQueries(I id) {
		if (queryCache != null)
			evictFromQueryCache(queryCache, id);
		if (predicatedQueryCache != null)
			evictFromQueryCache((LockableCache)predicatedQueryCache, id);
	}

	private void doEvictFromQueries(Collection<I> ids) {
		if (queryCache != null)
			evictFromQueryCache(queryCache, ids);
		if (predicatedQueryCache != null)
			evictFromQueryCache((LockableCache)predicatedQueryCache, ids);
	}

	private void evictFromQueryCache(LockableCache<Query, List<I>> queryCache, I id) {
		for (Map.Entry<Query, List<I>> queryEntry : queryCache.entrySetSnapshot()) {
			Query query = queryEntry.getKey();
			queryCache.lock(query);
			try {
				queryEntry.getValue().remove(id);
			}
			finally {
				queryCache.unlock(query);
			}
		}
	}

	private void evictFromQueryCache(LockableCache<Query, List<I>> queryCache, Collection<I> ids) {
		for (Map.Entry<Query, List<I>> queryEntry : queryCache.entrySetSnapshot()) {
			Query query = queryEntry.getKey();
			queryCache.lock(query);
			try {
				queryEntry.getValue().removeAll(ids);
			}
			finally {
				queryCache.unlock(query);
			}
		}
	}

	private void evictFromQueries(final E entity) {
		Transaction tran = TransactionManager.getTransaction();
		if (tran != null) {
			tran.registerSynchronization(new TransactionSynchronizationAdapter() {
				@Override public void afterCompletion(TransactionStatus status) {
					doEvictFromQueries(entity);
				}
			});
		}
		else
			doEvictFromQueries(entity);
	}

	private void evictFromQueries(final Iterable<E> entities) {
		Transaction tran = TransactionManager.getTransaction();
		if (tran != null) {
			tran.registerSynchronization(new TransactionSynchronizationAdapter() {
				@Override public void afterCompletion(TransactionStatus status) {
					doEvictFromQueries(entities);
				}
			});
		}
		else
			doEvictFromQueries(entities);
	}

	private void doEvictFromQueries(E entity) {
		if (queryCache != null)
			queryCache.tryClear();
		if (predicatedQueryCache != null) {
			for (Map.Entry<PredicatedQuery, List<I>> queryEntry : predicatedQueryCache.entrySetSnapshot()) {
				PredicatedQuery query = queryEntry.getKey();
				predicatedQueryCache.lock(query);
				try {
					List<I> result = queryEntry.getValue();
					Comparator<I> comparator = getQueryComparator(query, result, entity, null);
					sortOrInvalidateQuery(query, result, comparator);
				}
				finally {
					predicatedQueryCache.unlock(query);
				}
			}
		}
	}

	private void doEvictFromQueries(Iterable<E> entities) {
		if (queryCache != null)
			queryCache.tryClear();
		if (predicatedQueryCache != null) {
			for (Map.Entry<PredicatedQuery, List<I>> queryEntry : predicatedQueryCache.entrySetSnapshot()) {
				PredicatedQuery query = queryEntry.getKey();
				predicatedQueryCache.lock(query);
				try {
					List<I> result = queryEntry.getValue();
					Comparator<I> comparator = null;
					for (E entity : entities)
						comparator = getQueryComparator(query, result, entity, comparator);
					sortOrInvalidateQuery(query, result, comparator);
				}
				finally {
					predicatedQueryCache.unlock(query);
				}
			}
		}
	}

	private static final Comparator MARK_FOR_REMOVE = Collections.reverseOrder();

	private Comparator<I> getQueryComparator(PredicatedQuery query, List<I> result, E entity, Comparator<I> comparator) {
		I id = entity.getId();
		if (query.test(entity)) {
			if (!result.contains(id)) {
				if (query.isSorted()) {
					if (comparator == null)
						comparator = query.getIdComparator(this);
					if (comparator != null)
						result.add(id);
					else
						comparator = MARK_FOR_REMOVE;
				}
				else
					result.add(id);
			}
		}
		else
			result.remove(id);
		return comparator;
	}

	private void sortOrInvalidateQuery(PredicatedQuery query, List<I> result, Comparator<I> comparator) {
		if (comparator != null) {
			if (comparator == MARK_FOR_REMOVE)
				predicatedQueryCache.remove(query);
			else
				Collections.sort(result, comparator);
		}
	}


	// Statistics

	public int getCachedEntityCount() {
		return cache.size();
	}

	public int getCachedQueryCount() {
		return queryCache != null ? queryCache.size() : 0;
	}

	public int getCachedPredicatedQueryCount() {
		return predicatedQueryCache != null ? predicatedQueryCache.size() : 0;
	}

	public Map<String, Object> getEntityCacheStatistics() {
		return getCacheStatistics(cache);
	}

	public Map<String, Object> getQueryCacheStatistics() {
		return queryCache != null ? getCacheStatistics(queryCache) : null;
	}

	public Map<String, Object> getPredicatedQueryCacheStatistics() {
		return predicatedQueryCache != null ? getCacheStatistics(predicatedQueryCache) : null;
	}

	private Map<String, Object> getCacheStatistics(LockableCache cache) {
		HashMap<String, Object> statsMap = new HashMap<>();
		CacheStatistics stats = cache.getStatistics();
		statsMap.put("Size", stats.size());
		statsMap.put("Capacity", stats.capacity());
		statsMap.put("FillRatio", stats.fillRatio());
		statsMap.put("Gets", stats.gets());
		statsMap.put("Hits", stats.hits());
		statsMap.put("HitRatio", stats.hitRatio());
		statsMap.put("LockCount", cache.lockedKeySetSnapshot().size());
		return statsMap;
	}

	public void resetStatistics() {
		cache.resetStatistics();
		if (queryCache != null)
			queryCache.resetStatistics();
		if (predicatedQueryCache != null)
			predicatedQueryCache.resetStatistics();
	}
}
