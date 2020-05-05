package model.searchModel.searchStrategies;

import model.async.FutureUtil.FutureCollection;
import model.async.lockableDataStructures.LockableConcurrentHashMap;
import model.async.threadPool.AppThreadPool;
import model.util.Progress;
import model.util.SearchException;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Finds duplicate files based on file metadata
 */
public class MetadataStrategy implements ISearchStrategy {

    private LinkedList<Future> taskFutures;                                                                             // TODO: maybe use thread safe Queue?
    private int totalFileCount;
    private long startTime;                                                                                             // epoch time for when last search was started

    @Override
    public Progress getProgress() throws SearchException {
        int done = 0;                                                                                                   // TODO: add check for if search has started
        for (Future future: this.taskFutures) {
            if (future.isDone()) {
                done++;
            }
        }
        int remaining = this.totalFileCount - done;

        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - this.startTime;
        long rate = elapsedTime / done;                                                                                 // We lose decimal accuracy here but for our purposes it doesn't matter
        long eta = remaining*rate;

        return new Progress(done,-1,remaining, eta, -1, null, null);                // TODO: add support for remaining stats
    }

    @Override
    public Future<List<List<File>>> findDuplicates(List<File> allFiles) {                                               // TODO: javadoc
        LockableConcurrentHashMap<String, LinkedList<File>> duplicates = new LockableConcurrentHashMap<>();
        this.taskFutures = new LinkedList<>();
        this.totalFileCount = allFiles.size();
        this.startTime = System.currentTimeMillis();

        for (File file: allFiles) {
            MetadataHasher task = new MetadataHasher(file, duplicates);
            Future taskFuture = AppThreadPool.getInstance().submit(task);
            taskFutures.add(taskFuture);
        }

        return new FutureCollection<List<List<File>>>(taskFutures) {
            @Override
            public List<List<File>> get(long timeout, TimeUnit unit)
                    throws InterruptedException, ExecutionException, TimeoutException {

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
            } finally {                                                                                                 // TODO: exception handling
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
