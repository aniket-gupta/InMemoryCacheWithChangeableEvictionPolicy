package com.cache.cachewithdynamicevictpolicy;

import java.util.Map;

public interface CachePolicy<K,V> {
	
	public V get(K key);
	
	public void put(K key, V value);
	
	public void remove(K key);
	
	public K evictKey();
	
	public void buildFrom(Map<K, CachableValue<V>> map);

}
