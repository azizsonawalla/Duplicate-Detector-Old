package util;

import java.util.List;

/**
 * An object to hold progress on a given task
 */
public class Progress {

    private long done;                // number of tasks already done
    private long inProgress;          // number of tasks being done right now
    private long remaining;           // number of tasks yet to check
    private long positives;           // number of tasks that returned positive result
    private long eta;                 // estimated time remaining
    private List<Exception> errors;   // error messages encountered
    private String currentTask;       // name of current task

    /**
     * Create an instance of task progress
     * @param done number of tasks done
     * @param inProgress number of tasks currently being processed
     * @param remaining number of tasks remaining to be processed
     * @param positives number of tasks that returned a positive result
     * @param eta time in milliseconds that remains for all tasks to be completed
     * @param errors error messages encountered so far
     * @param currentTask current task that is being worked on
     */
    public Progress(long done, long inProgress, long remaining, long positives,
                    long eta, List<Exception> errors, String currentTask) {
        this.done = done;
        this.inProgress = inProgress;
        this.positives = positives;
        this.remaining = remaining;
        this.eta = eta;
        this.errors = errors;
        this.currentTask = currentTask;
    }

    public long getDone() {
        return done;
    }

    public void setDone(int done) {
        this.done = done;
    }

    public long getInProgress() {
        return inProgress;
    }

    public void setInProgress(int inProgress) {
        this.inProgress = inProgress;
    }

    public long getRemaining() {
        return remaining;
    }

    public long getPositives() {
        return positives;
    }

    public void setRemaining(int remaining) {
        this.remaining = remaining;
    }

    public long getEta() {
        return eta;
    }

    public void setEta(long eta) {
        this.eta = eta;
    }

    public List<Exception> getErrors() {
        return errors;
    }

    public void setErrors(List<Exception> errs) {
        this.errors = errs;
    }

    public String getCurrentTask() {
        return currentTask;
    }

    public void setCurrentTask(String currentTask) {
        this.currentTask = currentTask;
    }
}
