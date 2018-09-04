package com.cache.cachewithdynamicevictpolicy;

import java.util.concurrent.TimeUnit;

public final class CleanupTask<K, V> implements Runnable {
	
	private final Cache<K, V> cache;
	
	public CleanupTask(Cache<K, V> cache) {
		this.cache = cache;
	}

	@Override
	public void run() {
		while(true){
            try {
                TimeUnit.SECONDS.sleep(1);
                cache.cleanup();
            }
            
            catch(InterruptedException ie){
               System.err.println("Error: " + ie);
            }
        }

	}

}
