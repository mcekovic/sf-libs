package test.pool;

public class PooledObject {

	private final int id;

	private static int nextId;

	public PooledObject() {
		synchronized(this) {
			id = nextId++;
		}
	}

	@Override public String toString() {
		return "Resource: " + String.valueOf(id);
	}
}
