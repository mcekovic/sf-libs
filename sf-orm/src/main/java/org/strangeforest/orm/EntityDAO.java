package org.strangeforest.orm;

import java.util.*;
import java.util.function.*;

public interface EntityDAO<I, E extends Entity<I>> {

	Class<E> entityClass();

	E fetch(I id);
	E fetch(Query query);
	List<E> fetchList(Query query);
	void fetch(Query query, Consumer<E> callback);
	<D> List<D> fetchDetailList(String detailName, I id);

	void create(E entity);
	void create(Iterable<E> entities);

	boolean save(E entity, E oldEntity);
	Collection<E> save(Iterable<E> entities, Iterable<E> oldEntities);

	void delete(I id);
	void delete(Iterable<I> ids);
	int deleteAll();

	String _VERSION = "_version";
}
