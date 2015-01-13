package com.odk.pairpong.game;

import com.algy.schedcore.BaseItemType;

public class BallType extends BaseItemType {
    public boolean isRolling;

    @Override
    public BaseItemType duplicate() {
        return new BallType();
    }

}
