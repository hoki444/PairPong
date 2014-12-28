package com.odk.pairpong.comm.game;

import com.odk.pairpong.comm.general.CommFunction;

public class PingListener extends PingPongListener {
    public PingListener(CommFunction commFun) {
        super(commFun);
    }

    @Override
    public String getTypeName() {
        return CommConstants.TYPE_PING;
    }

    @Override
    public void onReceive(String obj) {
        commFun.sendMessage(CommConstants.TYPE_PONG, obj, null);
    }
}
