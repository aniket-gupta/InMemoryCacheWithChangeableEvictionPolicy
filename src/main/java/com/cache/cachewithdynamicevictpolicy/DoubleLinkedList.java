package com.cache.cachewithdynamicevictpolicy;

public class DoubleLinkedList<K,V> {
	
	private int n;
    private Node<K,V> head;
    private Node<K,V> tail;

    public void add(Node<K,V> node) {
        if (head == null) {
            head = node;
        } else {
            tail.next = node;
            node.pre = tail;
        }
        tail = node;
        n++;
    }

    public void remove(Node<K, V> node) {

        if (node.next == null) tail = node.pre;
        else node.next.pre = node.pre;

        if (head.key == node.key) head = node.next;
        else node.pre.next = node.next;

        n--;
    }
    

    public Node<K, V> head() {
        return head;
    }

    public int size() {
        return n;
    }

}
