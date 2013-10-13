package test.pool;

public class PoolableObject {

	private TestPool pool;
	private PooledObject obj;

	PoolableObject(TestPool pool, PooledObject obj) {
		super();
		this.pool = pool;
		this.obj = obj;
	}

	public void close() {
		pool.returnObject(obj);
		obj = null;
	}

	@Override protected void finalize() throws Throwable {
		close();
		super.finalize();
	}
}
