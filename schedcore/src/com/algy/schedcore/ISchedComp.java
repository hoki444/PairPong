package com.algy.schedcore;

public interface ISchedComp extends ISchedTask {
    // Aspect from scheduler
    long schedPeriod();
    long schedOffset();
    
    // Aspect from core
    int taskId();
    void setTaskId(int taskId);
}