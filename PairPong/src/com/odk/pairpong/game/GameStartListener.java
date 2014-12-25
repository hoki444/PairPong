package com.odk.pairpong.game;

import com.algy.schedcore.frontend.SceneMgr;
import com.odk.pairpong.comm.CommConstants;
import com.odk.pairpong.comm.CommOption;
import com.odk.pairpong.comm.general.CommFunction;
import com.odk.pairpong.comm.general.MessageListener;

public class GameStartListener implements MessageListener<CommOption> {
    private CommFunction commFun;
    public GameStartListener (CommFunction commFun) {
        this.commFun = commFun;
    }

    @Override
    public String getTypeName() {
        return CommConstants.TYPE_START_GAME;
    }

    @Override
    public Class<CommOption> getTypeClass() {
        return CommOption.class;
    }

    @Override
    public void onReceive(CommOption obj) {
        SceneMgr.switchScene(new GameScene(commFun, obj));
    }
}