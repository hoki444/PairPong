package com.algy.schedcore;

public class SchedTime {
    public SchedTime(long delta, long deadline, long realDelta, boolean first) {
        super();
        this.deltaTime = delta;
        this.deadline = deadline;
        this.realDeltaTime = realDelta;
        this.first = first;
    }
    public long deltaTime;
    public long deadline;
    public long realDeltaTime;
    public boolean first;
}
