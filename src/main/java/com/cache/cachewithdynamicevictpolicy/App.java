package com.cache.cachewithdynamicevictpolicy;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        Cache<Integer, Integer> cache = new Cache<>(2);
        cache.setCachePolicy(new CachePolicyLRU<>());
        
        cache.put(1, 1, -1);
        cache.put(2, 2, -1);
        System.out.println( cache.get(1));       // returns 1
        cache.put(3, 3, -1);    // evicts key 2
        System.out.println( cache.get(2));       // returns -1 (not found)
        cache.put(4, 4, -1);    // evicts key 1
        System.out.println( cache.get(1));       // returns -1 (not found)
        System.out.println( cache.get(3));       // returns 3
        System.out.println( cache.get(4));  // return 4
        System.out.println( cache.get(3));       // returns 3
        System.out.println( cache.get(4));
        System.out.println( cache.get(4));
        System.out.println( cache.get(4));
        
        cache.setCachePolicy(new CachePolicyLFU<>());
        System.out.println();
        System.out.println( cache.get(1));       // returns -1 (not found)
        cache.put(2, 2, -1);
        System.out.println( cache.get(3));       // returns 3
        System.out.println( cache.get(2));
        
        
    }
}
