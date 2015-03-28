package org.strangeforest.orm;

public interface DomainContextAware {

	DomainContext getContext();
	void setContext(DomainContext context);
}
