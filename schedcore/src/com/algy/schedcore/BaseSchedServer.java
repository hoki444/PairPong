package com.algy.schedcore;


public abstract class BaseSchedServer extends BaseCompServer implements ISchedComp {
    private int taskId = -1;
    @Override
    public int taskId() {
        return taskId;
    }
    

    @Override
    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

}
