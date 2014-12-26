package com.odk.pairpong.game;

import com.algy.schedcore.frontend.SceneMgr;
import com.odk.pairpong.comm.CommConstants;
import com.odk.pairpong.comm.general.CommFunction;
import com.odk.pairpong.comm.general.MessageListener;

public class CeaseGameListener implements MessageListener<Object> {
    private CommFunction commFun;
    public CeaseGameListener (CommFunction commFun) {
        this.commFun = commFun;
    }

    @Override
    public String getTypeName() {
        return CommConstants.TYPE_CEASE_GAME;
    }

    @Override
    public Class<Object> getTypeClass() {
        return Object.class;
    }

    @Override
    public void onReceive(Object obj) {
        SceneMgr.switchScene(new MainScene(commFun));
    }
}
