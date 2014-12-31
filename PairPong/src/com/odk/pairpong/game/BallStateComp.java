package com.odk.pairpong.game;

import com.algy.schedcore.BaseComp;

public class BallStateComp extends BaseComp {
    public boolean isRollingOnGround;
    
    public BallStateComp (boolean isRollingOnGround) {
        this.isRollingOnGround = isRollingOnGround;
    }

    @Override
    public BaseComp duplicate() {
        return new BallStateComp(isRollingOnGround);
    }

}
