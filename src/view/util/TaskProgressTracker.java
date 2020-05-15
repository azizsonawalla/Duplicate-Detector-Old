package view.util;

import javafx.application.Platform;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeoutException;

public class TaskProgressTracker implements Runnable {                                                                   // TODO: javadoc

    private final Long waitPollInterval;
    private final Long waitTimeout;
    private final Long taskPollInterval;
    private final Long taskTimeout;
    private final Callable<Boolean> isStarted;
    private final Callable<Boolean> isDone;
    private final Runnable update;
    private final Runnable onComplete;
    private boolean interrupted = false;

    public TaskProgressTracker (
            long waitPollInterval,
            long waitTimeout,
            long taskPollInterval,
            long taskTimeout,
            Callable<Boolean> isStarted,
            Callable<Boolean> isDone,
            Runnable update,
            Runnable onComplete
    ) {                                                                            // TODO: javadoc

        this.waitPollInterval = waitPollInterval;
        this.waitTimeout = waitTimeout;
        this.taskPollInterval = taskPollInterval;
        this.taskTimeout = taskTimeout;
        this.isStarted = isStarted;
        this.isDone = isDone;
        this.update = update;
        this.onComplete = onComplete;
    }

    @Override
    public void run() {
        try {
            pollAndUpdate(waitTimeout, waitPollInterval, isStarted, null);
            if (interrupted) { return; }
            pollAndUpdate(taskTimeout, taskPollInterval, isDone, update);
        } catch (Exception e) {
            e.printStackTrace();                                                                                    // TODO: error handling
        }

        if (interrupted) return;
        Platform.runLater(onComplete);
    }

    public void stop() {
        interrupted = true;
    }

    private void pollAndUpdate(Long timeout, Long pollInterval,
                               Callable<Boolean> isDone, Runnable update) throws Exception {                        // TODO: javadoc
        Long start = System.currentTimeMillis();
        while (!isDone.call() && !interrupted) {
            Long elapsed = System.currentTimeMillis() - start;
            if (elapsed > timeout) {
                throw new TimeoutException("Timed out while waiting");
            }
            if (update != null) {
                Platform.runLater(update);
            }
            Thread.sleep(pollInterval);
        }
        if (update != null) {
            Platform.runLater(update);
        }
    }
}