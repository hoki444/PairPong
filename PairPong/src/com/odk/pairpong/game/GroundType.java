package com.odk.pairpong.game;

import com.algy.schedcore.BaseItemType;

public class GroundType extends BaseItemType {

    @Override
    public BaseItemType duplicate() {
        return new GroundType();
    }

}
