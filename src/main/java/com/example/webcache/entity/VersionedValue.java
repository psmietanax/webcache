package com.example.webcache.entity;

import java.io.Serializable;
import java.util.UUID;

/**
 * A class that represents an output from the cache returned to a client.
 * It consists of a version id - a unique version number - and a cached value.
 */
public class VersionedValue implements Serializable {
	private final String versionId;
	private final Object value;

	public VersionedValue(Object value) {
		this.value = value;
		this.versionId = UUID.randomUUID().toString();
	}

	public String getVersionId() {
		return versionId;
	}

	public Object getValue() {
		return value;
	}
}
