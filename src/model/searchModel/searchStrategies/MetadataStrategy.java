package model.searchModel.searchStrategies;

import model.async.FutureUtil.FutureCollection;
import model.async.lockableDataStructures.LockableConcurrentHashMap;
import model.async.threadPool.AppThreadPool;
import model.util.Progress;
import model.util.ScanException;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Finds duplicate files based on file metadata
 */
public class MetadataStrategy implements ISearchStrategy {

    private LinkedList<Future> taskFutures;                                                                             // TODO: maybe use thread safe Queue?
    private LockableConcurrentHashMap<String, LinkedList<File>> duplicates;

    private long totalInitialFileCount;                                                                                 // total number of files that will be scanned
    private long startTime;                                                                                             // epoch time for when last search was started

    @Override
    public Progress getProgress() throws ScanException {
        int done = 0;                                                                                                   // TODO: add check for if search has started // TODO: break into helpers
        for (Future future: this.taskFutures) {
            if (future.isDone()) {
                done++;
            }
        }
        long remaining = this.totalInitialFileCount - done;

        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - this.startTime;
        double rate = elapsedTime*1. / done;
        long eta = Math.round(remaining*rate);

        long duplicatesCount = 0;
        for (List<File> files: duplicates.values()) {
            if (files.size() > 1) {
                duplicatesCount++;
            }
        }

        return new Progress(done,-1,remaining, duplicatesCount, eta, null, null);                                       // TODO: add support for remaining stats
    }

    @Override
    public Future<List<List<File>>> findDuplicates(List<File> allFiles) {                                               // TODO: javadoc
        duplicates = new LockableConcurrentHashMap<>();
        this.taskFutures = new LinkedList<>();
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
