package com.algy.schedcore;


public abstract class BaseSchedMgr extends BaseCompMgr implements StaticSchedMixin {
    private TaskController task = null;

    @Override
    public TaskController getTask() {
        return task;
    }

    @Override
    public void setTask(TaskController task) {
        this.task = task;
    }
}
