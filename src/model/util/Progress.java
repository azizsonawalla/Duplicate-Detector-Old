package model.util;

public class Progress {

    public int checked;     // number of files already checked
    public int current;     // number of files being checked right now
    public int remaining;   // number of files yet to check
    public long eta;        // estimated time remaining

    // TODO: Add errors
    // TODO: Add current task name

    public Progress(int checked, int current, int remaining, long eta) {
        this.checked = checked;
        this.current = current;
        this.remaining = remaining;
        this.eta = eta;
    }
}
