package com.cache.cachewithdynamicevictpolicy;

import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Cache<K, V> {

	private static final int DEFAULT_MAX_SIZE = 50;

	private final HashMap<K, CachableValue<V>> cache;
	private final int maxSize;

	private CachePolicy<K, V> cahcePolicy;

	private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

	public Cache(int size) {
		if (size < 0)
			throw new IllegalArgumentException("size : " + size);
		this.maxSize = size;
		this.cache = new HashMap<K, CachableValue<V>>(size);
		Thread cleanUpThread = new Thread(new CleanupTask<K, V>(this));
		cleanUpThread.setDaemon(true);
		cleanUpThread.start();
	}

	public Cache() {
		this(DEFAULT_MAX_SIZE);
	}

	public V get(K key) {
		lock.readLock().lock();
		try {
			CachableValue<V> cachableValue = cache.get(key);
			if (cachableValue == null)
				return null;
			if (cachableValue.getTtl() != -1) { // -1 = lifetime
				if (System.currentTimeMillis() < cachableValue.getLastAccessedTime() + cachableValue.getTtl()) {
					cache.remove(key);
					cahcePolicy.remove(key);
					return null;
				}
			}

			cachableValue.useValue();
			return cahcePolicy.get(key);
		} finally {
			lock.readLock().unlock();
		}
	}

	public V put(K key, V value, long ttlInMillis) {
		lock.writeLock().lock();
		try {
			if (cache.size() == maxSize) {
				K evictedKey = cahcePolicy.evictKey();
				cache.remove(evictedKey);
			}
			CachableValue<V> cachableValue = new CachableValue<V>(value, ttlInMillis);
			cache.put(key, cachableValue);
			cahcePolicy.put(key, value);
			return value;
		} finally {
			lock.writeLock().unlock();
		}
	}

	public void remove(K key) {
		lock.writeLock().lock();
		try {
			cache.remove(key);
			cahcePolicy.remove(key);
		} finally {
			lock.writeLock().unlock();
		}
	}

	public void setCachePolicy(CachePolicy<K, V> newCachePolicy) {
		lock.writeLock().lock();
		try {
			if (newCachePolicy != null) {
				newCachePolicy.buildFrom(cache);
				this.cahcePolicy = newCachePolicy;
			}
		} finally {
			lock.writeLock().unlock();
		}
	}

	public void cleanup() {
		Set<K> keySet = cache.keySet();

		for (K key : keySet) {
			CachableValue<V> cacheValue = cache.get(key);

			synchronized (cache) {
				if (cacheValue != null && cacheValue.getTtl() != -1) {
					long lsstAccessTime = cacheValue.getLastAccessedTime();
					long ttl = cacheValue.getTtl();

					if (System.currentTimeMillis() < lsstAccessTime + ttl) {
						this.remove(key);
						cahcePolicy.remove(key);
						Thread.yield();
					}
				}
			}

		}
	}

}
