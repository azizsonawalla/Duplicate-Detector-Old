package model.async.lockableDataStructures;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Adds explicit locking functionality to java.util.ConcurrentHashMap
 */
public class LockableConcurrentHashMap<K, V> extends ConcurrentHashMap<K, V> {

    private ReentrantLock lock = new ReentrantLock();

    /**
     * Acquires the lock. Will wait until lock is free.
     */
    public void lock() {
        this.lock.lock();
    }

    /**
     * Releases the lock if held by the current thread.
     */
    public void unlock() {
        this.lock.unlock();
    }

    /**
     * If lock is held by current thread, returns the object associated with the given key
     * @param key key to retrieve object
     * @return object associated with key
     * @throws RuntimeException if current thread does not hold the lock. Must call lock() first.
     */
    @Override
    public V get(@NotNull Object key) {
        if (lock.isHeldByCurrentThread()) {
            return super.get(key);
        } else {
            throw new RuntimeException("Current thread does not hold lock. Cannot call LockableConcurrentHashMap.get()");
        }
    }

    /**
     * If lock is held by the current thread, puts the given key-value pair into the hashmap
     * @param key key for the object
     * @param value value associated with the given key
     * @return the previous value associated with the key, or null if no value associated previously
     * @throws RuntimeException if current thread does not hold the lock. Must call lock() first
     */
    @Override
    public V put(@NotNull K key, @NotNull V value) {
        if (lock.isHeldByCurrentThread()) {
            return super.put(key, value);
        } else {
            throw new RuntimeException("Current thread does not hold lock. Cannot call LockableConcurrentHashMap.put()");
        }
    }

    /**
     * If lock is held by the current thread, returns true if there is a value associated with the given key, else false
     * @param key key for the object
     * @return true if the key is mapped to a value, else false
     * @throws RuntimeException if current thread does not hold the lock. Must call lock() first
     */
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
