package com.algy.schedcore;

public class SchedTime {
    public SchedTime(long delta, long deadline, long realDelta, boolean first) {
        super();
        this.delta = delta;
        this.deadline = deadline;
        this.realDelta = realDelta;
        this.first = first;
    }
    public long delta;
    public long deadline;
    public long realDelta;
    public boolean first;
}
