package org.strangeforest.orm;

public abstract class DomainService {

	protected DomainService(DomainContext context) {
		super();
		context.registerService(this);
	}

	protected Class<?> getServiceClass() {
		return getClass();
	}
}