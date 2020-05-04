package model.searchStrategies;

import model.async.FutureUtil.FutureCollection;
import model.async.threadPool.AppThreadPool;
import model.util.Progress;
import model.util.SearchException;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.File;
import java.util.*;
import java.util.concurrent.*;

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
    protected Future<List<List<File>>> findDuplicates(List<File> allFiles) {                                            // TODO: implement this
        ConcurrentHashMap<String, List<File>> duplicates = new ConcurrentHashMap<>();
        List<Future> taskFutures = new LinkedList<>();

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

                List<List<File>> filtered = new LinkedList<>();
                for (List<File> duplicateSet: duplicates.values()) {
                    if (duplicateSet.size() > 1) {
                        filtered.add(duplicateSet);
                    }
                }

                setSearchDone();
                return filtered;
            }
        };
    }

    static class MetadataHasher implements Runnable {                                                                   // TODO: Javadoc

        private final File file;
        private final ConcurrentHashMap<String, List<File>> duplicates;

        MetadataHasher(File file, ConcurrentHashMap<String, List<File>> duplicates) {
            this.file = file;
            this.duplicates = duplicates;
        }

        @Override
        public void run() {
            String key = getNameSizeHash(this.file);
            if (this.duplicates.containsKey(key)) {                                                                     // TODO: potential issue if key is added b/w check and next line, need some kind of lock?
                this.duplicates.get(key).add(this.file);
            } else {
                System.out.println(duplicates.toString());                                                              // TODO: Remove this
                List<File> newFileSet = Arrays.asList(this.file);
                this.duplicates.put(key, newFileSet);
            }
        }

        /**
         * Creates a hashcode for the file using its name and size
         */
        private static String getNameSizeHash(File file) {
            String name = file.getName();
            long size = file.length();
            return String.format("%s%s", name, Long.toString(size));
        }
    }
}
