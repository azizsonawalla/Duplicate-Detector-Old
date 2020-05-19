package model.searchModel.searchStrategies;

import model.async.FutureUtil.FutureCollection;
import model.async.lockableDataStructures.LockableConcurrentHashMap;
import model.async.threadPool.AppThreadPool;
import model.util.Progress;
import model.util.ScanException;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Finds duplicate files based on file metadata.
 *
 * Definition of duplicate file: files with the same name and size
 */
public class MetadataStrategy implements ISearchStrategy {

    private ConcurrentLinkedQueue<Future> taskFutures;
    private LockableConcurrentHashMap<String, LinkedList<File>> duplicates;

    private long totalInitialFileCount;                                                                                 // total number of files that will be scanned
    private long startTime;                                                                                             // epoch time for when last search was started

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

        return new Progress(done,-1,remaining, duplicatesCount, eta, null, null);                                       // TODO: add support for remaining stats
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
    public Future<List<List<File>>> findDuplicates(List<File> allFiles) {                                               // TODO: javadoc
        duplicates = new LockableConcurrentHashMap<>();
        this.taskFutures = new ConcurrentLinkedQueue<>();
        this.totalInitialFileCount = allFiles.size();
        this.startTime = System.currentTimeMillis();

        for (File file: allFiles) {
            MetadataHasher task = new MetadataHasher(file, duplicates);
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

    static class MetadataHasher implements Runnable {                                                                   // TODO: Javadoc

        private final File file;
        private final LockableConcurrentHashMap<String, LinkedList<File>> duplicates;

        MetadataHasher(File file, LockableConcurrentHashMap<String, LinkedList<File>> duplicates) {
            this.file = file;
            this.duplicates = duplicates;
        }

        @Override
        public void run() {
            if (Thread.interrupted()) {
                return;
            }
            String key = getNameSizeHash(this.file);
            try {
                this.duplicates.lock();
                if (this.duplicates.containsKey(key)) {
                    LinkedList<File> existingSet = this.duplicates.get(key);
                    existingSet.add(this.file);
                    this.duplicates.put(key, existingSet);
                } else {
                    LinkedList<File> newFileSet = new LinkedList<>();
                    newFileSet.add(this.file);
                    this.duplicates.put(key, newFileSet);
                }
            } catch (Exception e) {
                // TODO: exception handling
            } finally {
                this.duplicates.unlock();
            }
        }

        /**
         * Creates a hashcode for the file using its name and size
         */
        private static String getNameSizeHash(File file) {
            long nameHash = file.getName().hashCode();
            long size = file.length();
            return String.format("%s_%s", nameHash, Long.toString(size));
        }
    }
}
