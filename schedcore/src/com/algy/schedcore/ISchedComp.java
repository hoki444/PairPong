package com.algy.schedcore;

import com.algy.schedcore.Scheduler.Task;

public interface ISchedComp extends SchedTask {
    // Aspect from scheduler
    long schedPeriod();
    long schedOffset();
    
    // Aspect from core
    Task getTask();
    void setTask(Task task);
}