package com.algy.schedcore;

public interface Context {
    public Scheduler getScheduler();
    public void invokeEvent();
    public void observe();
    public void observeOnce( );
}