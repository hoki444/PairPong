package com.algy.schedcore.event;

import com.algy.schedcore.BaseItemType;

public interface GameItemEventReceiver <T extends GameEvent, I extends BaseItemType> {
    public void onReceive(T event, I itemType);
}