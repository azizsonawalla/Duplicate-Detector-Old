package model.searchStrategies;

import model.async.FutureUtil.FutureCollection;
import model.async.lockableDataStructures.LockableConcurrentHashMap;
import model.async.threadPool.AppThreadPool;
import model.util.Progress;
import model.util.SearchException;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Finds duplicate files based on file metadata
 */
public class MetadataDuplicateFinder extends DuplicateFinder {

    public MetadataDuplicateFinder(String rootDirectory) {
        super(rootDirectory);
    }

    @Override
    public void stopSearch() throws SearchException {                                                                   // TODO: implement this
        throw new NotImplementedException();
    }

    @Override
    public Progress getSearchProgress() throws SearchException {                                                        // TODO: implement this
        throw new NotImplementedException();
    }

    @Override
    protected Future<List<List<File>>> findDuplicates(List<File> allFiles) {                                            // TODO: javadoc
        LockableConcurrentHashMap<String, LinkedList<File>> duplicates = new LockableConcurrentHashMap<>();
        LinkedList<Future> taskFutures = new LinkedList<>();

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
