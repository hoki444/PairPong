package com.algy.schedcore.event;

import com.algy.schedcore.BaseItemType;

public interface ItemTypeAggregate {
    public <T extends BaseItemType> Iterable<T> fetchType(Class<T> itemTypeClass);
}
