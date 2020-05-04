package model.async.lockableDataStructures;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Adds explicit locking functionality to java.util.ConcurrentHashMap
 */
public class LockableConcurrentHashMap<K, V> extends ConcurrentHashMap<K, V> {

    private ReentrantLock lock = new ReentrantLock();

    public void lock() {
        this.lock.lock();
    }

    public void unlock() {
        this.lock.unlock();
    }

    @Override
    public V get(Object key) {
        if (lock.isHeldByCurrentThread()) {
            return super.get(key);
        } else {
            throw new RuntimeException("Current thread does not hold lock. Cannot call LockableConcurrentHashMap.get()");
        }
    }

    @Override
    public V put(K key, V value) {
        if (lock.isHeldByCurrentThread()) {
            return super.put(key, value);
        } else {
            throw new RuntimeException("Current thread does not hold lock. Cannot call LockableConcurrentHashMap.put()");
        }
    }

    @Override
    public boolean containsKey(Object key) {
        if (lock.isHeldByCurrentThread()) {
            return super.containsKey(key);
        } else {
            throw new RuntimeException("Current thread does not hold lock. " +
                    "Cannot call LockableConcurrentHashMap.containsKey()");
        }
    }
}
