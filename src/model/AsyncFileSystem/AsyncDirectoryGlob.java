package model.AsyncFileSystem;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

// TODO: javadoc
public class AsyncDirectoryGlob implements Callable<List<File>> {

    private static Integer vCPUs = Runtime.getRuntime().availableProcessors();  // TODO: move this to config?
    private final String rootDirectory;

    public AsyncDirectoryGlob(String rootDirectory) {
        this.rootDirectory = rootDirectory;
    }

    @Override
    public List<File> call() throws Exception {

        ConcurrentLinkedQueue<File> toVisit = new ConcurrentLinkedQueue<>();
        LinkedList<Future<File[]>> futures = new LinkedList<>();
        List<File> allFiles = new LinkedList<>();

        try {
            File root = new File(rootDirectory);
            if (!root.isDirectory()) {
                throw new IOException("Root directory is not a directory");
            }
            toVisit.add(root);
        } catch (Exception e) {
            throw new IOException("Cannot read root directory: " + e.getMessage());
        }

        // thread pool to asynchronously read files from disk
        ExecutorService threadPool = Executors.newFixedThreadPool(Math.max(1, vCPUs - 1));

        while(!toVisit.isEmpty() || !futures.isEmpty()) {
            if (!toVisit.isEmpty()) {
                File thisDirectory = toVisit.poll();
                AsyncFileList loader = new AsyncFileList(thisDirectory);
                Future<File[]> future = threadPool.submit(loader);
                futures.add(future);
            } else {
                // Check if any of the futures are ready
                for (int i = 0; i < futures.size(); i++) {
                    Future<File[]> future = futures.get(i);
                    if (future.isDone()) {
                        try {
                            File[] unseenFiles = future.get();
                            for (File file: unseenFiles) {
                                if (file.isDirectory()) {
                                    toVisit.add(file);
                                } else if (file.isFile()) {  // TODO: add check for extension
                                    allFiles.add(file);
                                    System.out.println(file.getName());
                                }
                            }
                        } catch (Exception e) {
                            throw new IOException("Error reading directory");  // TODO: Add better exception msg
                        }
                        futures.remove(i);
                        break;
                    }
                }
            }
        }

        threadPool.shutdownNow();
        return allFiles;
    }

    public static void main(String args[]) {
        AsyncDirectoryGlob a = new AsyncDirectoryGlob("D:\\");
        ExecutorService pool = Executors.newFixedThreadPool(2);
        pool.submit(a);
        pool.shutdown();
    }
}
