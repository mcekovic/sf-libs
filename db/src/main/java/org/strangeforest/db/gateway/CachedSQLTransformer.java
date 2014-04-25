package org.strangeforest.db.gateway;

public interface CachedSQLTransformer extends SQLTransformer {

	String cacheKey();
}
