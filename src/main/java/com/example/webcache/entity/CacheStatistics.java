package com.example.webcache.entity;

import java.io.Serializable;

/**
 * This class consists of cache statistics - cache hits, misses and size.
 * It's used to display these values on the front-end.
 */
public class CacheStatistics implements Serializable {
	private final long hits;
	private final long misses;
	private final long size;

	public CacheStatistics(long hits, long misses, long size) {
		this.hits = hits;
		this.misses = misses;
		this.size = size;
	}

	public long getHits() {
		return hits;
	}

	public long getMisses() {
		return misses;
	}

	public long getSize() {
		return size;
	}
}
