package model.async.threadPool;

import config.Config;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Singleton wrapper for ThreadPoolExecutor to be used across the application for background tasks.
 *
 * Saves memory/computational resources by not having to create/destroy threads for each background task, and allows
 * easy management of parallelism and asynchronous tasks across the application.
 */
public class AppThreadPool extends ThreadPoolExecutor {

    private static AppThreadPool instance = null;

    /**
     * Get the singleton instance of the worker pool
     * @return inner ThreadPoolExecutor instance
     */
    public static AppThreadPool getInstance() {
        if (instance == null) {
            instance = new AppThreadPool();
        }
        return instance;
    }

    /**
     * Initializes inner thread pool based on Config values
     */
    private AppThreadPool() {
        super(
                Config.POOL_SIZE,
                Config.POOL_SIZE,
                0L,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>()
        );
    }
}
