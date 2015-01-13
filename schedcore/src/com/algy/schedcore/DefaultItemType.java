package com.algy.schedcore;

public final class DefaultItemType extends BaseItemType {
    public DefaultItemType () {
    }

    @Override
    public BaseItemType duplicate() {
        return new DefaultItemType();
    }

}
