package com.algy.schedcore.event;

import com.algy.schedcore.BaseItemType;

public interface SuperInteractionReceiver <I1 extends BaseItemType, I2 extends BaseItemType> {
    public boolean invokeOnSuper(Class<? super I1> superClassLHS, Class<? super I2> superClassRHS);
}