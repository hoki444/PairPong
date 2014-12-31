package com.algy.schedcore;

import com.algy.schedcore.util.Item;

public class CompMgrSpace extends Item<BaseCompMgr, GameItemSpace> {
    public CompMgrSpace(GameItemSpace owner) {
        super(BaseCompMgr.class, owner);
    }
}