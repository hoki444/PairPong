package com.algy.schedcore;

public interface SchedTask {
    void onScheduled(SchedTime time);
    void beginSchedule();
    void endSchedule();
}