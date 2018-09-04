package com.cache.cachewithdynamicevictpolicy;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

public class CachePolicyLFU<K, V> implements CachePolicy<K, V> {

	private Map<K, Node<K, V>> values = new HashMap<K, Node<K, V>>();
	private Map<K, Integer> counts = new HashMap<K, Integer>();
	private TreeMap<Integer, DoubleLinkedList<K, V>> frequencies = new TreeMap<Integer, DoubleLinkedList<K, V>>();

	public V get(K key) {
		if (!values.containsKey(key)) {
			return null;
		}
		Node<K, V> node = values.get(key);
		int frequency = counts.get(key);
		frequencies.get(frequency).remove(node);
		removeIfListEmpty(frequency);
		frequencies.computeIfAbsent(frequency + 1, k -> new DoubleLinkedList<K, V>()).add(node);

		counts.put(key, frequency + 1);
		return values.get(key).value;

	}

	public void put(K key, V value) {
		if (!values.containsKey(key)) {

			Node<K, V> node = new Node<K, V>(key, value);
			values.put(key, node);
			counts.put(key, 1);
			frequencies.computeIfAbsent(1, k -> new DoubleLinkedList<>()).add(node); // starting frequency = 1

		}
	}
	
	public void buildFrom(Map<K, CachableValue<V>> cache) {
		if(!cache.isEmpty()) {
			Set<Entry<K, CachableValue<V>>> entrySet = cache.entrySet();
			for(Entry<K, CachableValue<V>> entry : entrySet) {
				K k = entry.getKey();
				CachableValue<V> value = entry.getValue();
				V v = value.getValue();
				int accessFreq = value.getAccessFreq();
				Node<K, V> node = new Node<K, V>(k, v);
				values.put(k, node);
				counts.put(k, accessFreq);
				frequencies.computeIfAbsent(accessFreq, i -> new DoubleLinkedList<>()).add(node);
				
			}
		}

	}

	
	private void removeIfListEmpty(int frequency) {
        if (frequencies.get(frequency).size() == 0) {
            frequencies.remove(frequency);  // remove from map if list is empty
        }
    }


	public void remove(K key) {
		Node<K, V> nodeToDelete = values.get(key);
		Integer count = counts.get(key);
		DoubleLinkedList<K, V> doubleLinkedList = frequencies.get(count);
		doubleLinkedList.remove(nodeToDelete);
		removeIfListEmpty(count);
		values.remove(key);
        counts.remove(key);
	}

	public K evictKey() {
		int lowestCount = frequencies.firstKey();   // smallest frequency
        Node<K, V> nodeTodelete = frequencies.get(lowestCount).head(); // first item (LRU)
        frequencies.get(lowestCount).remove(nodeTodelete);

        K keyToDelete = nodeTodelete.key();
        removeIfListEmpty(lowestCount);
        values.remove(keyToDelete);
        counts.remove(keyToDelete);
        return keyToDelete;

	}

	
}
