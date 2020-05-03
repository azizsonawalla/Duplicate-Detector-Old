package model.searchStrategies;

import model.async.threadPool.AppThreadPool;
import model.util.Progress;
import model.util.SearchException;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

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
    protected Future<Map<String, List<File>>> findDuplicates(List<File> allFiles) {                                     // TODO: implement this
        ConcurrentHashMap<String, List<File>> duplicates = new ConcurrentHashMap<>();
        for (File file: allFiles) {
            String key = getNameSizeHash(file);
            if (duplicates.containsKey(key)) {
                duplicates.get(key).add(file);
            } else {
                List<File> newFileSet = Arrays.asList(file);
                duplicates.put(key, newFileSet);
            }
        }
        setSearchDone();
        throw new NotImplementedException();                                                                            // TODO: filter values of size 1
    }

    /**
     * Creates a hashcode for the file using its name and size
     */
    private static String getNameSizeHash(File file) {
        String name = file.getName();
        long size = file.length();
        return String.format("%s%s", name, Long.toString(size));
    }

    class HashAndStore implements Runnable {                                                                            // TODO: Javadoc

        private final File file;
        private final ConcurrentHashMap<String, List<File>> duplicates;

        public HashAndStore(File file, ConcurrentHashMap<String, List<File>> duplicates) {
            this.file = file;
            this.duplicates = duplicates;
        }

        @Override
        public void run() {
            String key = getNameSizeHash(this.file);
            if (this.duplicates.containsKey(key)) {                                                                     // TODO: potential issue if key is added b/w check and next line, need some kind of lock?
                this.duplicates.get(key).add(this.file);
            } else {
                List<File> newFileSet = Arrays.asList(this.file);
                this.duplicates.put(key, newFileSet);
            }
        }
    }
}
