package com.example.webcache.controller;

import com.example.webcache.entity.CacheStatistics;
import com.example.webcache.entity.KeyValuePair;
import com.example.webcache.entity.VersionedValue;
import com.example.webcache.service.WebCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

/**
 * This class is responsible for handling webcache's REST API requests.
 */
@RestController
public class WebcacheController {

	@Autowired
	private WebCacheService webCacheService;

	@GetMapping(value = "/cache/entries", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public VersionedValue getValue(@RequestParam Object key) {
		VersionedValue versionedValue = webCacheService.get(key);
		if (versionedValue == null) {
			throw new NoSuchElementException();
		}
		return versionedValue;
	}

	@PutMapping(name = "/cache/entries", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public VersionedValue putValue(@RequestBody KeyValuePair keyValuePair) {
		if (keyValuePair.getKey() == null && keyValuePair.getValue() == null) {
			throw new IllegalArgumentException();
		}
		return webCacheService.put(keyValuePair.getKey(), keyValuePair.getValue());
	}

	@GetMapping(value = "/cache/stats", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public CacheStatistics getCacheStatistics() {
		return webCacheService.getCacheStatistics();
	}

	@ExceptionHandler(NoSuchElementException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public void handleNoSuchElementException() {
		// returns 404 status
	}

	@ExceptionHandler(IllegalArgumentException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public void handleIllegalArgumentException() {
		// returns 400 status
	}
}

