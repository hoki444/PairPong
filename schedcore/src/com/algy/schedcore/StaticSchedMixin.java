package com.algy.schedcore;


public interface StaticSchedMixin extends SchedTask {
    // Aspect from scheduler
    long schedPeriod();
    long schedDelay();
    boolean isPeriodic();

    // Aspect from core
    TaskController getTask();
    void setTask(TaskController task);
}