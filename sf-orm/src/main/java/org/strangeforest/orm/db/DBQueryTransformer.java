package org.strangeforest.orm.db;

import org.strangeforest.db.gateway.*;
import org.strangeforest.orm.*;

public abstract class DBQueryTransformer implements QueryTransformer, SQLTransformer {

	public boolean isStatementPoolable() {
		return false;
	}


	// Object methods

	@Override public boolean equals(Object o) {
		if (this == o) return true;
		return o != null && getClass() == o.getClass();
	}

	@Override public int hashCode() {
		return getClass().hashCode();
	}
}
