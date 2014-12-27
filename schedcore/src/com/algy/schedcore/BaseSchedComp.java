package com.algy.schedcore;

public abstract class BaseSchedComp extends BaseComp implements ISchedComp {
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