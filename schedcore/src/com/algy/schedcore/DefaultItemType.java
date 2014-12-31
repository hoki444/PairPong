package com.algy.schedcore;

public final class DefaultItemType extends BaseItemType {
    public final static DefaultItemType instance  = new DefaultItemType ();
    
    private DefaultItemType () {
    }

    @Override
    public BaseItemType duplicate() {
        return instance;
    }

}
