package com.odk.pairpong;


import android.os.Bundle;

import com.algy.schedcore.frontend.SceneMgr;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.odk.pairpong.comm.backend.QPairCommFunction;
import com.odk.pairpong.game.MainScene;

public class PairPongBoardActivity extends AndroidApplication {
	private QPairCommFunction commFun;
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

		commFun = new QPairCommFunction("com.odk.pairpongsender");
		commFun.registerReceivers(this);
		commFun.setContext(getApplicationContext());
        initialize(new SceneMgr(new MainScene(commFun)));
    }

    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	commFun.unregisterReceivers(this);
    }
}
