package org.strangeforest.orm.db;

import java.util.*;

import org.apache.commons.lang3.builder.*;
import org.strangeforest.orm.*;

class SaveOptimizer<I, E extends Entity<I>> {

	private final String name;
	private final EntityMapper<I, E> mapper;
	private final String[] excludeFields;

	public SaveOptimizer(DBEntityDAO entityDAO, String name, EntityMapper<I, E> mapper, String[] optimizationFields) {
		super();
		this.name = name;
		this.mapper = mapper;
		this.excludeFields = concatenate(entityDAO.excludeFromShallowEquals(), optimizationFields);
	}

	public String getName() {
		return name;
	}

	public EntityMapper<I, E> getMapper() {
		return mapper;
	}

	public boolean canOptimize(Entity entity, Entity oldEntity) {
		return EqualsBuilder.reflectionEquals(entity, oldEntity, excludeFields);
	}

	private String[] concatenate(String[] a1, String[] a2) {
		int length = a1.length + a2.length;
		List<String> l = new ArrayList<>(length);
		l.addAll(Arrays.asList(a1));
		l.addAll(Arrays.asList(a2));
		return l.toArray(new String[length]);
	}
}
