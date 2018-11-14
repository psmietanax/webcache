package com.example.webcache.service;

import com.example.webcache.entity.CacheStatistics;
import com.example.webcache.entity.VersionedValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * A LRU cache service.
 * This class sets up a cache service with a given size.
 * It's backed by the LinkedHashMap that is responsible for cache storage and eviction.
 * This implementation supports concurrent access for reading and writing keys and is thread-safe.
 */
@Service
public class WebCacheService {
	private static Logger LOGGER = LoggerFactory.getLogger(WebCacheService.class);

	private final Map<Object, VersionedValue> cache;
	private final Lock readLock;
	private final Lock writeLock;
	private final AtomicLong misses;
	private final AtomicLong hits;

	public WebCacheService(@Value("${webcache.maxSize}") int maxSize) {
		if (maxSize < 1) {
			throw new IllegalArgumentException("WebCache max size must be > 0.");
		}
		LOGGER.debug("Creating a new WebCache instance with size {}", maxSize);
		this.cache = new LinkedHashMap<Object, VersionedValue>(maxSize, 0.75f, true) {
			@Override
			protected boolean removeEldestEntry(Map.Entry<Object, VersionedValue> eldest) {
				return size() > maxSize;
			}
		};
		ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
		this.readLock = readWriteLock.readLock();
		this.writeLock = readWriteLock.writeLock();
		this.hits = new AtomicLong(0);
		this.misses = new AtomicLong(0);
	}

	public VersionedValue put(Object key, Object value) {
		writeLock.lock();
		try {
			// remove an entry if given value is null
			if (value == null) {
				LOGGER.debug("Removing an entry with the key {}", key);
				return cache.remove(key);
			}
			LOGGER.debug("Updating an entry with the key {}", key);
			return cache.put(key, new VersionedValue(value));
		} finally {
			writeLock.unlock();
		}
	}

	public VersionedValue get(Object key) {
		LOGGER.debug("Getting an entry with the key {}", key);
		readLock.lock();
		try {
			VersionedValue value = cache.get(key);
			if (value == null) {
				misses.getAndIncrement();
			} else {
				hits.getAndIncrement();
			}
			return value;
		} finally {
			readLock.unlock();
		}
	}

	public CacheStatistics getCacheStatistics() {
		writeLock.lock();
		try {
			return new CacheStatistics(hits.get(), misses.get(), cache.size());
		} finally {
			writeLock.unlock();
		}
	}

}

