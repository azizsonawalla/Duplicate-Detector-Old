package model.async.workerPool;

import config.Config;

import java.util.concurrent.*;

public class WorkerPool extends ThreadPoolExecutor {

    private static WorkerPool instance = new WorkerPool();

    public static WorkerPool getInstance() {
        return instance;
    }

    private WorkerPool() {
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
