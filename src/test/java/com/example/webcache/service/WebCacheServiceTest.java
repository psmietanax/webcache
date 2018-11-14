package com.example.webcache.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WebCacheServiceTest {

	@Test(expected = IllegalArgumentException.class)
	public void testNonValidCacheInstance() {
		new WebCacheService(0);
	}

	@Test
	public void testAddScalarElement() {
		// GIVEN
		WebCacheService webCacheService = new WebCacheService(5);

		// WHEN
		Object value1 = new Object();
		Object value2 = new Object();
		Object value3 = new Object();
		webCacheService.put("TEST1", value1);
		webCacheService.put("TEST2", value2);
		webCacheService.put("TEST3", value3);

		// THEN
		Assert.assertEquals(3, webCacheService.getCacheStatistics().getSize());

		Assert.assertNotNull(webCacheService.get("TEST1"));
		Assert.assertEquals(value1, webCacheService.get("TEST1").getValue());

		Assert.assertNotNull(webCacheService.get("TEST2"));
		Assert.assertEquals(value2, webCacheService.get("TEST2").getValue());

		Assert.assertNotNull(webCacheService.get("TEST3"));
		Assert.assertEquals(value3, webCacheService.get("TEST3").getValue());
	}

	@Test
	public void testAddKeyValuedElement() {
		// GIVEN
		WebCacheService webCacheService = new WebCacheService(5);

		// WHEN
		LinkedHashMap<String, Object> value1 = new LinkedHashMap<>();
		value1.put("test_1_1", "value_1_1");
		value1.put("test_1_2", "value_1_2");

		LinkedHashMap<String, Object> value2 = new LinkedHashMap<>();
		value2.put("test_2_1", "value_2_1");
		value2.put("test_2_2", "value_2_2");
		value2.put("test_2_3", "value_2_3");

		webCacheService.put("TEST1", value1);
		webCacheService.put("TEST2", value2);

		// THEN
		Assert.assertEquals(2, webCacheService.getCacheStatistics().getSize());

		Assert.assertNotNull(webCacheService.get("TEST1"));
		Assert.assertEquals(value1, webCacheService.get("TEST1").getValue());

		Assert.assertNotNull(webCacheService.get("TEST2"));
		Assert.assertEquals(value2, webCacheService.get("TEST2").getValue());
	}

	@Test
	public void testAddListElement() {
		// GIVEN
		WebCacheService webCacheService = new WebCacheService(5);

		// WHEN
		List<Integer> value1 = Arrays.asList(1, 2, 3);
		List<Integer> value2 = Arrays.asList(4, 5);

		webCacheService.put("TEST1", value1);
		webCacheService.put("TEST2", value2);

		// THEN
		Assert.assertEquals(2, webCacheService.getCacheStatistics().getSize());

		Assert.assertNotNull(webCacheService.get("TEST1"));
		Assert.assertEquals(value1, webCacheService.get("TEST1").getValue());

		Assert.assertNotNull(webCacheService.get("TEST2"));
		Assert.assertEquals(value2, webCacheService.get("TEST2").getValue());
	}

	@Test
	public void testRemoveElement() {
		// GIVEN
		WebCacheService webCacheService = new WebCacheService(5);

		// WHEN
		webCacheService.put("TEST1", new Object());
		webCacheService.put("TEST2", new Object());
		webCacheService.put("TEST1", null);

		// THEN
		Assert.assertEquals(1, webCacheService.getCacheStatistics().getSize());

		Assert.assertNull(webCacheService.get("TEST1"));

		Assert.assertNotNull(webCacheService.get("TEST2"));
	}

	@Test
	public void testCacheLRUEviction() {
		// GIVEN
		WebCacheService webCacheService = new WebCacheService(5);

		// WHEN
		webCacheService.put("TEST1", new Object());
		webCacheService.put("TEST2", new Object());
		webCacheService.put("TEST3", new Object());
		webCacheService.put("TEST4", new Object());
		webCacheService.put("TEST5", new Object());
		Object value = new Object();
		webCacheService.put("TEST6", value);

		// THEN
		Assert.assertEquals(5, webCacheService.getCacheStatistics().getSize());
		Assert.assertNull(webCacheService.get("TEST1"));

		Assert.assertEquals(value, webCacheService.get("TEST6").getValue());
	}

	@Test
	public void testValueVersioning() {
		// GIVEN
		WebCacheService webCacheService = new WebCacheService(5);

		// WHEN
		webCacheService.put("TEST", new Object());
		String versionId = webCacheService.get("TEST").getVersionId();
		webCacheService.put("TEST", new Object());

		// THEN
		Assert.assertNotEquals(versionId, webCacheService.get("TEST").getVersionId());
	}

	@Test
	public void testConcurrentPutDifferentKeys() throws InterruptedException {
		// GIVEN
		// cache capacity
		int N = 500_000;
		// number of running threads
		int T = 4;
		// number of elements each thread will add
		int M = 100_000;

		WebCacheService webCacheService = new WebCacheService(N);

		// WHEN
		CountDownLatch startLatch = new CountDownLatch(T);
		CountDownLatch endLatch = new CountDownLatch(T);

		// Run two threads
		ExecutorService executorService = Executors.newFixedThreadPool(T);
		for (int i = 0; i < T; i++) {
			int idx = i;
			executorService.submit(() -> {
				startLatch.await();
				for (int j = 0; j<M; j++) {
					webCacheService.put(M * idx + j, new Object());
				}
				endLatch.countDown();
				return null;
			});
			startLatch.countDown();
		}

		endLatch.await();

		// THEN
		Assert.assertEquals(Math.min(M * T, N), webCacheService.getCacheStatistics().getSize());
	}

	@Test
	public void testConcurrentPutSameKeys() throws InterruptedException {
		// GIVEN
		// cache capacity
		int N = 500_000;
		// number of running threads
		int T = 4;
		// number of elements each thread will add
		int M = 100_000;

		WebCacheService webCacheService = new WebCacheService(N);

		// WHEN
		CountDownLatch startLatch = new CountDownLatch(T);
		CountDownLatch endLatch = new CountDownLatch(T);

		// Run two threads
		ExecutorService executorService = Executors.newFixedThreadPool(T);
		for (int i = 0; i < T; i++) {
			executorService.submit(() -> {
				startLatch.await();
				for (int j = 0; j<M; j++) {
					webCacheService.put(j % 10, new Object());
				}
				endLatch.countDown();
				return null;
			});
			startLatch.countDown();
		}

		endLatch.await();

		// THEN
		Assert.assertEquals(10, webCacheService.getCacheStatistics().getSize());
	}

	@Test
	public void testConcurrentStatisticsHits() throws InterruptedException {
		// GIVEN
		// cache capacity
		int N = 500_000;
		// number of running threads
		int T = 4;
		// number of elements each thread will check if value exists
		int M = 100_000;

		WebCacheService webCacheService = new WebCacheService(N);
		webCacheService.put("TEST", new Object());

		// WHEN
		CountDownLatch startLatch = new CountDownLatch(T);
		CountDownLatch endLatch = new CountDownLatch(T);

		// Run two threads
		ExecutorService executorService = Executors.newFixedThreadPool(T);
		for (int i = 0; i < T; i++) {
			executorService.submit(() -> {
				startLatch.await();
				for (int j = 0; j<M; j++) {
					webCacheService.get("TEST");
				}
				endLatch.countDown();
				return null;
			});
			startLatch.countDown();
		}

		endLatch.await();

		// THEN
		Assert.assertEquals(T * M, webCacheService.getCacheStatistics().getHits());
	}

	@Test
	public void testConcurrentStatisticsMisses() throws InterruptedException {
		// GIVEN
		// cache capacity
		int N = 500_000;
		// number of running threads
		int T = 4;
		// number of elements each thread will check if value exists
		int M = 100_000;

		WebCacheService webCacheService = new WebCacheService(N);

		// WHEN
		CountDownLatch startLatch = new CountDownLatch(T);
		CountDownLatch endLatch = new CountDownLatch(T);

		// Run two threads
		ExecutorService executorService = Executors.newFixedThreadPool(T);
		for (int i = 0; i < T; i++) {
			executorService.submit(() -> {
				startLatch.await();
				for (int j = 0; j<M; j++) {
					webCacheService.get("TEST");
				}
				endLatch.countDown();
				return null;
			});
			startLatch.countDown();
		}

		endLatch.await();

		// THEN
		Assert.assertEquals(T * M, webCacheService.getCacheStatistics().getMisses());
	}
}
