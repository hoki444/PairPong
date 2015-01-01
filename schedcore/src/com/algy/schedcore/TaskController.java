package com.algy.schedcore;

import com.algy.schedcore.Scheduler.JobStatus;
import com.algy.schedcore.Scheduler.TaskType;

public interface TaskController {
    public int getTaskId();
    public TaskType getTaskType();
    public JobStatus getStatus();

    public boolean active();
    public boolean suspended();

    public void kill();
    public boolean suspend();
    public boolean resume();
    
    public boolean aperiodicDone ();

    public void setRepr(Object reprObj);
    public Object repr();
    
    public float getAverageExecTime();
}