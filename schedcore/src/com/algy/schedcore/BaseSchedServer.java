package com.algy.schedcore;


public abstract class BaseSchedServer extends BaseCompServer implements ISchedComp {
    private Scheduler.Task task = null;

    @Override
    public Scheduler.Task getTask() {
        return task;
    }

    @Override
    public void setTask(Scheduler.Task task) {
        this.task = task;
    }
}
