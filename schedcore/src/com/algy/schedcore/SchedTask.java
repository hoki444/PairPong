package com.algy.schedcore;

public interface SchedTask {
    void onScheduled(SchedTime time);
    void beginSchedule(TaskController t);
    void endSchedule(TaskController t);
}