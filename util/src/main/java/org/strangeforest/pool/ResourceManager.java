package org.strangeforest.pool;

/**
 * Interface used by <tt>ResourcePool</tt> for managing resources.
 */
public interface ResourceManager<R> {

	R allocateResource() throws PoolException;
	void releaseResource(R resource) throws PoolException;
	boolean checkResource(R resource);
}
