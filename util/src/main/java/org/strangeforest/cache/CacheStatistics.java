package org.strangeforest.cache;

public class CacheStatistics {

	private final int size;
	private final int capacity;
	private final long gets;
	private final long hits;

	public CacheStatistics(int size, int capacity, long gets, long hits) {
		super();
		this.size = size;
		this.capacity = capacity;
		this.gets = gets;
		this.hits = hits;
	}

	public int size() {
		return size;
	}

	public int capacity() {
		return capacity;
	}

	/**
	 * Returns current cache fill ratio (size/capacity).
	 * @return fill ratio.
	 */
	public float fillRatio() {
		return capacity > 0 ? ((float)size)/capacity : 0.0f;
	}

	public long gets() {
		return gets;
	}

	public long hits() {
		return hits;
	}

	/**
	 * Returns current cache hit ratio (number of hits/number of gets).
	 * @return hit ratio.
	 */
	public float hitRatio() {
		return gets != 0L ? ((float)hits)/gets : 0.0f;
	}
}
