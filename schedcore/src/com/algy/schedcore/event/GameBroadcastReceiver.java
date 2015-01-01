package com.algy.schedcore.event;

public interface GameBroadcastReceiver <T extends GameBroadcast> {
    public void onReceive(T event);
}
