package org.strangeforest.orm.db;

import java.util.*;
import java.util.function.*;
import javax.sql.*;

import org.apache.commons.lang3.builder.*;
import org.strangeforest.db.gateway.*;
import org.strangeforest.orm.*;
import org.strangeforest.util.*;

//TODO Add support for eager fetching of entities (eager fetching of details is supported through projections)
public class DBEntityDAO<I, E extends DomainEntity<I, E>> extends ORMDBDAO implements EntityDAO<I, E>, DomainContextAware {

	private final Class<E> entityClass;
	protected EntityMapper<I, E> mapper;
	protected DomainContext context;
	private boolean checkVersion;
	private Map<String, DBDetailDAO<I, ?>> detailDAOs;
	private Map<String, Supplier<? extends ObjectReader<E>>> projectors;
	private List<SaveOptimizer<I, E>> saveOptimizers;

	public DBEntityDAO(Class<E> entityClass, EntityMapper<I, E> mapper, DataSource dataSource) {
		this(entityClass, dataSource);
		this.mapper = mapper;
	}

	public DBEntityDAO(Class<E> entityClass, DataSource dataSource) {
		super(dataSource, entityClass);
		this.entityClass = entityClass;
	}

	@Override public Class<E> entityClass() {
		return entityClass;
	}

	public EntityMapper<I, E> getMapper() {
		return mapper;
	}

	protected void setMapper(EntityMapper<I, E> mapper) {
		this.mapper = mapper;
	}

	@Override public DomainContext getContext() {
		return context;
	}

	@Override public void setContext(DomainContext context) {
		this.context = context;
	}

	public boolean isCheckVersion() {
		return checkVersion;
	}

	public void setCheckVersion(boolean checkVersion) {
		this.checkVersion = checkVersion;
	}


	// Basic CRUD

	@Override public E fetch(I id) {
		return db.fetchOne("Fetch", id, mapper);
	}

	@Override public E fetch(Query query) {
		return db.fetchOne(getQuerySQLName(query), getReader(query), getSQLTransformer(query), QueryStatementPreparer.createStatementPreparer(query));
	}

	@Override public List<E> fetchList(Query query) {
		return db.fetchList(getQuerySQLName(query), getReader(query), getSQLTransformer(query), QueryStatementPreparer.createStatementPreparer(query));
	}

	@Override public void fetch(Query query, final Consumer<E> callback) {
		final ObjectReader<E> reader = getReader(query);
		db.executeQuery(getQuerySQLName(query), getSQLTransformer(query), QueryStatementPreparer.createStatementPreparer(query), rs -> {
			E entity = reader.read(rs);
			if (entity != null)
				callback.accept(entity);
		});
	}

	private SQLTransformer getSQLTransformer(Query query) {
		QueryTransformer transformer = query.getTransformer();
		return transformer instanceof SQLTransformer ? (SQLTransformer)transformer : null;
	}

	@Override public <D> List<D> fetchDetailList(String detailName, I id) {
		return (List<D>)getDetailDAO(detailName).fetch(id);
	}

	@Override public void create(E entity) {
		db.createEntity("Create", entity, mapper);
		createDetails(entity);
	}

	@Override public void create(Iterable<E> entities) {
		for (E entity : entities)
			create(entity);
	}

	@Override public boolean save(E entity, E oldEntity) {
		boolean changed = false;
		try {
			if (oldEntity == null || !shallowEqualsByValue(entity, oldEntity)) {
				checkAndIncVersion(entity, oldEntity);
				changed = true;
				doSaveEntity(entity, oldEntity);
			}
			saveDetails(entity, oldEntity);
		}
		catch (Throwable th) {
			if (changed)
				entity.dec_version();
			throw ExceptionUtil.throwIt(th);
		}
		return changed;
	}

	@Override public Collection<E> save(Iterable<E> entities, Iterable<E> oldEntities) {
		List<E> forSave = new ArrayList<>();
		List<E> oldForSave = saveOptimizers != null ? new ArrayList<>() : null;
		for (Iterator<E> iter = entities.iterator(), oldIter = oldEntities.iterator(); iter.hasNext();) {
			E entity = iter.next();
			E oldEntity = oldIter.next();
			if (oldEntity == null || !shallowEqualsByValue(entity, oldEntity)) {
				checkAndIncVersion(entity, oldEntity);
				forSave.add(entity);
				if (oldForSave != null)
					oldForSave.add(oldEntity);
			}
		}
		//TODO Use save optimizers for batch saves too
		if (isBatchedSave() && forSave.size() > 1)
			db.saveEntities("Save", forSave, mapper);
		else {
			if (oldForSave == null) {
				for (E entity : forSave)
					db.saveEntity("Save", entity, mapper);
			}
			else {
				for (Iterator<E> iter = forSave.iterator(), oldIter = oldForSave.iterator(); iter.hasNext();)
					doSaveEntity(iter.next(), oldIter.next());
			}
		}
		try {
			for (Iterator<E> iter = entities.iterator(), oldIter = oldEntities.iterator(); iter.hasNext();)
				saveDetails(iter.next(), oldIter.next());
		}
		catch (Throwable th) {
			for (E entity : forSave)
				entity.dec_version();
			throw ExceptionUtil.throwIt(th);
		}
		return forSave;
	}

	private void doSaveEntity(E entity, E oldEntity) {
		SaveOptimizer<I, E> optimizer = getSaveOptimizer(entity, oldEntity);
		if (optimizer == null)
			db.saveEntity("Save", entity, mapper);
		else
			db.saveEntity("Save" + optimizer.getName(), entity, optimizer.getMapper());
	}

	private void checkAndIncVersion(DomainEntity<I, E> entity, DomainEntity<I, E> oldEntity) {
		if (checkVersion && oldEntity != null && entity.get_version() != oldEntity.get_version())
			throw new OptimisticLockingException(entityClass, entity.getId(), entity.get_version(), oldEntity.get_version());
		entity.inc_version();
	}

	@Override public void delete(I id) {
		db.deleteEntity("Delete", id, mapper);
	}

	@Override public void delete(Iterable<I> ids) {
		for (I id : ids)
			delete(id);
	}

	@Override public int deleteAll() {
		return db.executeUpdate("DeleteAll");
	}

	protected void createDetails(E entity) {}
	protected void saveDetails(E entity, E oldEntity) {}
	protected void deleteDetails(E entity) {}

	public boolean isBatchedSave() {
		return true;
	}


	// Utilities

	private boolean shallowEqualsByValue(E entity1, E entity2) {
		return EqualsBuilder.reflectionEquals(entity1, entity2, excludeFromShallowEquals());
	}

	private static final String[] EXCLUDE_VERSION = {EntityDAO._VERSION};

	protected String[] excludeFromShallowEquals() {
		return EXCLUDE_VERSION;
	}

	private String getQuerySQLName(Query query) {
		return "Fetch" + query.getName();
	}

	protected <I2, E2 extends DomainEntity<I2, E2>> EntityMapper<I2, E2> getMapper(Class<E2> entityClass) {
		return ((DBEntityDAO<I2, E2>)((LocalRepository<I2, E2>)context.getRepository(entityClass)).getDAO()).getMapper();
	}

	void registerDetailDAO(String detailName, DBDetailDAO<I, ?> detailDAO) {
		if (detailDAOs == null)
			detailDAOs = new HashMap<>();
		detailDAOs.put(detailName, detailDAO);
	}

	private DBDetailDAO<I, ?> getDetailDAO(String detailName) {
		return detailDAOs != null ? detailDAOs.get(detailName) : null;
	}

	protected void registerProjector(String projection, ObjectReader<E> projector) {
		registerProjector(projection, new SingletonSupplier<>(projector));
	}

	protected void registerProjector(String projection, Supplier<? extends ObjectReader<E>> projectorFactory) {
		if (projectors == null)
			projectors = new HashMap<>();
		projectors.put(projection, projectorFactory);
	}

	private ObjectReader<E> getReader(Query query) {
		String projection = query.getProjection();
		if (projection == null)
			return mapper;
		else {
			Supplier<? extends ObjectReader<E>> projectorFactory = projectors != null ? projectors.get(projection) : null;
			if (projectorFactory == null)
				throw new IllegalStateException(String.format("Unknown projection '%1$s' for entity '%2$s'.", projection, entityClass.getName()));
			return projectorFactory.get();
		}
	}

	protected void registerSaveOptimizer(String name, EntityMapper<I, E> mapper, String[] optimizationFields) {
		if (saveOptimizers == null)
			saveOptimizers = new ArrayList<>();
		saveOptimizers.add(new SaveOptimizer<>(this, name, mapper, optimizationFields));
	}

	private SaveOptimizer<I, E> getSaveOptimizer(E entity, E oldEntity) {
		if (saveOptimizers != null) {
			for (SaveOptimizer<I, E> optimizer : saveOptimizers) {
				if (optimizer.canOptimize(entity, oldEntity))
					return optimizer;
			}
		}
		return null;
	}
}
