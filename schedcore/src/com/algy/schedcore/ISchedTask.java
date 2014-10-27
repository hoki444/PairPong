package com.algy.schedcore;

public interface ISchedTask {
    void schedule(SchedTime time);
    void beginSchedule();
    void endSchedule();
}