package com.cache.cachewithdynamicevictpolicy;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Set;

public class CachePolicyLRU<K, V> implements CachePolicy<K, V> {

	Map<K, Node<K, V>> map;
	Node<K, V> head;
	Node<K, V> end;

	public CachePolicyLRU() {
		this.map = new HashMap<K, Node<K, V>>();
	}

	public V get(K key) {
		if (map.containsKey(key)) {
			Node<K, V> n = map.get(key);
			remove(n);
			setHead(n);
			return n.value;
		}

		return null;
	}

	public void put(K key, V value) {

		if (map.containsKey(key)) {
			Node<K, V> old = map.get(key);
			old.value = value;
			remove(old);
			setHead(old);
		} else {
			Node<K, V> created = new Node<K, V>(key, value);
			map.put(key, created);
			setHead(created);
		}
	}

	public void remove(K key) {
		if (map.containsKey(key)) {
			Node<K, V> n = map.get(key);
			remove(n);
			map.remove(key);
		}

	}

	public K evictKey() {
		if (end != null) {
			K key = end.key;
			map.remove(end.key);
			remove(end);
			return key;
		}
		return null;
	}

	public void buildFrom(Map<K, CachableValue<V>> cache) {
		if (!cache.isEmpty()) {
			Set<Entry<K, CachableValue<V>>> entrySet = cache.entrySet();
			PriorityQueue<HeapNode<K, V>> pq = new PriorityQueue<CachePolicyLRU.HeapNode<K, V>>();
			for (Entry<K, CachableValue<V>> entry : entrySet) {
				K key = entry.getKey();
				CachableValue<V> value = entry.getValue();
				long lastAccessedTime = value.getLastAccessedTime();
				pq.offer(new HeapNode<K, V>(key, value.getValue(), lastAccessedTime));
			}

			while (!pq.isEmpty()) {
				HeapNode<K, V> n = pq.poll();
				Node<K, V> created = new Node<K, V>(n.k, n.v);
				map.put(n.k, created);
				setHead(created);
			}
		}
	}

	private void remove(Node<K, V> n) {
		if (n.pre != null) {
			n.pre.next = n.next;
		} else {
			head = n.next;
		}

		if (n.next != null) {
			n.next.pre = n.pre;
		} else {
			end = n.pre;
		}

	}

	private void setHead(Node<K, V> n) {
		n.next = head;
		n.pre = null;

		if (head != null)
			head.pre = n;

		head = n;

		if (end == null)
			end = head;
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

			return new Long(o.accesTime).compareTo(new Long(this.accesTime));
		}

	}

}
