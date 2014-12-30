package com.odk.pairpong.game;

import com.odk.pairpong.comm.game.CommConstants;
import com.odk.pairpong.comm.general.MessageListener;

public class EndAppListener implements MessageListener<Object> {
    private Runnable quitter;
    public EndAppListener (Runnable quitter) {
        this.quitter = quitter;
    }
    @Override
    public String getTypeName() {
        return CommConstants.TYPE_END_APP;
    }

    @Override
    public Class<Object> getTypeClass() {
        return null;
    }

    @Override
    public void onReceive(Object obj) {
        System.out.println("Good bye, cruel world...");
        quitter.run();
    }

}
