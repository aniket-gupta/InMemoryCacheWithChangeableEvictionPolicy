package com.cache.cachewithdynamicevictpolicy;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Set;


public class CachePolicyFIFO<K, V> implements CachePolicy<K, V> {

	private FIFOMap<K, V> map;

	public CachePolicyFIFO(int maxSize) {
		this.map = new FIFOMap<>(maxSize);
	}

	@Override
	public K evictKey() {
		for(K key : map.keySet()) {
			return key;
		}
		return null;

	}

	@Override
	public void buildFrom(Map<K, CachableValue<V>> cache) {
		if (!cache.isEmpty()) {
			Set<Entry<K, CachableValue<V>>> entrySet = cache.entrySet();
			PriorityQueue<HeapNode<K, V>> pq = new PriorityQueue<HeapNode<K, V>>();
			for (Entry<K, CachableValue<V>> entry : entrySet) {
				K key = entry.getKey();
				CachableValue<V> value = entry.getValue();
				long time = value.getCreationTime();
				pq.offer(new HeapNode<K, V>(key, value.getValue(), time));
			}

			while (!pq.isEmpty()) {
				HeapNode<K, V> n = pq.poll();
				map.put(n.k, n.v);
			}
		}

	}

	@Override
	public V get(K key) {
		return map.get(key);
	}

	@Override
	public void put(K key, V value) {
		map.put(key, value);

	}

	@Override
	public void remove(K key) {
		map.remove(key);

	}
	

	private static class FIFOMap<K, V> extends LinkedHashMap<K, V> {

		private static final long serialVersionUID = 1L;
		private final int maxSize;

		public FIFOMap(int maxSize) {
			this.maxSize = maxSize;
		}

		@Override
		protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
			return size() > maxSize;
		}

		@Override
		public boolean equals(Object obj) {
			return obj == this;
		}

		@Override
		public int hashCode() {
			return System.identityHashCode(this);
		}

	}
	
	private static class HeapNode<K, V> implements Comparable<HeapNode<K, V>> {

		K k;
		V v;
		long accesTime;

		public HeapNode(K k, V v, long time) {
			this.k = k;
			this.v = v;
			this.accesTime = time;
		}

		public int compareTo(HeapNode<K, V> o) {

			return new Long(this.accesTime).compareTo(new Long(o.accesTime));
		}

	}

}
