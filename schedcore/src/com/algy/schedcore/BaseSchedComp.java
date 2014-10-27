package com.algy.schedcore;

public abstract class BaseSchedComp extends BaseComp implements ISchedComp {
    private int taskId;

    @Override
    public int taskId() {
        return taskId;
    }

    @Override
    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }
}
