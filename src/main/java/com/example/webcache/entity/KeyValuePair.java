package com.example.webcache.entity;

import java.io.Serializable;

/**
 * A class that represents a key-value pair that is provided
 * by a client in order to add a new entry to the cache.
 */
public class KeyValuePair implements Serializable {
	private Object key;
	private Object value;

	public Object getKey() {
		return key;
	}

	public void setKey(Object key) {
		this.key = key;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}
}
