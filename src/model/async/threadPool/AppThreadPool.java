package model.async.threadPool;

import config.Config;

import java.util.concurrent.*;

public class AppThreadPool extends ThreadPoolExecutor {

    private static AppThreadPool instance = null;

    public static AppThreadPool getInstance() {
        if (instance == null) {
            instance = new AppThreadPool();
        }
        return instance;
    }

    private AppThreadPool() {
        super(
                Config.POOL_SIZE,
                Config.POOL_SIZE,
                0L,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>()
        );
    }

    // TODO: Add a way to automatically close pool on closing application
}
