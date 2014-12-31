package com.algy.schedcore.event;

import com.algy.schedcore.BaseItemType;

public interface GameInteractionReceiver <T extends GameEvent, I extends BaseItemType, I2 extends BaseItemType> {
    public void onReceive(T event, I lhs, I2 rhs, SuperInteractionReceiver<I, I2> superReceiver);
}