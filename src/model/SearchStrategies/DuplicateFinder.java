package model.SearchStrategies;

import model.AsyncFileSystem.AsyncDirectoryGlob;
import model.util.Progress;
import model.util.SearchException;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.File;
import java.util.*;
import java.util.concurrent.*;

public abstract class DuplicateFinder {

    private final String rootDirectory;                          // Root directory with duplicates
    private Future<List<File>> allFilesFuture;                   // Future object for allFiles
    private ConcurrentLinkedQueue<File> allFiles;                // All files in Root directory
    private long startTime;                                      // epoch time for when last search was started
    private ConcurrentHashMap<String, List<File>> duplicates;    // Thread-safe map to store duplicate files

    public DuplicateFinder(String rootDirectory) {
        this.allFiles = new ConcurrentLinkedQueue<>();
        this.rootDirectory = rootDirectory;
        AsyncDirectoryGlob asyncGlob = new AsyncDirectoryGlob(this.rootDirectory);  // TODO: pass valid extensions
        ExecutorService bgThread = Executors.newSingleThreadExecutor();
        this.allFilesFuture = bgThread.submit(asyncGlob);
        bgThread.shutdown();
    }

    /**
     * Start the search for duplicates in the root folder. This method searches for duplicates asynchronously and will
     * return immediately after triggering the search. To get progress stats for the search, call ``getProgress()`` and
     * to interrupt the search call ``stopSearch()``
     * @throws SearchException if there's an error starting the search
     */
    public abstract void startSearch() throws SearchException;

    /**
     * Stops an ongoing search and halts all asynchronous tasks. Does nothing if no search is in progress.
     * @throws SearchException if there's an error stopping the search
     */
    public abstract void stopSearch() throws SearchException;

    /**
     * Get the status of the current search.
     * @return Progress of the current search.
     * @throws SearchException if no search is in progress
     */
    public abstract Progress getProgress() throws SearchException;

    /**
     * Get results from the search.
     * @return a 2-D list where each inner list is a collection of File objects that are suspected duplicates
     * @throws SearchException if search is still ongoing
     */
    public List<List<File>> getResults() throws SearchException {
        if (!searchDone()) {
            throw new SearchException("Search is still in progress. Cannot return results.");
        }
        ArrayList<List<File>> results = new ArrayList<>();
        for (List<File> duplicateSet: duplicates.values()) {
            if (duplicateSet.size() > 1) {
                results.add(duplicateSet);
            }
        }
        return results;
    }

    /**
     * Returns the extension associated with the file without the dot. Returns null if file has no extension
     */
    private static String getFileExtension(File file) {
        String name = file.getName();
        String[] parts = name.split(".");
        if (parts.length > 1) {
            return parts[parts.length-1];
        }
        return null;
    }

    /**
     * Asynchronously reads files from this.remainingFiles and stores duplicate files in this.duplicates
     */
    protected abstract void findDuplicates();

    /**
     * @return true if search is complete, else false
     */
    private boolean searchDone() {
        // TODO
        throw new NotImplementedException();
    }

    public static void main(String args[]) {
        DuplicateFinder d = new DuplicateFinder("D:\\") {
            @Override
            public void startSearch() throws SearchException {

            }

            @Override
            public void stopSearch() throws SearchException {

            }

            @Override
            public Progress getProgress() throws SearchException {
                return null;
            }

            @Override
            protected void findDuplicates() {

            }
        };

    }
}
