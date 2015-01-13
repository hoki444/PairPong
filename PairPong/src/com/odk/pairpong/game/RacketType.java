package com.odk.pairpong.game;

import com.algy.schedcore.BaseItemType;

public class RacketType extends BaseItemType {

    @Override
    public BaseItemType duplicate() {
        return new RacketType();
    }

}
