package com.algy.schedcore;

import com.algy.schedcore.event.GameEventMgr;

public interface Context {
    public Scheduler getScheduler();
    public GameEventMgr getGameEventMgr();
    public GameItemSpace getItemSpace();
}