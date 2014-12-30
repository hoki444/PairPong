package com.odk.pairpong.game;

import com.algy.schedcore.BaseComp;
import com.algy.schedcore.IComp;

public class BallStateComp extends BaseComp {
    public boolean isRollingOnGround;
    
    public BallStateComp (boolean isRollingOnGround) {
        this.isRollingOnGround = isRollingOnGround;
    }

    @Override
    public IComp duplicate() {
        return new BallStateComp(isRollingOnGround);
    }

}
