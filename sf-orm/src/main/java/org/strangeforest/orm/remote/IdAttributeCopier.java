package org.strangeforest.orm.remote;

import org.strangeforest.orm.*;

public class IdAttributeCopier<I, E extends Entity<I>> extends AttributeCopier<E> {

	@Override public void copyAttributes(E from, E to) {
		to.setId(from.getId());
	}
}
