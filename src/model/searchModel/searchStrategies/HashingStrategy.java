package model.searchModel.searchStrategies;

import model.async.FutureUtil.FutureCollection;
import model.async.lockableDataStructures.LockableConcurrentHashMap;
import model.async.threadPool.AppThreadPool;
import util.Progress;
import util.ScanException;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * An abstract class for search strategies that use hashing to identify duplicates
 */
public abstract class HashingStrategy implements ISearchStrategy {

    private ConcurrentLinkedQueue<Future> taskFutures;
    private LockableConcurrentHashMap<String, LinkedList<File>> duplicates;

    private long totalInitialFileCount;                                                                                 // total number of files that will be scanned
    private long startTime;                                                                                             // epoch time for when last search was started

    /**
     * Create a hashcode for the given file
     * @param file file to hash
     * @return a unique hashcode as a String
     * @throws Exception if file cannot be hashed
     */
    abstract String hash(File file) throws Exception;

    /**
     * {@inheritDoc}
     */
    @Override
    public Progress getProgress() throws ScanException {
        if (this.taskFutures == null) {
            throw new ScanException("No futures have been created. Search may not have started.");
        }

        long done = countDoneFutures(this.taskFutures);
        long remaining = this.totalInitialFileCount - done;
        long eta = calculateEta(done, remaining, this.startTime);
        long duplicatesCount = countValuesWithMultipleItems(this.duplicates.values());

        return new Progress(done,-1,remaining, duplicatesCount, eta, null, null);
    }

    /**
     * Get a count of the number of Future objects that are complete
     * @param futures a collection of future objects to get count from
     * @return count of Future objects that are done
     */
    private static long countDoneFutures(Collection<Future> futures) {
        long done = 0;
        for (Future future: futures) {
            if (future.isDone()) {
                done++;
            }
        }
        return done;
    }

    /**
     * Calculate a time-based ETA for the current search using the number of tasks done and the number of tasks remaining
     * Assumption: on average tasks take similar amount of time
     * @param done number of tasks that are complete
     * @param remaining number of tasks that remain
     * @param startTime epoch time when the search was started
     * @return estimated time remaining for search in milliseconds
     */
    private static long calculateEta(long done, long remaining, long startTime) {
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - startTime;
        double rate = elapsedTime*1. / done;
        return Math.round(remaining*rate);
    }

    /**
     * Count the number inner collections with multiple items
     * @param collection a collection to get count from. Inner values must extend Collection
     * @return a count of the number of values that have multiple items
     */
    private static long countValuesWithMultipleItems(Collection<? extends Collection> collection) {
        long count = 0;
        for (Collection<?> value: collection) {
            if (value.size() > 1) {
                count++;
            }
        }
        return count;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Future<List<List<File>>> findDuplicates(List<File> allFiles) {
        duplicates = new LockableConcurrentHashMap<>();
        this.taskFutures = new ConcurrentLinkedQueue<>();
        this.totalInitialFileCount = allFiles.size();
        this.startTime = System.currentTimeMillis();

        for (File file: allFiles) {
            Hasher task = new Hasher(file, duplicates);
            Future taskFuture = AppThreadPool.getInstance().submit(task);
            taskFutures.add(taskFuture);
        }

        return new FutureCollection<List<List<File>>>(taskFutures) {
            @Override
            public List<List<File>> get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException {

                for (Future future: this.futures) {
                    future.get();
                }

                LinkedList<List<File>> filtered = new LinkedList<>();
                for (List<File> duplicateSet: duplicates.values()) {
                    if (duplicateSet.size() > 1) {
                        filtered.add(duplicateSet);
                    }
                }
                return filtered;
            }
        };
    }

    /**
     * Asynchronously hashes files using HashingStrategy.hash() and inserts them into a results HashMap
     */
    class Hasher implements Runnable {

        private final File file;
        private final LockableConcurrentHashMap<String, LinkedList<File>> results;

        /**
         * Create Hasher object
         * @param file file to hash
         * @param results map to store results into
         */
        Hasher(File file, LockableConcurrentHashMap<String, LinkedList<File>> results) {
            this.file = file;
            this.results = results;
        }

        /**
         * Hashes and inserts file into results map
         */
        @Override
        public void run() {
            if (Thread.interrupted()) {
                return;
            }
            try {
                String key = hash(this.file);
                this.results.lock();
                insertFileIntoResults(key, this.file, this.results);
            } catch (Exception e) {
                // TODO: exception handling
            } finally {
                this.results.unlock();
            }
        }

        /**
         * Inserts file into map with the given key. If key already has associated values, adds this file to the
         * collection, else creates a new collection to add the file.
         * @param key key associated with this file
         * @param file file to insert
         * @param results results map to insert into
         */
        private void insertFileIntoResults(String key, File file,
                                                  LockableConcurrentHashMap<String, LinkedList<File>> results) {
            if (!results.containsKey(key)) {
                results.put(key, new LinkedList<>());
            }
            addFileToExistingSet(key, file, results);
        }

        /**
         * Adds given file into results map using the given key. Assumes that a collection already exists associated
         * to the given key - will add the given file to the existing collection.
         * @param key key associated with this file
         * @param file file to insert
         * @param results results map to insert into
         */
        private void addFileToExistingSet(String key, File file,
                                                 LockableConcurrentHashMap<String, LinkedList<File>> results) {
            LinkedList<File> existingSet = results.get(key);
            existingSet.add(file);
            results.put(key, existingSet);
        }

    }
}
