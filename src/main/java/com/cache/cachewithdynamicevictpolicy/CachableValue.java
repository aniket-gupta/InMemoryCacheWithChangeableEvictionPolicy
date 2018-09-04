package com.cache.cachewithdynamicevictpolicy;

public class CachableValue<V> {
	
	private V value;
	private long creationTime;
	private long lastAccessedTime;
	private long ttl;
	private int accessFreq;
	
	public CachableValue(V v, long ttlInMilli) {
		this.value = v;
		this.ttl = ttlInMilli;
		this.creationTime = this.lastAccessedTime = System.currentTimeMillis();
		this.accessFreq = 1;
	}

	public V getValue() {
		return value;
	}

	public void setValue(V value) {
		this.value = value;
	}

	public long getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(long creationTime) {
		this.creationTime = creationTime;
	}

	public long getLastAccessedTime() {
		return lastAccessedTime;
	}

	public void setLastAccessedTime(long lastAccessedTime) {
		this.lastAccessedTime = lastAccessedTime;
	}

	public long getTtl() {
		return ttl;
	}

	public void setTtl(long ttl) {
		this.ttl = ttl;
	}

	public int getAccessFreq() {
		return accessFreq;
	}

	public void setAccessFreq(int accessFreq) {
		this.accessFreq = accessFreq;
	}
	
	public V useValue() {
		this.lastAccessedTime = System.currentTimeMillis();
		this.accessFreq++;
		return value;
	}
	
	

}
