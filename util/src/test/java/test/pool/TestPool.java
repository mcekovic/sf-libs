package test.pool;

import org.strangeforest.pool.*;

public class TestPool extends ResourcePool<PooledObject> {

	private boolean trace;

	public TestPool(int initialPoolSize, int minPoolSize, int maxPoolSize, boolean trace) {
		super(initialPoolSize, minPoolSize, maxPoolSize);
		this.trace = trace;
		setResourceManager(new PooledObjectManager(trace));
	}

	public PoolableObject getObject() {
		if (trace)
			System.out.println("Resource getting.");
		return new PoolableObject(this, getResource());
	}

	void returnObject(PooledObject obj) {
		returnResource(obj, false);
		if (trace)
			System.out.println("Resource returned.");
	}

	private static class PooledObjectManager implements ResourceManager<PooledObject> {

		private boolean trace;

		private PooledObjectManager(boolean trace) {
			super();
			this.trace = trace;
		}

		public PooledObject allocateResource() throws PoolException {
			try {
				if (trace)
					System.out.println("Resource allocated.");
				return new PooledObject();
			}
			catch (Throwable ex) {
				throw new PoolException("Error allocating new object.", ex);
			}
		}

		public void releaseResource(PooledObject resource) {
			if (trace)
				System.out.println("Resource released.");
		}

		public boolean checkResource(PooledObject resource) {
			return true;
		}
	}
}
