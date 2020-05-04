package model.util;

import java.util.List;

/**
 * An object to hold progress on a given task
 */
public class Progress {

    private int done;              // number of files already done
    private int inProgress;        // number of files being done right now
    private int remaining;         // number of files yet to check
    private long eta;              // estimated time remaining
    private int errors;            // number of errors so far
    private List<String> errMsgs;  // error messages encountered
    private String currentTask;    // name of current task

    public Progress(int done, int inProgress, int remaining, long eta,
                    int errors, List<String> errMsgs, String currentTask) {
        this.done = done;
        this.inProgress = inProgress;
        this.remaining = remaining;
        this.eta = eta;
        this.errors = errors;
        this.errMsgs = errMsgs;
        this.currentTask = currentTask;
    }

    public int getDone() {
        return done;
    }

    public void setDone(int done) {
        this.done = done;
    }

    public int getInProgress() {
        return inProgress;
    }

    public void setInProgress(int inProgress) {
        this.inProgress = inProgress;
    }

    public int getRemaining() {
        return remaining;
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

    public int getErrors() {
        return errors;
    }

    public void setErrors(int errors) {
        this.errors = errors;
    }

    public List<String> getErrMsgs() {
        return errMsgs;
    }

    public void setErrMsgs(List<String> errMsgs) {
        this.errMsgs = errMsgs;
    }

    public String getCurrentTask() {
        return currentTask;
    }

    public void setCurrentTask(String currentTask) {
        this.currentTask = currentTask;
    }
}
