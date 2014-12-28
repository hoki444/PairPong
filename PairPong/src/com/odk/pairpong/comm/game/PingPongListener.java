package com.odk.pairpong.comm.game;

import com.odk.pairpong.comm.general.CommFunction;
import com.odk.pairpong.comm.general.MessageListener;

public abstract class PingPongListener implements MessageListener<String> {
    protected CommFunction commFun;
    
    public PingPongListener (CommFunction commFun) {
        this.commFun = commFun;
    }

    @Override
    public Class<String> getTypeClass() {
        return String.class;
    }
}