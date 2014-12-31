package com.algy.schedcore.event;

import com.algy.schedcore.BaseItemType;

public interface SuperItemEventReceiver <I extends BaseItemType> {
    public boolean invokeOnSuper(Class<? super I> superClass);
}
